# Phase 0 Research: Separar "Meus jogos" em página de cadastro e página de conferência

Nenhum item do Technical Context ficou marcado como `NEEDS CLARIFICATION` — a spec já resolveu a única ambiguidade de UX relevante (comportamento pós-cadastro) na sessão de clarificação de 2026-07-22. As decisões abaixo cobrem as escolhas técnicas necessárias para implementar a divisão dentro do padrão já estabelecido pelo projeto (ver CLAUDE.md).

## Decisão 1: Rota da página de conferência reaproveita `GET /jogos`

- **Decision**: A rota existente `GET /jogos` passa a renderizar a página de conferência (lista de jogos + "Sorteios premiados"), em vez de introduzir uma rota nova e redirecionar `/jogos` para ela.
- **Rationale**: Satisfaz FR-011 (endereço antigo continua funcionando) sem precisar de lógica de redirect HTTP; qualquer link/favorito para `/jogos` continua respondendo 200 com conteúdo válido. Também é a rota para a qual o usuário volta com mais frequência (checar resultados), conforme a Assumption já registrada na spec.
- **Alternatives considered**: Criar `/jogos/conferencia` como rota nova e fazer `/jogos` redirecionar (302) para ela — rejeitado por adicionar um hop de rede sem benefício, já que não há necessidade de preservar `/jogos` para outro propósito.

## Decisão 2: Cadastro vive em rota nova `GET /jogos/cadastro`

- **Decision**: Nova rota `GET /jogos/cadastro`, view `jogos-cadastro.jsp`.
- **Rationale**: Caminho dedicado e descritivo, aninhado sob `/jogos` (mesmo prefixo da área de "jogos"), consistente com o padrão de sub-rotas já usado no projeto (`/loterias/{loteria}/dashboard`, `/loterias/{loteria}/concursos/{numero}`).
- **Alternatives considered**: `/jogos/novo` — nome também razoável, mas "cadastro" reflete melhor o vocabulário já usado na spec e no `<h2>Cadastrar jogo (teimosinha)</h2>` existente.

## Decisão 3: Confirmação pós-cadastro é inline via JS, sem `location.reload()`

- **Decision**: `cadastrar()` na nova `jogos-cadastro.jsp` deixa de chamar `location.reload()` no sucesso (201); em vez disso, exibe uma mensagem de confirmação inline (reutilizando o padrão do elemento `#erro-jogo`, com uma contraparte de sucesso) contendo um link para `/jogos`, limpa a seleção do volante (`selecionadas = []`, desmarca os botões `.selecionada`) e reseta os campos de concurso/descrição para permitir um novo cadastro em seguida.
- **Rationale**: Requisito direto da clarificação registrada na spec (FR-006): permanecer na página, confirmar inline, oferecer link, manter o formulário pronto para outro cadastro.
- **Alternatives considered**: Redirecionar via `window.location.href = '/jogos'` — era a Opção A rejeitada na clarificação. Modal de confirmação (Opção C) — rejeitado por adicionar complexidade de UI sem necessidade.

## Decisão 4: Navegação principal ganha duas abas dedicadas

- **Decision**: `comum/cabecalho.jspf` troca `<a href="/jogos" class="aba ${abaAtiva == 'jogos' ? 'ativa' : ''}">Meus jogos</a>` por duas entradas: "Cadastrar jogo" (`/jogos/cadastro`, ativa quando `abaAtiva == 'jogos-cadastro'`) e "Conferir jogos" (`/jogos`, ativa quando `abaAtiva == 'jogos-conferencia'`).
- **Rationale**: Atende FR-007 (acesso direto às duas páginas a partir de qualquer página do sistema) da forma mais simples possível — dois links de nível superior, sem submenu/dropdown, seguindo o padrão plano já usado para "Manutenção" e "Machine Learning".
- **Alternatives considered**: Manter uma aba "Meus jogos" com um submenu para as duas páginas — rejeitado por exigir componente de UI (dropdown) que não existe hoje no cabeçalho, aumentando o escopo além de uma reorganização de páginas.

## Decisão 5: Página de detalhe (`/jogos/{id}`) e link de "voltar" continuam iguais

- **Decision**: `jogo.jsp` não muda de marcação; `PaginasController.jogo()` passa a setar `abaAtiva = "jogos-conferencia"` (em vez de `"jogos"`) para destacar a aba correta. O link `«voltar aos meus jogos»` continua apontando para `/jogos`, que agora é a conferência — continua correto sem edição.
- **Rationale**: A spec marca explicitamente essa página como fora de escopo (Edge Cases, Assumptions) — só precisa continuar acessível e com a aba certa destacada.
- **Alternatives considered**: Nenhuma — mudança de conteúdo nesta página está fora de escopo por decisão explícita do usuário.

## Decisão 6: Estado vazio da conferência aponta para a página de cadastro

- **Decision**: A mensagem `"Nenhum jogo cadastrado ainda. Cadastre acima a sua primeira teimosinha."` em `jogos.jsp` é atualizada para não dizer mais "acima" (o formulário não está mais nessa página) e passa a incluir um link para `/jogos/cadastro`.
- **Rationale**: Satisfaz FR-008 e o Acceptance Scenario 2 da User Story 2 (estado vazio com caminho claro para o cadastro).
- **Alternatives considered**: Nenhuma.
