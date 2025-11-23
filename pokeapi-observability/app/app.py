from dataclasses import asdict
import time
from fastapi import  FastAPI, HTTPException,  Request
import logging
import httpx
from contextlib import asynccontextmanager
from sqlalchemy.ext.asyncio import AsyncSession, create_async_engine
from sqlalchemy.orm import sessionmaker, registry
from sqlalchemy import Column, Integer, String, Table

from opentelemetry.sdk.metrics import MeterProvider
from opentelemetry.metrics import get_meter_provider
from opentelemetry.sdk.metrics.export import PeriodicExportingMetricReader
from opentelemetry.exporter.otlp.proto.grpc.metric_exporter import OTLPMetricExporter
from opentelemetry.instrumentation.system_metrics import SystemMetricsInstrumentor
from opentelemetry.instrumentation.requests import RequestsInstrumentor
from opentelemetry.instrumentation.fastapi import FastAPIInstrumentor
from opentelemetry import trace
from opentelemetry.instrumentation.asgi import OpenTelemetryMiddleware
from opentelemetry.metrics import get_meter_provider
from opentelemetry.sdk.trace import TracerProvider
from opentelemetry.sdk.trace.export import BatchSpanProcessor
from opentelemetry.exporter.otlp.proto.grpc.trace_exporter import OTLPSpanExporter
from opentelemetry.instrumentation.sqlalchemy import SQLAlchemyInstrumentor
from opentelemetry.instrumentation.asyncpg import AsyncPGInstrumentor



logger = logging.getLogger()
logger.setLevel(logging.INFO)
logger.addHandler(logging.StreamHandler())

# Configuração do exportador de métricas
# metric_exporter = OTLPMetricExporter(endpoint="http://API-02_otel_collector:4317", insecure=True)
metric_exporter = OTLPMetricExporter(endpoint="http://sentinel-otel:4317", insecure=True)
metric_reader = PeriodicExportingMetricReader(metric_exporter)
provider = MeterProvider(metric_readers=[metric_reader])
meter = get_meter_provider().get_meter(__name__)

trace.set_tracer_provider(TracerProvider())
trace.get_tracer_provider().add_span_processor(
    BatchSpanProcessor(OTLPSpanExporter(endpoint="http://sentinel-otel:4317", insecure=True))
    # BatchSpanProcessor(OTLPSpanExporter(endpoint="http://API-02_otel_collector:4317", insecure=True))
)

# Instrumentação de métricas de runtime e sistema
SystemMetricsInstrumentor().instrument()
# Instrumentar requisições externas (exemplo: chamadas à PokéAPI)
RequestsInstrumentor().instrument()
AsyncPGInstrumentor().instrument()

DATABASE_URL = "postgresql+asyncpg://pokeob:pokeob@database:5433/pokeob"
POKEAPI_URL = "https://pokeapi.co/api/v2/pokemon/"

# Configuração do SQLAlchemy
reg = registry()
engine = create_async_engine(DATABASE_URL, echo=True, future=True)
AsyncSessionLocal = sessionmaker(
    bind=engine,
    class_=AsyncSession,
    expire_on_commit=False
)

pokemon_table = Table(
    "pokemons",
    reg.metadata,
    Column("id", Integer, primary_key=True),
    Column("name", String, nullable=False),
    Column("types", String, nullable=False),
    Column("sprite", String, nullable=False),
)

@reg.mapped_as_dataclass
class FavPoke:
    __tablename__ = "pokemons"
    id: int
    name: str
    types: str  
    sprite: str


@asynccontextmanager
async def lifespan(app):
    logger.info("🚀 Aplicação iniciando...")
    async with engine.begin() as conn:
        await conn.run_sync(reg.metadata.create_all)
    yield
    logger.info("🛑 Aplicação encerrando...")
    async with engine.begin() as conn:
        await conn.run_sync(reg.metadata.drop_all)
    await engine.dispose()
    

app = FastAPI(lifespan=lifespan)

# Adicionar o middleware de OpenTelemetry para capturar métricas HTTP
app.add_middleware(OpenTelemetryMiddleware)
FastAPIInstrumentor().instrument_app(app, tracer_provider=trace.get_tracer_provider())
SQLAlchemyInstrumentor().instrument(
    engine=engine,
    enable_commenter=True,
    enable_db_statement=True,
    enable_connection_attributes=True
)


