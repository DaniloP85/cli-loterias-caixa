# Spec: Regras de Aposta e Premiação — Lotofácil (CAIXA)

> Fonte: https://loterias.caixa.gov.br/Paginas/Lotofacil.aspx (consultado em 2026-07-14)
>
> ⚠️ Esta página **não** expõe o resultado do concurso vigente via HTML estático
> (é carregado via JS/API). Para números sorteados, acumulado e premiação de um
> concurso específico, use a API pública:
> `https://servicebus2.caixa.gov.br/portaldeloterias/api/lotofacil` (último concurso)
> ou `.../lotofacil/{numero}` (concurso específico).

---

## 1. Regras básicas do jogo

| Campo | Valor |
|---|---|
| Números disponíveis | 1 a 25 |
| Mínimo de números marcados por aposta | 15 |
| Máximo de números marcados por aposta | 20 |
| Números sorteados por concurso | 15 |
| Faixas premiadas | 11, 12, 13, 14 e 15 acertos |

## 2. Tabela de preços (aposta simples)

| Qtd. de números | Qtd. de jogos (combinações) | Valor da aposta |
|---|---|---|
| 15 | 1 | R$ 3,50 |
| 16 | 16 | R$ 56,00 |
| 17 | 136 | R$ 476,00 |
| 18 | 816 | R$ 2.856,00 |
| 19 | 3.876 | R$ 13.566,00 |
| 20 | 15.504 | R$ 54.264,00 |

> Regra derivada: `valor_aposta = 3.50 * C(n-1, 14)` onde `n` = qtd. de números marcados
> (equivalente a `3.50 * quantidade_de_jogos`, já que cada "jogo" é uma combinação de 15 números).

## 3. Bolão

| Qtd. números | Jogos no bolão | Cotas mín. | Cotas máx. | Valor mín. cota | Valor mín. bolão | Valor máx. bolão | Jogos máx. no recibo |
|---|---|---|---|---|---|---|---|
| 15 | 1 | 2 | 7 | R$ 4,50 | R$ 14,00 | R$ 35,00 | 10 |
| 16 | 16 | 2 | 35 | R$ 4,50 | R$ 56,00 | R$ 560,00 | 10 |
| 17 | 136 | 2 | 40 | R$ 11,90 | R$ 476,00 | R$ 4.760,00 | 10 |
| 18 | 816 | 2 | 50 | R$ 57,12 | R$ 2.856,00 | R$ 28.560,00 | 10 |
| 19 | 3.876 | 2 | 85 | R$ 159,60 | R$ 13.566,00 | R$ 122.094,00 | 9 |
| 20 | 15.504 | 2 | 100 | R$ 542,64 | R$ 54.264,00 | R$ 217.056,00 | 4 |

- Todas as apostas de um mesmo bolão devem ter a mesma quantidade de números.
- Tarifa de serviço adicional: até 35% do valor da cota (canal lotérica/online).
- Venda de bolão digital encerra às 19h30 para sorteio no mesmo dia.

## 4. Distribuição da arrecadação (rateio geral)

| Destino | Percentual |
|---|---|
| Prêmio bruto | 43,79% |
| Seguridade Social | 17,32% |
| Fundo Nacional da Cultura (FNC) | 2,91% |
| Fundo Penitenciário Nacional (FUNPEN) | 3,00% |
| Fundo Nacional de Segurança Pública (FNSP) | 6,80% |
| Ministério do Esporte | 2,49% |
| Fenaclubes | 0,01% |
| Secretarias de esporte estaduais/DF | 1,00% |
| Comitê Brasileiro de Clubes (CBC) | 0,46% |
| Comitê Brasileiro de Clubes Paralímpicos (CBCP) | 0,07% |
| Confed. Brasileira do Desporto Escolar (CBDE) | 0,22% |
| Confed. Brasileira do Desporto Universitário (CBDU) | 0,11% |
| Comitê Olímpico do Brasil (COB) | 1,73% |
| Comitê Paralímpico Brasileiro (CPB) | 0,96% |
| Custeio e manutenção (lotéricos 8,61% + operacional 9,57% + FDL 0,95%) | 19,13% |
| **Total** | **100%** |

## 5. Regras de premiação (prêmio bruto = 43,79% da arrecadação)

### 5.1 Prêmios de valor fixo (deduzidos primeiro)
- 11 acertos: **R$ 7,00** por aposta
- 12 acertos: **R$ 14,00** por aposta
- 13 acertos: **R$ 35,00** por aposta

### 5.2 Distribuição do valor restante — concursos normais
| Faixa | Percentual |
|---|---|
| 15 acertos | 62% |
| 14 acertos | 13% |
| Acumulado p/ próximo concurso de final "0" | 10% |
| Acumulado p/ 1ª faixa do concurso especial de setembro (Independência) | 15% |

