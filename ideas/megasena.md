# Spec: Regras de Aposta e Premiação — Mega-Sena (CAIXA)

> Fonte: https://loterias.caixa.gov.br/Paginas/Mega-Sena.aspx (consultado em 2026-07-14)
>
> ⚠️ O resultado do concurso vigente (números sorteados, acumulado, ganhadores) é
> carregado via JS/API, não vem no HTML estático. Use a API pública:
> `https://servicebus2.caixa.gov.br/portaldeloterias/api/megasena` (último concurso)
> ou `.../megasena/{numero}` (concurso específico).

---

## 1. Regras básicas do jogo

| Campo | Valor |
|---|---|
| Números disponíveis | 1 a 60 |
| Mínimo de números marcados por aposta | 6 |
| Máximo de números marcados por aposta | 20 (volante); até 15 pelo Internet Banking |
| Números sorteados por concurso | 6 |
| Faixas premiadas | 4 (Quadra), 5 (Quina) e 6 (Sena) acertos |
| Dias de sorteio | Terça, quinta e sábado, a partir das 21h |

## 2. Tabela de preços / probabilidade (aposta simples)

| Nº marcados | Valor da aposta | Prob. Sena (1 em) | Prob. Quina (1 em) | Prob. Quadra (1 em) |
|---|---|---|---|---|
| 6 | R$ 6,00 | 50.063.860 | 154.518 | 2.332 |
| 7 | R$ 42,00 | 7.151.980 | 44.981 | 1.038 |
| 8 | R$ 168,00 | 1.787.995 | 17.192 | 539 |
| 9 | R$ 504,00 | 595.998 | 7.791 | 312 |
| 10 | R$ 1.260,00 | 238.399 | 3.973 | 195 |
| 11 | R$ 2.772,00 | 108.363 | 2.211 | 129 |
| 12 | R$ 5.544,00 | 54.182 | 1.317 | 90 |
| 13 | R$ 10.296,00 | 29.175 | 828 | 65 |
| 14 | R$ 18.018,00 | 16.671 | 544 | 48 |
| 15 | R$ 30.030,00 | 10.003 | 370 | 37 |
| 16 | R$ 48.048,00 | 6.252 | 260 | 29 |
| 17 | R$ 74.256,00 | 4.045 | 188 | 23 |
| 18 | R$ 111.384,00 | 2.697 | 139 | 19 |
| 19 | R$ 162.792,00 | 1.845 | 105 | 16 |
| 20 | R$ 232.560,00 | 1.292 | 81 | 13 |

## 3. Bolão

| Nº números | Nº jogos | Cotas mín. | Cotas máx. | Valor mín. cota | Valor mín. bolão | Valor máx. bolão | Jogos máx. no recibo |
|---|---|---|---|---|---|---|---|
| 6 | 1 | 2 | 8 | R$ 7,00 | R$ 18,00 | R$ 60,00 | 10 |
| 7 | 7 | 2 | 50 | R$ 8,00 | R$ 42,00 | R$ 420,00 | 10 |
| 8 | 28 | 2 | 100 | R$ 8,00 | R$ 168,00 | R$ 1.680,00 | 10 |
| 9 | 84 | 2 | 100 | R$ 8,00 | R$ 504,00 | R$ 5.040,00 | 10 |
| 10 | 210 | 2 | 100 | R$ 12,60 | R$ 1.260,00 | R$ 12.600,00 | 10 |
| 11 | 462 | 2 | 100 | R$ 27,72 | R$ 2.772,00 | R$ 27.720,00 | 10 |
| 12 | 924 | 2 | 100 | R$ 55,44 | R$ 5.544,00 | R$ 55.440,00 | 10 |
| 13 | 1.716 | 2 | 100 | R$ 102,96 | R$ 10.296,00 | R$ 102.960,00 | 10 |
| 14 | 3.003 | 2 | 100 | R$ 180,18 | R$ 18.018,00 | R$ 180.180,00 | 10 |
| 15 | 5.005 | 2 | 100 | R$ 300,30 | R$ 30.030,00 | R$ 210.210,00 | 7 |
| 16 | 8.008 | 2 | 100 | R$ 480,48 | R$ 48.048,00 | R$ 192.192,00 | 4 |
| 17 | 12.376 | 2 | 100 | R$ 742,56 | R$ 74.256,00 | R$ 222.768,00 | 3 |
| 18 | 18.564 | 2 | 100 | R$ 1.113,84 | R$ 111.384,00 | R$ 222.768,00 | 2 |
| 19 | 27.132 | 2 | 100 | R$ 1.627,92 | R$ 162.792,00 | R$ 162.792,00 | 1 |
| 20 | 38.760 | 2 | 100 | R$ 2.325,60 | R$ 232.560,00 | R$ 232.560,00 | 1 |

- Todas as apostas de um bolão devem ter a mesma quantidade de números.
- Tarifa de serviço adicional: até 35% do valor da cota.
- Venda de bolão digital encerra às 19h30 para sorteio no mesmo dia.
- Apostas via Internet Banking CAIXA: resgate de prêmio só em agência, independente do valor.

## 4. Distribuição da arrecadação (rateio geral)

Mesma tabela usada em todas as modalidades CAIXA (idêntica à da Lotofácil):
Prêmio Bruto 43,79% · Seguridade Social 17,32% · FNC 2,91% · FUNPEN 3% ·
FNSP 6,80% · Ministério do Esporte 2,49% · Fenaclubes 0,01% · Secretarias
estaduais de esporte 1% · CBC 0,46% · CBCP 0,07% · CBDE 0,22% · CBDU 0,11% ·
COB 1,73% · CPB 0,96% · Custeio/manutenção 19,13% (lotéricos 8,61% +
operacional 9,57% + FDL 0,95%). Total 100%.

