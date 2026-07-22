# Phase 0 Research: Ajustes de UI na página de jogos

Nenhum `NEEDS CLARIFICATION` restou no Technical Context do plan — as duas decisões abaixo já vieram explícitas do pedido do usuário; documentadas aqui apenas para registrar o raciocínio e as alternativas descartadas.

## 1. Grid do volante: colunas fixas vs. responsivas

- **Decision**: `grid-template-columns: repeat(10, minmax(2.6rem, 1fr))` — 10 colunas fixas, mantendo `minmax(2.6rem, 1fr)` por célula.
- **Rationale**: pedido explícito do usuário ("só tô trocando o comportamento do grid"). 10 colunas espelha o layout de um volante de loteria físico e fica consistente em todas as 4 loterias suportadas (megasena, lotofacil, quina, lotomania), independentemente de quantas dezenas cada uma tem.
- **Alternatives considered**: manter `auto-fill` (responsivo, mas o número de colunas varia com a largura da tela e da quantidade de dezenas — foi exatamente o comportamento que o usuário quis substituir); `repeat(auto-fit, ...)` (mesmo problema de `auto-fill`, não fixa em 10). Nenhuma alternativa foi escolhida — a mudança solicitada é direta.

## 2. Remoção da tabela de referência de preços: UI-only vs. remover também o backend

- **Decision**: remover apenas o consumo/exibição na UI (`jogos.jsp` + o campo `ConfigLoteria.precos` em `PaginasController` que só alimentava essa UI). `PremioService.tabelaReferencia(Loteria)` e o endpoint `GET /api/loterias/{loteria}/precos` permanecem intactos.
- **Rationale**: o pedido do usuário foi sobre a experiência visual ("não ficou muito bom", "não faz muito sentido para a proposta"), não sobre o cálculo em si. O endpoint já é uma API pública documentada em `CLAUDE.md` e pode ter outros consumidores futuros (ex. a aba `/ml` mencionada no CLAUDE.md como placeholder). Removê-lo seria escopo maior que o pedido e potencialmente destrutivo sem necessidade.
- **Alternatives considered**: remover também `PremioService.tabelaReferencia` e o endpoint REST — descartado por ampliar o escopo além do pedido e por risco de quebrar futuros consumidores sem benefício adicional (a UI já para de exibir os dados só removendo o HTML/JS/atributo de model).
