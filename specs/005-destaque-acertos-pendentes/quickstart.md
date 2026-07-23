# Quickstart: Validação manual — destaque de acertos do concurso mais recente nos cards

## Pré-requisitos

- Build local: `mvn clean package -DskipTests` (BUILD SUCCESS)
- App rodando com MongoDB disponível (`docker-compose up -d --build`, ou `java -jar target/loterias-caixa.war` com Mongo local) — ver `CLAUDE.md`
- Duas teimosinhas cadastradas via `/jogos/cadastro`:
  - Uma já totalmente concluída (intervalo termina antes do maior concurso importado na base) — ex.: `3667` a `3740`.
  - Outra ainda com concursos pendentes (intervalo cruza ou começa perto do maior concurso importado) — ex.: `3742` a `3763`, cadastrada para concursos futuros/recentes.

## Cenário 1 — Destaque aparece no concurso mais recente, enquanto a teimosinha está ativa (US1, FR-001 a FR-003, FR-006)

1. Garanta que a teimosinha ativa (`3742` a `3763`) já tem pelo menos o primeiro concurso do intervalo apurado na base (importe/atualize a base se necessário).
2. Acesse `/jogos`.
3. **Esperado**: no card dessa teimosinha, as dezenas grandes que coincidem com o resultado do concurso mais recente apurado do intervalo aparecem destacadas com a cor do badge "premiado" (fundo e texto claros) — mesmo que a quantidade de acertos seja menor que a faixa mínima de premiação da loteria (ex.: menos de 11 na lotofácil).
4. Confirme que o card **não** exibe em nenhum lugar o número do concurso ao qual o destaque se refere — só a cor nas dezenas.
5. Compare com `GET /api/jogos/{id}/conferencia` dessa teimosinha: as dezenas destacadas no card devem ser exatamente `dezenasAcertadas` do item de maior `concurso` cuja `situacao` não é `PENDENTE`.

## Cenário 2 — Nenhum destaque em teimosinha expirada (FR-004)

1. Acesse `/jogos` e localize o card da teimosinha já concluída (`3667` a `3740`, sem concursos pendentes).
2. **Esperado**: nenhuma dezena grande aparece destacada nesse card, mesmo que o último concurso do intervalo (`3740`) tenha tido acertos.

## Cenário 3 — Nenhum destaque antes do primeiro concurso apurado (FR-005)

1. Cadastre (ou use) uma teimosinha cujo intervalo ainda não tenha nenhum concurso apurado na base (todos os concursos do intervalo ainda são futuros/pendentes).
2. Acesse `/jogos`.
3. **Esperado**: nenhuma dezena grande aparece destacada nesse card.
4. Atualize a base para importar o primeiro concurso desse intervalo.
5. Recarregue `/jogos`. **Esperado**: agora as dezenas que coincidiram com esse primeiro concurso aparecem destacadas.

## Cenário 4 — Destaque acompanha a atualização da base (FR-006)

1. Com a teimosinha ativa do Cenário 1 já mostrando destaque de um concurso `N`, atualize a base para importar o próximo concurso `N+1` do intervalo.
2. Recarregue `/jogos`.
3. **Esperado**: o destaque no card passa a refletir os acertos do concurso `N+1`, não mais os de `N`.

## Cenário 5 — Suavização da cor de acertos destacados (US2, FR-007)

1. Abra o painel de sorteios premiados de uma teimosinha com concursos premiados (clique no badge "premiado" em `/jogos`).
2. **Esperado**: na coluna "Dezenas jogadas (acertos destacados)", as dezenas acertadas aparecem com um verde claramente mais suave que a versão anterior (fundo/texto no mesmo tom do badge "premiado"), mantendo o número legível.
3. Acesse `/jogos/{id}` de um jogo com concursos premiados.
4. **Esperado**: na tabela "Concurso a concurso", coluna "Dezenas sorteadas (acertos destacados)", a mesma cor mais suave aparece — a suavização é global (Clarifications), não só no painel de `/jogos`.

## Regressão geral

- Grade de cards, badge "premiado" clicável, painel único de sorteios premiados, custo total e ganho total (feature 004) continuam funcionando sem mudanças.
- `GET /api/jogos` continua respondendo com todos os campos anteriores, mais o novo `dezenasAcertadasUltimoConcurso` (ver `contracts/api-jogos.md`).
- `mvn clean package -DskipTests` continua com BUILD SUCCESS.
