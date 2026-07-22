# Phase 1 Data Model: Ajustes de UI na página de jogos

Sem novas entidades de dados. Esta feature é puramente de apresentação (CSS + marcação JSP) e não introduz, altera nem remove nenhum DTO, documento Mongo ou modelo de domínio.

## Impacto em modelos existentes

- **`PaginasController.ConfigLoteria`** (view model interno, não persistido): o campo `List<PrecoAposta> precos` deixa de ser necessário, pois seu único consumidor era o atributo `data-precos` em `jogos.jsp`, usado para montar a tabela de referência de preços que está sendo removida (US2). O campo (e o parâmetro correspondente no construtor/mapeamento em `PaginasController`) deve ser removido para não deixar código morto (FR-005).
- **`PrecoAposta`** (`dto/PrecoAposta.java`) e **`PremioService.tabelaReferencia(Loteria)`**: permanecem inalterados — continuam existindo e sendo usados pelo endpoint `GET /api/loterias/{loteria}/precos` (FR-006). Apenas deixam de ser referenciados a partir de `PaginasController`/`jogos.jsp`.
- Nenhum outro DTO (`JogoComResumo`, `ConferenciaConcurso`, `ResultadoSorteio`, etc.) é afetado — a coluna "custo da aposta" já exibida na lista de jogos cadastrados usa um caminho de dados diferente (`JogoComResumo.custoAposta`, populado via `PremioService.custoAposta`) e não é tocada por esta feature (FR-004).