@app.middleware("http")
async def db_tracker(request: Request, call_next):
    if "/pokemon/" in str(request.url):
        start_time = time.time()
        try:
            response = await call_next(request)
            duration = time.time() - start_time
            
            # Métricas essenciais
            meter = get_meter_provider().get_meter(__name__)
            meter.create_histogram(
                "db.query.duration",
                unit="s",
                description="Query execution time"
            ).record(duration, {"operation": "write" if request.method == "POST" else "read"})
            
            return response
        except Exception as e:
            raise
    return await call_next(request)

# meter = get_meter_provider().get_meter("http.server")

# @app.middleware("http")
# async def status_code_capture(request: Request, call_next):
#     span = trace.get_current_span()
#     try:
#         response = await call_next(request)
        
#         if span.is_recording():
#             span.set_attributes({
#                 "http.response.status_code": response.status_code,
#                 "http.route": request.url.path,
#                 "http.method": request.method,
#                 "http.host": request.url.hostname,
#                 "http.scheme": request.url.scheme
#             })
        
#         return response
        
#     except HTTPException as e:
#         if span.is_recording():
#             span.set_attributes({
#                 "http.status_code": e.status_code,
#                 "error": True,
#                 "error.message": str(e.detail)
#             })
#         raise

# @app.middleware("http")
# async def db_metrics_middleware(request: Request, call_next):
#     meter = metrics.get_meter(__name__)
#     db_call_counter = meter.create_counter(
#         "db.calls.count",
#         description="Count of database calls"
#     )
    
#     try:
#         response = await call_next(request)
#         # Incrementa o contador para cada operação no banco
#         db_call_counter.add(1, attributes={"route": request.url.path})
#         return response
#     except Exception as e:
#         db_call_counter.add(1, attributes={
#             "route": request.url.path,
#             "error": True
#         })
#         raise
    
    
######################################################################################################################################################################
######################################################################################################################################################################


@app.get("/ping")
def ping():
    logger.info("pong")
    return {"ping": "pong"}

@app.get("/pokemon/{id}")
async def get_pokemon(id: int):
    """ Busca um Pokémon na PokéAPI pelo ID """
    logger.info(f"Iniciando busca pelo Pokémon com ID: {id}")

    if id < 1 or id > 300:
        logger.warning(f"ID {id} fora do intervalo permitido (1-300)")
        raise HTTPException(status_code=400, detail="ID fora do intervalo permitido (1-300)")

    try:
        async with httpx.AsyncClient() as client:
            response = await client.get(f"{POKEAPI_URL}{id}")

        if response.status_code != 200:
            logger.error(f"Falha ao buscar Pokémon com ID {id}, resposta {response.status_code}")
            raise HTTPException(status_code=404, detail="Pokémon não encontrado")

        data = response.json()
        logger.info(f"Pokémon encontrado: {data['name']}, ID: {data['id']}")
        
        return {
            "id": data["id"],
            "name": data["name"],
            "types": [t["type"]["name"] for t in data["types"]],
            "sprite": data["sprites"]["front_default"]
        }

    except httpx.RequestError as e:
        logger.error(f"Erro na requisição HTTP para Pokémon ID {id}: {e}")
        raise HTTPException(status_code=500, detail="Erro ao acessar a PokéAPI")


@app.post("/pokemon/{id}")
async def save_pokemon(id: int):
    logger.info(f"Iniciando processo de salvar Pokémon com ID: {id}")
    
    try:
        pokemon_data = await get_pokemon(id)  # Usando o httpx para requisição assíncrona
        logger.info(f"Pokémon {pokemon_data['name']} encontrado. Salvando no banco de dados.")

        async with AsyncSessionLocal() as session:
            async with session.begin():
                pokemon = FavPoke(
                    id=pokemon_data["id"],
                    name=pokemon_data["name"],
                    types=",".join(pokemon_data["types"]),
                    sprite=pokemon_data["sprite"]
                )
                session.add(pokemon)

            await session.commit()
            logger.info(f"Pokémon {pokemon.name} salvo no DB com sucesso!")

        return asdict(pokemon)

    except HTTPException as e:
        logger.error(f"Erro ao buscar Pokémon com ID {id}: {e.detail}")
        raise

    except Exception as e:
        logger.error(f"Erro ao salvar Pokémon com ID {id} no DB: {e}")
        raise HTTPException(status_code=500, detail="Erro ao salvar Pokémon no banco de dados")