# Spec: Regras de Aposta e Premiação — Lotomania (CAIXA)

> Fonte: https://loterias.caixa.gov.br/Paginas/Lotomania.aspx (consultado em 2026-07-14)
>
> ⚠️ O resultado do concurso vigente é carregado via JS/API, não vem no HTML
> estático. Use a API pública:
> `https://servicebus2.caixa.gov.br/portaldeloterias/api/lotomania` (último concurso)
> ou `.../lotomania/{numero}` (concurso específico).

---

## 1. Regras básicas do jogo

| Campo | Valor |
|---|---|
| Números disponíveis | 0 a 99 (100 números) |
| Números marcados por aposta | Fixo: 50 |
| Números sorteados por concurso | 20 |
| Faixas premiadas | 15, 16, 17, 18, 19, 20 acertos **e também 0 acertos** |
| Dias de sorteio | Segunda, quarta e sexta, a partir das 21h |

> Diferente das demais modalidades, a Lotomania **não tem variação no número
> de marcações** (sempre 50 números) e por isso **não tem tabela de bolão nem
> tabela de preços por quantidade** — a aposta é sempre a mesma.

## 2. Preço da aposta

| Campo | Valor |
|---|---|
| Valor único da aposta | R$ 3,00 |

Não há variação por quantidade de números marcados (aposta é sempre 50 números)
nem estrutura de bolão documentada na página oficial para esta modalidade.

## 3. Recursos de aposta

- **Surpresinha**: sistema escolhe os 50 números.
- **Teimosinha**: repete a mesma aposta por 2, 3, 4, 6, 8, 9 ou 12 concursos consecutivos.
- **Aposta-Espelho**: gera automaticamente uma segunda aposta com os outros 50
  números não marcados na aposta original.

## 4. Distribuição da arrecadação (rateio geral)

Idêntica às demais modalidades: Prêmio Bruto 43,79% · Seguridade Social 17,32%
· FNC 2,91% · FUNPEN 3% · FNSP 6,80% · Ministério do Esporte 2,49% ·
Fenaclubes 0,01% · Secretarias estaduais de esporte 1% · CBC 0,46% ·
CBCP 0,07% · CBDE 0,22% · CBDU 0,11% · COB 1,73% · CPB 0,96% ·
Custeio/manutenção 19,13%. Total 100%.

## 5. Regras de premiação (prêmio bruto = 43,79% da arrecadação)

### 5.1 Distribuição do prêmio bruto
| Faixa | Acertos | Percentual |
|---|---|---|
| 1ª | 20 números | 45% |
| 2ª | 19 números | 16% |
| 3ª | 18 números | 10% |
| 4ª | 17 números | 7% |
| 5ª | 16 números | 7% |
| 6ª | 15 números | 7% |
| 7ª | 0 números (nenhum acerto) | 8% |

> Soma: 45+16+10+7+7+7+8 = 100% do prêmio bruto.

### 5.2 Regra de acumulação
- Sem ganhador na **7ª faixa (0 acertos)**: o prêmio dessa faixa acumula para
  o concurso seguinte, mas na **1ª faixa (20 acertos)** — não na própria faixa
  de 0 acertos.
- Nas demais faixas (1ª a 6ª): sem ganhador, o prêmio acumula na **própria
  faixa**, para o concurso seguinte.

### 5.3 Prescrição
Prêmios não resgatados em 90 dias são repassados ao FIES (regra padrão das
Loterias CAIXA; a página específica da Lotomania não repete o texto, mas a
regra geral se aplica).

## 6. Probabilidade

| Faixa (acertos) | Probabilidade (1 em) |
|---|---|
| 20 | 11.372.635 |
| 19 | 352.551 |
| 18 | 24.235 |
| 17 | 2.776 |
| 16 | 472 |
| 15 | 112 |
| 0 | 11.372.635 |

> Nota: a probabilidade de acertar 0 números é idêntica à de acertar os 20 —
> isso é uma simetria combinatória esperada (escolher 50 de 100 números tem a
> mesma probabilidade de conter todos os 20 sorteados ou nenhum deles).

---

## 7. Sugestão de estrutura para o SDD

1. `valor_aposta()` — sempre retorna R$ 3,00 (sem parâmetro de quantidade,
   ao contrário das outras modalidades).
2. `calcular_premio_bruto(arrecadacao)` — 43,79% da arrecadação.
3. `distribuir_premio_variavel(valor_bruto)` — 7 faixas fixas, percentuais
   da seção 5.1.
4. `aplicar_acumulacao(faixas_sem_ganhador)` — regra especial: faixa 7 (zero
   acertos) sem ganhador redireciona o valor para a faixa 1 (20 acertos),
   diferente do padrão "acumula na própria faixa" das demais faixas.
5. Como a aposta é fixa (sempre 50 números), o cálculo de "quantidade de
   prêmios por aposta" não se aplica da mesma forma que em Lotofácil/Mega/
   Quina — vale a pena não replicar essa função aqui, ou documentar que ela
   sempre retorna 1 prêmio por faixa acertada.
6. Fonte de dados de resultado: API pública da CAIXA (`.../api/lotomania`).