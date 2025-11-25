# =============================
# 1. Imagem base
# =============================
FROM eclipse-temurin:17-jdk

# =============================
# 2. Diretório da aplicação
# =============================
WORKDIR /app

# =============================
# 3. Copiar JAR gerado pelo Maven
# =============================
COPY target/eventmaster-1.0.0.jar /app/app.jar

# =============================
# 4. Copiar o agente OpenTelemetry
# =============================
COPY opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar

# =============================
# 5. Variáveis do OpenTelemetry
# =============================
ENV OTEL_SERVICE_NAME=eventmaster
ENV OTEL_EXPORTER_OTLP_ENDPOINT=http://sentinel-otel:4318
ENV OTEL_EXPORTER_OTLP_PROTOCOL=http/protobuf

# Ativar o agente Java automaticamente
ENV JAVA_TOOL_OPTIONS="-javaagent:/app/opentelemetry-javaagent.jar"

# =============================
# 6. Porta
# =============================
EXPOSE 8080

# =============================
# 7. Executar aplicação
# =============================
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
