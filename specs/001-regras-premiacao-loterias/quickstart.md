# Quickstart: validar Regras de Aposta e Premiação por Loteria

Não há suite de testes automatizados neste repositório (ver `CLAUDE.md`).
Validação é manual, contra a aplicação rodando.

## Pré-requisitos

- MongoDB acessível (via `docker-compose up -d` ou instância local) com pelo
  menos uma loteria já importada (`POST /api/loterias/megasena/importacao`).
- App rodando: `mvn clean package -DskipTests && java -jar target/loterias-caixa.war`
  (ou `docker-compose up -d --build`).

## Cenário 1 — Custo da aposta (US1 / FR-001, FR-002, FR-003)

1. Cadastrar um jogo de Mega-Sena com 6 dezenas via `/jogos` (UI) ou:
   ```bash
   curl -X POST localhost:8080/api/jogos -H 'Content-Type: application/json' \
     -d '{"loteria":"megasena","numeros":[1,2,3,4,5,6],"concursoInicial":9999,"quantidadeConcursos":1,"descricao":"teste"}'
   ```
2. `GET /api/jogos` → confirmar `custoAposta: 6.00` no item criado.
3. Repetir com Lotofácil (18 dezenas) → esperar `custoAposta: 2856.00`.
4. Repetir com Lotomania (50 dezenas, único valor possível) → esperar
   `custoAposta: 3.00`.
5. Cadastrar um jogo com `concursoInicial` futuro (concurso ainda não
   sorteado) → confirmar que `custoAposta` aparece mesmo com o jogo
   `PENDENTE` (FR-002).

## Cenário 2 — Valor do prêmio (US2 / FR-004, FR-005, FR-007a, FR-007b)

1. Identificar um concurso já importado e as dezenas sorteadas (`GET
   /api/loterias/megasena/concursos/{numero}`).
2. Cadastrar um jogo cujas dezenas batam com uma faixa premiada (ex.: 4
   acertos) para esse `concursoInicial`.
3. `GET /jogos` (UI) → tabela "Sorteios premiados" deve mostrar o valor em
   reais da faixa "Quadra" daquele concurso específico (comparar com o
   valor oficial em `loterias.caixa.gov.br` ou na resposta bruta da API da
   Caixa para aquele concurso).
4. Repetir para Lotomania com 0 acertos → confirmar que aparece o valor da
   faixa "0 acertos" (regra especial já suportada na classificação).
5. Cadastrar um jogo com `concursoInicial` futuro → confirmar que **nenhum**
   valor de prêmio aparece (jogo `PENDENTE`, FR-005).
6. **Concurso legado**: escolher um concurso importado antes desta feature
   existir (sem `rateio_premios` no Mongo — checar via `mongosh` ou
   `GET /api/loterias/{loteria}/concursos/{numero}`) e repetir o passo 3;
   confirmar que o valor aparece mesmo assim (busca sob demanda, FR-007a) e
   que uma segunda consulta ao mesmo concurso não depende mais da API da
   Caixa (cache, FR-007b — pode-se confirmar via log/latência ou
   inspecionando o documento no Mongo antes/depois).
7. Simular falha de rede (ex.: derrubar `CAIXA_API_BASE_URL` temporariamente
   ou usar um host inválido) para um concurso legado → confirmar que a
   linha aparece com o prêmio marcado como indisponível, sem quebrar o
   restante da página (FR-007b).

## Cenário 3 — Tabela de referência de preços (US3 / FR-006)

1. Abrir `/jogos` na UI.
2. Selecionar cada uma das 4 loterias no formulário de cadastro e conferir
   que a seção de referência (perto do volante clicável) mostra o custo
   para cada quantidade válida de dezenas daquela loteria.
3. Para Lotomania, confirmar que aparece só o valor fixo, sem lista por
   quantidade (FR-003/US3 cenário 2).
4. Comparar os valores exibidos com a tabela oficial em
   `ideas/{loteria}.md` seção 2 (ou `loterias.caixa.gov.br`).

## Cenário 4 — Edge cases

- Múltiplos jogos do usuário premiados no mesmo concurso/faixa (mesmo com
  dezenas repetidas): confirmar que a tabela "Sorteios premiados" lista uma
  linha por jogo, cada uma com seu próprio valor de prêmio (não soma).
- Confirmar que o dataset de ML (`GET
  /api/loterias/{loteria}/export?formato=json` e a versão CSV) **não**
  ganhou nenhuma coluna nova relacionada a custo/prêmio (FR-010).
