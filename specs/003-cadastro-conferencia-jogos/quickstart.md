# Quickstart: Validação manual — separar "Meus jogos" em cadastro e conferência

## Pré-requisitos

- Build local: `mvn clean package -DskipTests` (BUILD SUCCESS)
- App rodando com MongoDB disponível (`docker-compose up -d --build`, ou `java -jar target/loterias-caixa.war` com Mongo local) — ver CLAUDE.md
- Pelo menos uma das 4 loterias com concursos importados, para o volante ter dezenas para exibir

## Cenário 1 — Página de cadastro isolada (US1, FR-001, FR-002)

1. Acesse `/jogos/cadastro`.
2. **Esperado**: aparece apenas o formulário de cadastro (loteria, volante clicável, concurso inicial, quantidade de concursos, descrição, botão salvar) — sem lista de jogos cadastrados e sem tabela "Sorteios premiados" na página.
3. Selecione uma loteria, marque as dezenas mínimas exigidas e clique em salvar.
4. **Esperado**: a página permanece em `/jogos/cadastro` (sem reload/redirect), exibe uma mensagem de confirmação inline e um link para a página de conferência; o volante é limpo (nenhuma dezena marcada) e o formulário fica pronto para um novo cadastro.
5. Clique no link de confirmação.
6. **Esperado**: navega para `/jogos` (conferência) e o jogo recém-cadastrado aparece na lista.

## Cenário 2 — Página de conferência isolada (US2, FR-003, FR-004)

1. Com pelo menos um jogo já cadastrado, acesse `/jogos`.
2. **Esperado**: aparecem a lista de jogos cadastrados (loteria, dezenas, concursos, descrição, custo, resumo de acertos) e a tabela "Sorteios premiados" — sem nenhum formulário/volante de cadastro na página.
3. Exclua um dos jogos listados (botão "excluir" + confirmação).
4. **Esperado**: o jogo some da lista imediatamente, sem precisar visitar a página de cadastro.
5. Exclua (ou, em uma base de testes, garanta) que não há nenhum jogo cadastrado e recarregue `/jogos`.
6. **Esperado**: mensagem de lista vazia com um link direto para `/jogos/cadastro`.

## Cenário 3 — Navegação entre as páginas (US3, FR-007)

1. A partir de qualquer página do sistema (ex.: `/`), observe a navegação principal.
2. **Esperado**: existem dois links diretos — um para a página de cadastro e outro para a página de conferência — no lugar da antiga aba única "Meus jogos".
3. Acesse a página de cadastro e confirme que a aba correspondente aparece destacada como ativa; acesse a conferência e confirme o mesmo para a aba dela.
4. A partir da página de cadastro, sem usar a navegação principal, confirme que existe um link direto para a conferência (ex.: o link de confirmação do Cenário 1, passo 4).

## Regressão geral

- Acesse `/jogos/{id}` a partir de um item da lista na conferência e confirme que a página de detalhe (`jogo.jsp`) continua funcionando como hoje, com a aba "conferir jogos" destacada na navegação, e que o link "« voltar aos meus jogos" retorna para `/jogos`.
- Confirme que um link/favorito antigo direto para `/jogos` continua respondendo normalmente (agora como página de conferência), sem erro 404 (FR-011).
- Confirme que nenhuma chamada de API mudou: `POST /api/jogos`, `DELETE /api/jogos/{id}` e `GET /api/jogos` continuam funcionando exatamente como antes (FR-010).
