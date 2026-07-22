quina# Spec: Regras de Aposta e Premiação — Quina (CAIXA)

> Fonte: https://loterias.caixa.gov.br/Paginas/Quina.aspx (consultado em 2026-07-14)
>
> ⚠️ O resultado do concurso vigente é carregado via JS/API, não vem no HTML
> estático. Use a API pública:
> `https://servicebus2.caixa.gov.br/portaldeloterias/api/quina` (último concurso)
> ou `.../quina/{numero}` (concurso específico).

---

## 1. Regras básicas do jogo

| Campo | Valor |
|---|---|
| Números disponíveis | 1 a 80 |
| Mínimo de números marcados por aposta | 5 |
| Máximo de números marcados por aposta | 15 |
| Números sorteados por concurso | 5 |
| Faixas premiadas | 2 (Duque), 3 (Terno), 4 (Quadra) e 5 (Quina) acertos |
| Dias de sorteio | Segunda a sábado, a partir das 21h (6x/semana) |

## 2. Tabela de preços / probabilidade (aposta simples)

| Nº marcados | Valor da aposta | Prob. Quina (1 em) | Prob. Quadra (1 em) | Prob. Terno (1 em) | Prob. Duque (1 em) |
|---|---|---|---|---|---|
| 5 | R$ 3,00 | 24.040.016 | 64.106 | 866 | 36 |
| 6 | R$ 18,00 | 4.006.669 | 21.658 | 445 | 25 |
| 7 | R$ 63,00 | 1.144.763 | 9.409 | 261 | 18 |
| 8 | R$ 168,00 | 429.286 | 4.770 | 168 | 14 |
| 9 | R$ 378,00 | 190.794 | 2.687 | 115 | 12 |
| 10 | R$ 756,00 | 95.396 | 1.635 | 82 | 9 |
| 11 | R$ 1.386,00 | 52.035 | 1.056 | 62 | 8 |
| 12 | R$ 2.376,00 | 30.354 | 714 | 48 | 7 |
| 13 | R$ 3.861,00 | 18.679 | 502 | 38 | 6 |
| 14 | R$ 6.006,00 | 12.008 | 364 | 31 | 5,8 |
| 15 | R$ 9.009,00 | 8.005 | 271 | 25 | 5,2 |

## 3. Bolão

| Nº números | Nº jogos | Cotas mín. | Cotas máx. | Valor mín. cota | Valor mín. bolão | Valor máx. bolão | Jogos máx. no recibo |
|---|---|---|---|---|---|---|---|
| 5 | 1 | 2 | 7 | R$ 4,00 | R$ 15,00 | R$ 30,00 | 10 |
| 6 | 6 | 2 | 25 | R$ 4,00 | R$ 18,00 | R$ 180,00 | 10 |
| 7 | 21 | 2 | 50 | R$ 4,00 | R$ 63,00 | R$ 630,00 | 10 |
| 8 | 56 | 2 | 50 | R$ 4,00 | R$ 168,00 | R$ 1.680,00 | 10 |
| 9 | 126 | 2 | 50 | R$ 7,56 | R$ 378,00 | R$ 3.780,00 | 10 |
| 10 | 252 | 2 | 50 | R$ 15,12 | R$ 756,00 | R$ 7.560,00 | 10 |
| 11 | 462 | 2 | 50 | R$ 27,72 | R$ 1.386,00 | R$ 13.860,00 | 10 |
| 12 | 792 | 2 | 50 | R$ 47,52 | R$ 2.376,00 | R$ 23.760,00 | 10 |
| 13 | 1.287 | 2 | 50 | R$ 77,22 | R$ 3.861,00 | R$ 38.610,00 | 10 |
| 14 | 2.002 | 2 | 50 | R$ 120,12 | R$ 6.006,00 | R$ 60.060,00 | 10 |
| 15 | 3.003 | 2 | 50 | R$ 180,18 | R$ 9.009,00 | R$ 90.090,00 | 10 |

- Todas as apostas de um bolão devem ter a mesma quantidade de números.
- Tarifa de serviço adicional: até 35% do valor da cota.
- Venda de bolão digital encerra às 19h30 para sorteio no mesmo dia.

## 4. Distribuição da arrecadação (rateio geral)

Idêntica às demais modalidades: Prêmio Bruto 43,79% · Seguridade Social 17,32%
· FNC 2,91% · FUNPEN 3% · FNSP 6,80% · Ministério do Esporte 2,49% ·
Fenaclubes 0,01% · Secretarias estaduais de esporte 1% · CBC 0,46% ·
CBCP 0,07% · CBDE 0,22% · CBDU 0,11% · COB 1,73% · CPB 0,96% ·
Custeio/manutenção 19,13%. Total 100%.

## 5. Regras de premiação (prêmio bruto = 43,79% da arrecadação)

Não há prêmios de valor fixo — todas as faixas são variáveis (%).