## 5. Regras de premiação (prêmio bruto = 43,79% da arrecadação)

Não há prêmios de valor fixo na Mega-Sena — todas as faixas são variáveis (%).

### 5.1 Distribuição do prêmio bruto — concursos normais
| Faixa | Percentual |
|---|---|
| Sena (6 acertos) | 40% |
| Quina (5 acertos) | 13% |
| Quadra (4 acertos) | 15% |
| Acumulado p/ concursos de final "0" ou "5" (faixa Sena) | 22% |
| Acumulado p/ 1ª faixa (Sena) da Mega da Virada (último concurso final 0/5 do ano) | 10% |

### 5.2 Regra de acumulação em cascata (quando não há ganhador)
1. Sem ganhador na Sena → acumula/rateia entre a Quina.
2. Sem ganhador na Sena e Quina → acumula/rateia entre a Quadra.
3. Sem ganhador em nenhuma faixa → acumula para o concurso seguinte, nas
   respectivas faixas (não é "tudo pra Sena" como na Lotofácil).

### 5.3 Prescrição
Prêmios não resgatados em **90 dias** são repassados ao FIES.

## 6. Mega 30 Anos / Mega da Virada (concursos especiais)

Ambos seguem as mesmas regras de distribuição:

- 90% — 1ª faixa (Sena)
- 5% — 2ª faixa (Quina)
- 5% — 3ª faixa (Quadra)

Composição da 1ª faixa (Sena) no concurso especial:
- 90% da arrecadação do próprio concurso
- + total acumulado do último concurso final 0/5 do ano
- + total acumulado do concurso de final 0/5
- + total acumulado da 1ª faixa (Sena) do concurso anterior, se houver

Acumulação em cascata igual à seção 5.2 (Sena → Quina → Quadra → concurso seguinte).

**Mega da Virada**: é o último concurso de final 0 ou 5 do ano civil, sorteado
sempre em 31/12, com vendas abertas durante novembro e dezembro.
**Mega 30 Anos**: concurso especial com período de vendas definido pela CAIXA
(não fixo por data, ao contrário da Mega da Virada).

## 7. Quantidade de prêmios pagos por aposta, por faixa de acerto

| Nº marcados | Sena (6) | Quina (5) | Quadra (4, quando 6 acertos) | Quina→Quadra | Quadra (só 4) |
|---|---|---|---|---|---|
| 6 | 1 | 0 | 0 | 1 | 1 |
| 7 | 1 | 6 | 0 | 2 | 3, 5* |
| 8 | 1 | 12 | 15 | 3 | 6, 15* |
| 9 | 1 | 18 | 45 | 4 | 10, 30* |
| 10 | 1 | 24 | 90 | 5 | 15, 50* |
| 11 | 1 | 30 | 150 | 6 | 21, 75* |
| 12 | 1 | 36 | 225 | 7 | 28, 105* |
| 13 | 1 | 42 | 315 | 8 | 36, 140* |
| 14 | 1 | 48 | 420 | 9 | 45, 180* |
| 15 | 1 | 54 | 540 | 10 | 55, 225* |
| 16 | 1 | 60 | 675 | 11 | 66, 275* |
| 17 | 1 | 66 | 825 | 12 | 78, 330* |
| 18 | 1 | 72 | 990 | 13 | 91, 390* |
| 19 | 1 | 78 | 1.170 | 14 | 105, 455* |
| 20 | 1 | 84 | 1.365 | 15 | 120, 525* |

> Colunas conforme a tabela oficial "Quantidade de prêmios a receber acertando":
> **Sena** = prêmios pagos acertando 6 na faixa 6 números; **Quina** = pagos
> acertando 5 na faixa "6 números" (combinações que contêm a Sena); **Quadra**
> (3ª col.) = pagos acertando 4 dentro da faixa "6 números"; **Quina** (4ª col.)
> = pagos acertando 5 na faixa "5 números"; **Quadra** (5ª col., marcada com \*)
> = pagos acertando 4 na faixa "5 números"; última coluna = pagos acertando 4
> na faixa "4 números" (mesmo valor da penúltima, replicado na fonte).
> Por ambiguidade da tabela de origem, valide contra a API oficial antes de
> usar em produção — ela é a fonte de verdade para conferência de apostas.

## 8. Tabela de preços × probabilidade

Ver seção 2 (já unificada).

---

## 9. Sugestão de estrutura para o SDD

1. `calcular_valor_aposta(qtd_numeros)` — tabela seção 2.
2. `calcular_premio_bruto(arrecadacao)` — 43,79% da arrecadação.
3. `distribuir_premio_variavel(valor_bruto, tipo_concurso)` — tipo_concurso ∈
   {normal, final_0_5, especial} muda os percentuais (seções 5.1 e 6).
4. `aplicar_cascata_acumulacao(faixas_sem_ganhador)` — Sena → Quina → Quadra →
   concurso seguinte (seção 5.2), reaproveitável no concurso especial.
5. `calcular_num_premios_por_aposta(qtd_numeros_jogados, faixa_acerto)` —
   seção 7 (**validar contra API antes de confiar 100%**, tabela de origem tem
   ambiguidade de rotulagem).
6. Diferenciar explicitamente `Mega da Virada` (data fixa 31/12, gatilho por
   calendário) de `Mega 30 Anos` (concurso especial avulso, gatilho manual/CAIXA).
7. Fonte de dados de resultado: API pública da CAIXA (`.../api/megasena`).