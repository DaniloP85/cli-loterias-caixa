# Quickstart: Validação manual — ajustes de UI na página de jogos

## Pré-requisitos

- Build local: `mvn clean package -DskipTests` (BUILD SUCCESS)
- App rodando com MongoDB disponível (`docker-compose up -d --build`, ou `java -jar target/loterias-caixa.war` com Mongo local) — ver CLAUDE.md
- Pelo menos uma das 4 loterias com concursos importados, para o volante ter dezenas para exibir

## Cenário 1 — Grid do volante em 10 colunas (US1, FR-001, FR-002)

1. Acesse `/jogos`.
2. Para cada uma das 4 loterias (megasena, lotofacil, quina, lotomania), selecione-a no formulário de cadastro.
3. **Esperado**: o volante clicável de dezenas é renderizado em exatamente 10 colunas por linha, em todas as 4 loterias.
4. Redimensione a janela do navegador para uma largura estreita (simulando mobile).
5. **Esperado**: o grid continua com 10 colunas (as células encolhem em largura, respeitando o mínimo de `2.6rem`, mas o número de colunas não muda).
6. Para a lotomania (100 dezenas, múltiplo de 10), confirme que a última linha fecha exatamente sem sobra. Para uma loteria cuja contagem não é múltiplo de 10 (ex.: lotofacil, 25 dezenas), confirme que a última linha fica incompleta sem quebrar o alinhamento das colunas anteriores.

## Cenário 2 — Remoção da tabela de referência de preços (US2, FR-003, FR-004, FR-005)

1. Acesse `/jogos`.
2. **Esperado**: não há nenhuma seção "Tabela de preços" (título, cabeçalho de colunas ou linhas) visível abaixo do volante de dezenas, para nenhuma das 4 loterias.
3. Troque a loteria selecionada no formulário.
4. **Esperado**: nenhuma tabela de preços é (re)montada para a nova loteria.
5. Abra o DevTools do navegador (console) ao carregar `/jogos` e ao trocar de loteria.
6. **Esperado**: nenhum erro de JavaScript relacionado a `montarTabelaPrecos`, `tabela-precos-linhas` ou `data-precos` (função e elementos devem ter sido removidos junto com a seção).
7. Confirme que a coluna "custo da aposta" continua aparecendo normalmente na tabela de jogos já cadastrados (não deve ter sido afetada — ver `data-model.md`).
8. (Opcional, confirma FR-006) Chame diretamente `GET /api/loterias/megasena/precos` (ex. via curl ou navegador) e confirme que o endpoint continua respondendo normalmente com a tabela de referência — só a página `/jogos` parou de exibi-la.

## Regressão geral

- Cadastre um jogo normalmente (selecionar dezenas no volante + salvar) e confirme que o fluxo completo de "Meus jogos" continua funcionando: listagem de jogos, "Sorteios premiados", conferência avulsa — nenhuma dessas áreas deve ter sido afetada pelas duas mudanças acima.
