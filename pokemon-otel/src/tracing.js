'use strict';

const { NodeSDK } = require('@opentelemetry/sdk-node');
const { getNodeAutoInstrumentations } = require('@opentelemetry/auto-instrumentations-node');
const { OTLPTraceExporter } = require('@opentelemetry/exporter-trace-otlp-http');
const { PrometheusExporter } = require('@opentelemetry/exporter-prometheus');
const { Resource } = require('@opentelemetry/resources');
const { TraceIdRatioBasedSampler } = require('@opentelemetry/sdk-trace-base');

const {
  SEMRESATTRS_SERVICE_NAME,
  SEMRESATTRS_SERVICE_INSTANCE_ID,
  HOST_NAME,
  SEMRESATTRS_SERVICE_VERSION
} = require('@opentelemetry/semantic-conventions');

const os = require('os');


// ============================================================
// 1. Exportador de TRACES para OTLP — HTTP/PROTOBUF 
// ============================================================
const traceExporter = new OTLPTraceExporter({
  // O Compose já define OTEL_EXPORTER_OTLP_TRACES_ENDPOINT
  url: process.env.OTEL_EXPORTER_OTLP_TRACES_ENDPOINT
    || `${process.env.OTEL_EXPORTER_OTLP_ENDPOINT}/v1/traces`,

  timeoutMillis: 15000,
  concurrencyLimit: 10,
});


// ============================================================
// 2. Prometheus Exporter para MÉTRICAS DO NODE.JS
// ============================================================
const prometheusExporter = new PrometheusExporter(
  {
    port: 9464,
    endpoint: '/metrics'
  },
  () => {
    console.log('Prometheus scrape endpoint disponível em: http://localhost:9464/metrics');
  }
);


// ============================================================
// 3. Recursos OTEL — IDENTIDADE DO SERVIÇO
// ============================================================
const resource = Resource.default().merge(
  new Resource({
    [SEMRESATTRS_SERVICE_NAME]: process.env.OTEL_SERVICE_NAME || 'pokemon-api',
    [SEMRESATTRS_SERVICE_INSTANCE_ID]: os.hostname(),
    [HOST_NAME]: os.hostname(),
    [SEMRESATTRS_SERVICE_VERSION]: process.env.npm_package_version || '1.0.0',

    // CUSTOM ATTRS (útil no Grafana / Loki)
    app: 'pokemon-api',
    environment: process.env.NODE_ENV || 'development',
  })
);


// ============================================================
// 4. Construção do SDK com sampling + instrumentações
// ============================================================
const sdk = new NodeSDK({
  resource,

  traceExporter,

  metricReader: prometheusExporter,

  sampler: new TraceIdRatioBasedSampler(1.0), // 100% sampling

  instrumentations: getNodeAutoInstrumentations({
    '@opentelemetry/instrumentation-fs': { enabled: false },
    '@opentelemetry/instrumentation-dns': { enabled: false },
    '@opentelemetry/instrumentation-express': { enabled: true },
    '@opentelemetry/instrumentation-http': { enabled: true },
  }),
});


// ============================================================
// 5. Inicialização segura
// ============================================================
async function initializeTracing() {
  try {
    await sdk.start();
    console.log('OpenTelemetry (Node.js) inicializado com sucesso.');
  } catch (error) {
    console.error('Erro ao inicializar OpenTelemetry:', error);
    process.exit(1);
  }
}


// ============================================================
// 6. Finalização segura — SIGTERM / SIGINT
// ============================================================
async function shutdownTracing() {
  try {
    await sdk.shutdown();
    console.log('OpenTelemetry finalizado corretamente.');
  } catch (error) {
    console.error('Erro ao finalizar OpenTelemetry:', error);
  } finally {
    process.exit(0);
  }
}

initializeTracing();

process.on('SIGTERM', shutdownTracing);
process.on('SIGINT', shutdownTracing);
