# Phase 1 Data Model: Layout em cards na página de conferência de jogos

Nenhuma entidade persistida no MongoDB é criada, alterada ou removida. Todas as mudanças abaixo são em DTOs de apresentação (view models) e em como são calculados na leitura.

## `JogoComResumo` (`dto/JogoComResumo.java`)

View model já existente, retornado por `JogoService.listarComResumo()` e usado tanto por `PaginasController.jogos()` (renderização em `jogos.jsp`) quanto por `JogoRestController.listar()` (`GET /api/jogos`).

| Campo | Antes | Depois | Notas |
|---|---|---|---|
| `jogo` | `JogoMongoDTO` | *(sem mudança)* | |
| `resumo` | `ResumoJogo` | *(sem mudança)* | |
| `custoAposta` | `BigDecimal` — valor de **uma** aposta | renomeado para `custoTotal` — valor **da teimosinha inteira** (`custoUnitário × quantidadeConcursos`) | FR-006. Fonte do valor unitário (`PremioService.custoAposta`) não muda. |
| `ganhoTotal` | *(não existia)* | `BigDecimal` — soma dos valores de prêmio de todos os concursos `PREMIADO` da teimosinha, `0` se nenhum | FR-007. Calculado em `JogoService.listarComResumo()` a partir da mesma varredura de concursos já feita para montar `resumo` (ver `research.md` §5). Concursos com prêmio `SEM_GANHADOR`/`INDISPONIVEL` não contribuem (Edge Cases do spec). |

## `ResultadoSorteio` (`dto/ResultadoSorteio.java`)

**Removido.** Era o DTO usado só pela tabela combinada "Sorteios premiados" (todos os jogos), que deixa de existir (FR-012). Nenhum outro consumidor no código (confirmado em `research.md` §7).

## `JogoService`

- `listarComResumo()`: para cada jogo, a mesma chamada a `conferirConcursos(jogo)` que hoje alimenta `resumo(...)` passa a alimentar também o novo cálculo de `ganhoTotal` (nova função privada, ex. `ganhoTotal(List<ConferenciaConcurso>)`, espelhando `resumo(...)`); `custoTotal` passa a multiplicar o valor unitário (`premioService.custoAposta(...)`) por `jogo.getQuantidadeConcursos()`.
- `resultadosSorteios()`: **removido** (sem consumidores após a remoção da tabela combinada).

## `PaginasController`

- `jogos()` (`GET /jogos`): remove `model.addAttribute("resultados", jogoService.resultadosSorteios())`. Continua populando `jogos` (agora com `custoTotal`/`ganhoTotal`) e `abaAtiva`.

## Painel de sorteios premiados por teimosinha (novo, só no cliente/JS)

Não é uma entidade nova do lado do servidor — é uma projeção, no JavaScript de `jogos.jsp`, da resposta já existente de `GET /api/jogos/{id}/conferencia` (`ConferenciaJogo` → `jogo` + `resumo` + `concursos: List<ConferenciaConcurso>`), filtrando `concursos` onde `situacao === 'PREMIADO'`. Ver `research.md` §6 e `contracts/api-jogos.md`.

## `jogos.jsp` — de tabela para grade de cards

| Antes (coluna da tabela) | Depois (card) |
|---|---|
| `Loteria` + `Concursos` (colunas separadas) | Título do card: `"${item.jogo.loteria} de ${item.jogo.concursoInicial} até ${item.jogo.concursoFinal}"` |
| `Dezenas` (coluna estreita, `.dezena`) | Corpo do card, `.dezena.grande` (reaproveitada, ver `research.md` §2) |
| `Descrição` | Bloco de descrição no corpo do card (`#descrição` do mockup) |
| `Custo` (`item.custoAposta`) | `item.custoTotal` (FR-006) |
| `Resumo` (badges) | Mesmos badges (`#resumo` do mockup); badge `premiado` ganha `onclick` + classe `.clicavel` (FR-009) |
| Botão "excluir" (coluna própria) | Controle "×" no cabeçalho do card (`.botao-fechar`, mesma função `excluir(id)`) |
| *(tabela combinada "Sorteios premiados" abaixo)* | Painel único `#painel-premiados` (oculto por padrão), populado sob demanda (FR-009/FR-010) |
