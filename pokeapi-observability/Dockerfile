# Usa uma imagem base do Python
FROM python:3.13-slim

# Diretório de trabalho
WORKDIR /app

# Copia arquivos do Poetry
COPY pyproject.toml poetry.lock* ./

# Instala o Poetry e ajusta o ambiente
RUN pip install poetry && \
    poetry config virtualenvs.create false 

# Instala dependências do projeto
RUN poetry install --with dev --no-root

# Instala dependências de Observabilidade (OpenTelemetry + Instrumentações)
RUN opentelemetry-bootstrap -a install && \
    pip install opentelemetry-instrumentation-fastapi \
                opentelemetry-instrumentation-httpx

# Copia o restante da aplicação
COPY . .

# Porta exposta
EXPOSE 8001

# Inicia a aplicação com instrumentação automática do OpenTelemetry
CMD ["opentelemetry-instrument", "uvicorn", "app.app:app", "--host", "0.0.0.0", "--port", "8001"]