### 5.1 Distribuição do prêmio bruto — concursos normais
| Faixa | Percentual |
|---|---|
| Quina (5 acertos) | 35% |
| Quadra (4 acertos) | 15% |
| Terno (3 acertos) | 10% |
| Duque (2 acertos) | 10% |
| Acumulado p/ acertadores da Quina de São João (concurso especial de junho) | 15% |
| Acumulado p/ 1ª faixa dos concursos de final "5" e do especial de junho | 15% |

> A soma nominal listada na página passa de 100% (35+15+10+10+15+15 = 100%,
> na verdade bate certo — os dois últimos itens de 15% se referem a destinos
> diferentes de acúmulo dentro da distribuição, não a percentuais adicionais
> sobre o prêmio bruto). Confirme o detalhamento exato na fonte antes de
> implementar cálculos financeiros críticos.

### 5.2 Regra de acumulação em cascata (concurso regular)
Se não houver ganhador em nenhuma das 4 faixas, o valor acumula para o
concurso seguinte, todo na 1ª faixa (Quina) — não há rateio entre faixas
inferiores em concurso regular.

### 5.3 Distribuição — Quina de São João (concurso especial de junho)
| Faixa | Percentual |
|---|---|
| 1ª (Quina, 5 acertos) | 65% |
| 2ª (Quadra, 4 acertos) | 15% |
| 3ª (Terno, 3 acertos) | 10% |
| 4ª (Duque, 2 acertos) | 10% |

Composição da 1ª faixa no concurso especial:
- 65% do valor destinado a prêmios da arrecadação do concurso
- + total acumulado para o concurso especial de junho
- + total acumulado do concurso anterior, se houver

### 5.4 Cascata de acumulação — Quina de São João
1. Sem ganhador na Quina (1ª) → soma à 2ª faixa (Quadra).
2. Sem ganhador na Quina e Quadra → soma à 3ª faixa (Terno).
3. Sem ganhador em Quina, Quadra e Terno → soma à 4ª faixa (Duque).
4. Sem ganhador em nenhuma → acumula para o concurso seguinte, na 1ª faixa.

### 5.5 Prescrição
Prêmios não resgatados em **90 dias** são repassados ao FIES.

## 6. Quantidade de prêmios pagos por aposta, por faixa de acerto

| Nº marcados | Quina(1ªF) | Quadra(2ªF, de 5n) | Terno(3ªF, de 5n) | Duque(4ªF, de 5n) | Quadra(2ªF, de 4n) | Terno(3ªF, de 4n) | Duque(4ªF, de 4n) | Terno(3ªF, de 3n) | Duque(4ªF, de 3n) | Duque(4ªF, de 2n) |
|---|---|---|---|---|---|---|---|---|---|---|
| 5 | 1 | 0 | 0 | 0 | 1 | 0 | 0 | 1 | 0 | 1 |
| 6 | 1 | 5 | 0 | 0 | 2 | 4 | 0 | 3 | 3 | 4 |
| 7 | 1 | 10 | 10 | 0 | 3 | 12 | 6 | 6 | 12 | 10 |
| 8 | 1 | 15 | 30 | 10 | 4 | 24 | 24 | 10 | 30 | 20 |
| 9 | 1 | 20 | 60 | 40 | 5 | 40 | 60 | 15 | 60 | 35 |
| 10 | 1 | 25 | 100 | 100 | 6 | 60 | 120 | 21 | 105 | 56 |
| 11 | 1 | 30 | 150 | 200 | 7 | 84 | 210 | 28 | 168 | 84 |
| 12 | 1 | 35 | 210 | 350 | 8 | 112 | 336 | 36 | 252 | 120 |
| 13 | 1 | 40 | 280 | 560 | 9 | 144 | 504 | 45 | 360 | 165 |
| 14 | 1 | 45 | 360 | 840 | 10 | 180 | 720 | 55 | 495 | 220 |
| 15 | 1 | 50 | 450 | 1.200 | 11 | 220 | 990 | 66 | 660 | 286 |

---

## 7. Sugestão de estrutura para o SDD

1. `calcular_valor_aposta(qtd_numeros)` — tabela seção 2.
2. `calcular_premio_bruto(arrecadacao)` — 43,79% da arrecadação.
3. `distribuir_premio_variavel(valor_bruto, tipo_concurso)` — normal (5.1) vs.
   Quina de São João (5.3), percentuais diferentes.
4. `aplicar_cascata_acumulacao(faixas_sem_ganhador, tipo_concurso)` —
   concurso regular acumula direto na 1ª faixa (5.2); concurso especial faz
   cascata 1ª→2ª→3ª→4ª (5.4).
5. `calcular_num_premios_por_aposta(qtd_numeros_jogados, faixa_acerto)` —
   tabela seção 6, validar contra API oficial.
6. Fonte de dados de resultado: API pública da CAIXA (`.../api/quina`).
7. Testar isoladamente a regra "concurso de final 5" — a página menciona
   acúmulo específico para "final 5" (não "0"), diferente de Lotofácil/Mega
   que usam "final 0" ou "final 0 ou 5". Confirmar com a API se necessário.