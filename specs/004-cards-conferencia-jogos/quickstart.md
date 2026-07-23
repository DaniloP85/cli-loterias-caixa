# Quickstart: Validação manual — layout em cards na página de conferência de jogos

## Pré-requisitos

- Build local: `mvn clean package -DskipTests` (BUILD SUCCESS)
- App rodando com MongoDB disponível (`docker-compose up -d --build`, ou `java -jar target/loterias-caixa.war` com Mongo local) — ver `CLAUDE.md`
- Pelo menos duas teimosinhas cadastradas via `/jogos/cadastro`, de loterias diferentes, cada uma abrangendo mais de um concurso, com concursos já importados suficientes para que pelo menos uma tenha concursos premiados e outra tenha concursos pendentes

## Cenário 1 — Grade de cards em vez de tabela (US1, FR-001 a FR-005)

1. Acesse `/jogos`.
2. **Esperado**: a lista de jogos aparece como cards, não mais como tabela; em tela larga, no máximo 4 cards aparecem lado a lado por linha.
3. Redimensione a janela para uma largura estreita (mobile).
4. **Esperado**: o número de cards por linha diminui (nunca mais que 4), sem quebrar a legibilidade.
5. Em um card, confirme: o título mostra `"<loteria> de <concursoInicial> até <concursoFinal>"`; as dezenas aparecem com destaque no corpo; a descrição (quando cadastrada) aparece; um controle "×" está visível no cabeçalho do card.
6. Clique no "×" de um card. **Esperado**: pede confirmação; ao confirmar, o jogo é removido da grade (mesmo comportamento do botão "excluir" de antes).

## Cenário 2 — Custo total da teimosinha (US2, FR-006)

1. Identifique uma teimosinha com mais de um concurso (ex.: concurso inicial 3667, 74 concursos).
2. Calcule manualmente: `custo unitário da loteria/quantidade de dezenas` (ver `GET /api/loterias/{loteria}/precos`) `× quantidade de concursos`.
3. **Esperado**: o valor de "Custo" exibido no card é exatamente esse total, não o valor de uma única aposta.
4. Repita para uma teimosinha de um único concurso. **Esperado**: o valor exibido é igual ao de uma aposta isolada (caso particular).

## Cenário 3 — Ganho total da teimosinha (US3, FR-007)

1. Use uma teimosinha com pelo menos um concurso premiado.
2. Some manualmente (ou via `GET /api/jogos/{id}/conferencia`, campo `premio.valor` dos itens com `situacao == "PREMIADO"`) os valores de prêmio de todos os concursos premiados dessa teimosinha.
3. **Esperado**: o valor de "Ganhos" exibido no card é exatamente essa soma.
4. Use uma teimosinha sem nenhum concurso premiado ainda. **Esperado**: "Ganhos" mostra R$ 0,00.

## Cenário 4 — Painel único de sorteios premiados (US4, FR-009 a FR-013, Clarifications)

1. Com a página `/jogos` carregada, confirme que nenhum painel de sorteios premiados está visível.
2. Clique no badge "premiado" de uma teimosinha com prêmios. **Esperado**: um único painel aparece abaixo de toda a grade de cards, mostrando só os sorteios premiados dessa teimosinha (loteria, concurso, apuração, dezenas com acertos destacados, acertos, prêmio) — nenhuma outra teimosinha aparece nele.
3. Clique no badge "premiado" de **outra** teimosinha (com prêmios diferentes). **Esperado**: o conteúdo do painel é substituído pelos sorteios premiados dessa segunda teimosinha; a primeira não aparece mais.
4. Clique de novo no badge da teimosinha atualmente exibida no painel. **Esperado**: o painel fecha.
5. Clique no badge "premiado" de uma teimosinha sem nenhum prêmio (contagem 0). **Esperado**: o painel abre mostrando uma mensagem de ausência de prêmios, não uma tabela vazia.
6. Confirme no DevTools do navegador (aba Network) que nenhum novo endpoint é chamado além de `GET /api/jogos/{id}/conferencia`.

## Regressão geral

- A tabela combinada "Sorteios premiados" (que antes ficava sempre visível abaixo da lista) não existe mais em nenhuma forma permanente.
- `GET /api/loterias/{loteria}/precos` continua respondendo normalmente (inalterado por esta feature).
- A página de detalhe `/jogos/{id}` (clique em um jogo específico, se ainda houver esse caminho na UI, ou acesso direto à URL) continua exibindo seus badges "premiado" como texto estático, **sem** cursor de link nem comportamento de clique (a mudança do badge clicável é escopada só a `/jogos`).
- Cadastrar um novo jogo em `/jogos/cadastro` e conferir que ele aparece corretamente como card em `/jogos` (custo total, ganho R$ 0,00, resumo com pendentes).
- `mvn clean package -DskipTests` continua com BUILD SUCCESS.