### 5.3 Distribuição do valor restante — concursos de final "0"
| Faixa | Percentual |
|---|---|
| 15 acertos | 72% |
| 14 acertos | 13% |
| Acumulado p/ 1ª faixa do concurso especial de setembro (Independência) | 15% |

### 5.4 Regra de acumulação
Se não houver ganhador em nenhuma faixa, o valor acumula para o concurso seguinte,
sempre na faixa de 15 acertos.

### 5.5 Prescrição
Prêmios não resgatados em **90 dias** após o sorteio são repassados ao FIES.

## 6. Lotofácil da Independência (concurso especial de setembro)

- Vendas: 30 dias, volante específico, concomitante aos concursos normais.
- Distribuição do valor variável (após deduzir prêmios fixos):
  - 87% — 1ª faixa (15 acertos)
  - 13% — 2ª faixa (14 acertos)
- Composição da 1ª faixa: 87% da arrecadação do concurso + acumulado do concurso
  especial + acumulado de final zero + acumulado do concurso anterior (se houver).
- Acumulação em cascata: 15→14→13→...→5ª faixa; se nenhuma faixa tiver ganhador,
  acumula para a 1ª faixa do concurso seguinte.

## 7. Quantidade de prêmios pagos por aposta, por faixa de acerto

> "Faixa" numerada da mais alta (1ª) para a mais baixa dentro de cada categoria de acerto.
> Ex.: para aposta de 17 números acertando 15 números: paga 1x na 1ª faixa, 30x na 2ª, 105x na 3ª.

| Nº marcados | Nº jogos | 15 acertos (1ª/2ª/3ª/4ª/5ª) | 14 acertos (2ª/3ª/4ª/5ª) | 13 acertos (3ª/4ª/5ª) | 12 acertos (4ª/5ª) | 11 acertos (5ª) |
|---|---|---|---|---|---|---|
| 15 | 1 | 1/0/0/0/0 | 1/0/0/0 | 1/0/0 | 1/0 | 1 |
| 16 | 16 | 1/15/0/0/0 | 2/14/0/0 | 3/13/0 | 4/12 | 5 |
| 17 | 136 | 1/30/105/0/0 | 3/42/91/0 | 6/52/78 | 10/60 | 15 |
| 18 | 816 | 1/45/315/455/0 | 4/84/364/364 | 10/130/390 | 20/180 | 35 |
| 19 | 3.876 | 1/60/630/1820/1365 | 5/140/910/1820 | 15/260/1170 | 35/420 | 70 |
| 20 | 15.504 | 1/75/1050/4550/6825 | 6/210/1820/5460 | 21/455/2730 | 56/840 | 126 |

## 8. Probabilidade (1 em N) por faixa e tipo de aposta

| Acertos | 15 núm. | 16 núm. | 17 núm. | 18 núm. | 19 núm. | 20 núm. |
|---|---|---|---|---|---|---|
| 15 | 3.268.760 | 204.298 | 24.035 | 4.006 | 843 | 211 |
| 14 | 21.792 | 3.027 | 601 | 153 | 47 | 17 |
| 13 | 692 | 162 | 49 | 18 | 8 | 4,2 |
| 12 | 60 | 21 | 9 | 5 | 3,2 | 2,6 |
| 11 | 11 | 6 | 4 | 3 | 2,9 | 3,9 |

---

## 9. Sugestão de estrutura para o SDD (Spec-Driven Development)

Pontos que valem virar specs/testes separados no seu projeto:

1. **`calcular_valor_aposta(qtd_numeros)`** — usa a tabela da seção 2.
2. **`calcular_premio_bruto(arrecadacao)`** — 43,79% da arrecadação (seção 5).
3. **`aplicar_premios_fixos(qtd_apostas_11, qtd_apostas_12, qtd_apostas_13)`** — seção 5.1.
4. **`distribuir_premio_variavel(valor_restante, concurso_final_zero: bool)`** — percentuais
   variam conforme o concurso ser "final 0" ou não (seções 5.2 e 5.3).
5. **`calcular_num_premios_por_aposta(qtd_numeros_jogados, faixa_acerto)`** — tabela da seção 7,
   útil para validar quantas vezes uma aposta "grande" (16-20 números) é premiada em cada faixa.
6. **Regra de acumulação em cascata** (seção 5.4/6) — importante para o concurso especial
   de setembro, que tem lógica diferente dos concursos normais.
7. **Fonte de dados de resultado** (números sorteados, ganhadores, valores por concurso) —
   não vem desta página; especificar como requisito de integração com a API pública da CAIXA.