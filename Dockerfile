FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copiar arquivos do Maven primeiro (melhor uso de cache)
COPY pom.xml .
COPY src ./src

# Buildar a aplicação (WAR executável)
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre AS runtime

WORKDIR /app

# openssl é usado pelo install-caixa-cert.sh para baixar o certificado
RUN apt-get update \
    && apt-get install -y --no-install-recommends openssl \
    && rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/target/loterias-caixa.war /app/loterias-caixa.war

# Baixar e instalar o certificado da Caixa no truststore da JVM
COPY install-caixa-cert.sh /app/install-caixa-cert.sh
RUN chmod +x /app/install-caixa-cert.sh && /app/install-caixa-cert.sh

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/loterias-caixa.war"]
