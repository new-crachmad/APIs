# =============================
# Stage 1 - Build
# =============================
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copia apenas o pom para baixar dependências no cache
COPY pom.xml .

# Baixa dependências (cache)
RUN mvn dependency:go-offline

# Copia o código-fonte
COPY src ./src

# Gera o JAR da aplicação
RUN mvn clean package -DskipTests


# =============================
# Stage 2 - Runtime
# =============================
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copia o JAR final gerado
COPY --from=build /app/target/*.jar /app/app.jar


# =============================
# OpenTelemetry Java Agent
# =============================
# Versão estável e existente!
ARG OTEL_AGENT_VERSION=2.4.0

RUN apt-get update && apt-get install -y curl && apt-get clean

RUN curl -L -f -o /app/opentelemetry-javaagent.jar \
    https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_AGENT_VERSION}/opentelemetry-javaagent.jar


# =============================
# Variáveis OTEL
# =============================
ENV JAVA_TOOL_OPTIONS="-javaagent:/app/opentelemetry-javaagent.jar"


# =============================
# Porta da aplicação
# =============================
EXPOSE 8080


# =============================
# Execução
# =============================
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
