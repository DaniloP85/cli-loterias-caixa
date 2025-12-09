#!/bin/bash

# Script para baixar e instalar o certificado SSL da Caixa no Java
# Autor: DaniloP85
# Data:  2025-12-09

set -e

echo "=========================================="
echo "Instalando certificado SSL da Caixa"
echo "=========================================="

# Configurações
CAIXA_HOST="servicebus2.caixa.gov.br"
CAIXA_PORT="443"
CERT_ALIAS="caixa-gov-br"
CERT_FILE="/tmp/caixa.crt"

# Localizar o cacerts do Java
JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
CACERTS="$JAVA_HOME/lib/security/cacerts"

echo "Java Home: $JAVA_HOME"
echo "Cacerts:  $CACERTS"
echo ""

# Verificar se o cacerts existe
if [ ! -f "$CACERTS" ]; then
    echo "❌ Erro: cacerts não encontrado em $CACERTS"
    exit 1
fi

# Fazer backup do cacerts original
echo "📦 Criando backup do cacerts..."
cp "$CACERTS" "$CACERTS.backup"

# Baixar o certificado do servidor da Caixa
echo "🔐 Baixando certificado de $CAIXA_HOST..."
echo -n | openssl s_client -connect $CAIXA_HOST:$CAIXA_PORT -servername $CAIXA_HOST 2>/dev/null | \
    openssl x509 -outform PEM > $CERT_FILE

# Verificar se o certificado foi baixado
if [ ! -s "$CERT_FILE" ]; then
    echo "❌ Erro ao baixar o certificado"
    exit 1
fi

echo "✓ Certificado baixado com sucesso"
echo ""

# Exibir informações do certificado
echo "📄 Informações do certificado:"
echo "----------------------------------------"
openssl x509 -in $CERT_FILE -noout -subject -issuer -dates
echo "----------------------------------------"
echo ""

# Remover certificado antigo se existir
echo "🗑️  Removendo certificado antigo (se existir)..."
keytool -delete -alias $CERT_ALIAS -keystore $CACERTS -storepass changeit 2>/dev/null || true

# Importar o certificado no keystore do Java
echo "📥 Importando certificado no keystore do Java..."
keytool -importcert \
    -alias $CERT_ALIAS \
    -file $CERT_FILE \
    -keystore $CACERTS \
    -storepass changeit \
    -noprompt

# Verificar se o certificado foi instalado
echo ""
echo "✅ Verificando instalação..."
if keytool -list -alias $CERT_ALIAS -keystore $CACERTS -storepass changeit > /dev/null 2>&1; then
    echo "✓ Certificado instalado com sucesso!"
else
    echo "❌ Erro:  Certificado não foi instalado corretamente"
    exit 1
fi

# Limpar arquivo temporário
rm -f $CERT_FILE

echo ""
echo "=========================================="
echo "Instalação do certificado concluída!"
echo "=========================================="
echo ""

# Listar todos os certificados relacionados à Caixa
echo "📋 Certificados da Caixa instalados:"
keytool -list -keystore $CACERTS -storepass changeit 2>/dev/null | grep -i caixa || echo "Nenhum certificado com 'caixa' no alias"
echo ""

exit 0