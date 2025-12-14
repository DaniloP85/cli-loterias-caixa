FROM maven:3.8.1-openjdk-17-slim AS builder

# Instalar ferramentas necessárias
# RUN apk add --no-cache bash openssl ca-certificates

# Criar diretório de trabalho
WORKDIR /app

# Copiar arquivos do Maven primeiro (melhor uso de cache)
COPY pom.xml .
COPY src ./src

# Buildar a aplicação
RUN mvn clean package -DskipTests

# Copiar o JAR da aplicação
COPY target/*.jar /app/loterias-caixa.jar

# Copiar scripts
COPY run-all-loterias.sh /app/run-all-loterias.sh
COPY install-caixa-cert.sh /app/install-caixa-cert.sh

# Dar permissão de execução aos scripts
RUN chmod +x /app/run-all-loterias.sh /app/install-caixa-cert.sh

# Baixar e instalar o certificado da Caixa
RUN /app/install-caixa-cert.sh

# Definir o script como entrypoint
ENTRYPOINT ["/app/run-all-loterias.sh"]