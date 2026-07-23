# Quickstart: Validação manual — destaque persistente e paginação de concursos em cards de teimosinha

## Pré-requisitos

- Build local: `mvn clean package -DskipTests` (BUILD SUCCESS)
- App rodando com MongoDB disponível (`docker-compose up -d --build`, ou `java -jar target/loterias-caixa.war` com Mongo local) — ver `CLAUDE.md`
- Quatro jogos cadastrados via `/jogos/cadastro`, cobrindo as combinações de teimosinha × apuração:
  - **A — Teimosinha expirada**: intervalo com mais de um concurso, totalmente apurado (ex.: `3667` a `3740`, todos já importados na base).
  - **B — Teimosinha ativa com múltiplos apurados**: intervalo com mais de um concurso, alguns apurados e alguns pendentes no fim (ex.: `3730` a `3750`, com `3730`–`3745` importados).
  - **C — Não-teimosinha (concurso único) já apurado**: `concursoInicial == concursoFinal`, já importado (ex.: `3743` a `3743`).
  - **D — Teimosinha sem nenhum apurado**: intervalo com mais de um concurso, todo no futuro/pendente (ex.: `9990` a `9995`, nenhum importado ainda).

## Cenário 1 — Destaque aparece mesmo em teimosinha expirada (US1, FR-001)

1. Acesse `/jogos` e localize o card do jogo A (teimosinha expirada).
2. **Esperado**: as dezenas grandes que coincidem com o resultado do concurso `3740` (o mais recente do intervalo) aparecem destacadas com a cor do badge "premiado" — diferente do comportamento anterior (005), que escondia o destaque quando `pendentes == 0`.
3. Compare com `GET /api/jogos`: o item desse jogo deve ter `concursoComparado: 3740` e `dezenasAcertadasUltimoConcurso` igual às `dezenasAcertadas` do concurso `3740` em `GET /api/jogos/{id}/conferencia`.

## Cenário 2 — Rótulo indica o concurso comparado (US2, FR-003)

1. No mesmo card do jogo A, observe o texto próximo às dezenas destacadas.
2. **Esperado**: um rótulo exibe explicitamente "comparação com o concurso 3740" (ou fraseado equivalente com o número do concurso).
3. Repita para o jogo C (não-teimosinha, já apurado): o rótulo aparece normalmente com o número `3743`, mas **sem** nenhum controle de voltar/avançar ao lado (FR-006).

## Cenário 3 — Paginação voltar/avançar dentro de uma teimosinha (US3, FR-005 a FR-009)

1. No card do jogo B (teimosinha ativa, `3730`–`3745` apurados, `3746`–`3750` pendentes), confirme o estado inicial: rótulo "comparação com o concurso 3745", controle avançar **desabilitado**, controle voltar **habilitado**.
2. Clique em voltar. **Esperado**: destaque e rótulo passam a refletir o concurso `3744`; controle avançar passa a ficar habilitado.
3. Clique em voltar repetidamente até chegar ao concurso `3730` (primeiro apurado do intervalo). **Esperado**: ao chegar em `3730`, o controle voltar fica desabilitado — não é possível ir além do intervalo apurado (FR-008).
4. A partir de `3730`, clique em avançar repetidamente até voltar a `3745`. **Esperado**: o controle avançar fica desabilitado ao chegar em `3745` (o mais recente apurado) — nunca alcança um concurso pendente (`3746`+) (FR-009, edge case).
5. Recarregue `/jogos`. **Esperado**: o card volta a mostrar por padrão o concurso `3745` (a navegação não é lembrada entre visitas, FR-010).
6. Com o card do jogo B navegado para outro concurso (ex.: `3744`), confirme que o card do jogo A permanece mostrando `3740` sem qualquer mudança (FR-011).

## Cenário 4 — Não-teimosinha nunca exibe controles (US3, FR-006)

1. Acesse `/jogos` e localize o card do jogo C (`3743` a `3743`).
2. **Esperado**: nenhum controle de voltar/avançar é exibido, mesmo com o rótulo do concurso `3743` visível.

## Cenário 5 — Teimosinha sem nenhum concurso apurado (FR-012)

1. Acesse `/jogos` e localize o card do jogo D (`9990` a `9995`, nenhum concurso apurado).
2. **Esperado**: nenhuma dezena aparece destacada; o rótulo exibe um texto indicativo de espera (ex.: "aguardando apuração") no lugar do número do concurso; os controles de voltar e avançar aparecem, mas **ambos desabilitados**.
3. Compare com `GET /api/jogos`: o item desse jogo deve ter `concursoComparado: null` e `dezenasAcertadasUltimoConcurso: []`.

## Regressão geral

- Grade de cards, badge "premiado" clicável, painel de sorteios premiados (`GET /api/jogos/{id}/conferencia`), custo total e ganho total (features 004/005) continuam funcionando sem mudanças — o painel de premiados usa o mesmo endpoint agora também consumido pela paginação, sem conflito (cada card cacheia sua própria resposta).
- `GET /api/jogos` continua respondendo com todos os campos anteriores, mais `jogo.teimosinha` e `concursoComparado` (ver `contracts/api-jogos.md`).
- A cor de `.dezena.acertada`/`.badge.premiado` introduzida em 005 permanece igual — nenhuma cor nova é adicionada.
- `mvn clean package -DskipTests` continua com BUILD SUCCESS.
