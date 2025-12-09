FROM eclipse-temurin:17-jre-alpine

# Instalar bash para o script de execução
RUN apk add --no-cache bash

# Criar diretório de trabalho
WORKDIR /app

# Copiar o JAR da aplicação
COPY target/cli-loterias-caixa-1.5-SNAPSHOT.jar /app/cli-loterias-caixa. jar

# Copiar o script de execução
COPY run-all-loterias.sh /app/run-all-loterias.sh

# Dar permissão de execução ao script
RUN chmod +x /app/run-all-loterias.sh

# Definir o script como entrypoint
ENTRYPOINT ["/app/run-all-loterias.sh"]