#!/bin/bash

# Script para executar o download de todas as loterias da Caixa
# Autor: DaniloP85
# Data:  2025-12-08

echo "=========================================="
echo "Iniciando download de todas as loterias"
echo "Data: $(date '+%Y-%m-%d %H:%M:%S')"
echo "=========================================="
echo ""

# Array com todas as loterias da Caixa Econômica Federal
LOTERIAS=(
    "megasena"
    "quina"
    "lotofacil"
    "lotomania"
    "timemania"
    "duplasena"
    "federal"
    "loteca"
    "diadesorte"
    "supersete"
)

# Contadores
TOTAL=${#LOTERIAS[@]}
SUCESSO=0
ERRO=0

# Executar o download de cada loteria
for i in "${!LOTERIAS[@]}"; do
    LOTERIA="${LOTERIAS[$i]}"
    NUMERO=$((i + 1))

    echo "----------------------------------------"
    echo "[$NUMERO/$TOTAL] Processando: $LOTERIA"
    echo "----------------------------------------"

    # Executar o JAR com a loteria específica
    java -jar /app/cli-loterias-caixa.jar -l "$LOTERIA"

    # Verificar o código de retorno
    if [ $? -eq 0 ]; then
        echo "✓ $LOTERIA processada com sucesso!"
        ((SUCESSO++))
    else
        echo "✗ Erro ao processar $LOTERIA"
        ((ERRO++))
    fi

    echo ""

    # Pequena pausa entre execuções para evitar sobrecarga
    if [ $NUMERO -lt $TOTAL ]; then
        sleep 2
    fi
done

# Sumário final
echo "=========================================="
echo "Processamento finalizado!"
echo "=========================================="
echo "Total de loterias:  $TOTAL"
echo "Sucesso: $SUCESSO"
echo "Erros: $ERRO"
echo "Data final: $(date '+%Y-%m-%d %H:%M:%S')"
echo "=========================================="

# Retornar código de erro se houver falhas
if [ $ERRO -gt 0 ]; then
    exit 1
fi

exit 0