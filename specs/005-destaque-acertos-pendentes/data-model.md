# Phase 1 Data Model: Destaque de acertos do concurso mais recente nos cards de teimosinha

Nenhuma entidade persistida no MongoDB é criada, alterada ou removida. A única mudança é um campo novo em um DTO de apresentação (view model), derivado na leitura a partir de dados já calculados.

## `JogoComResumo` (`dto/JogoComResumo.java`)

View model já existente, retornado por `JogoService.listarComResumo()` e usado tanto por `PaginasController.jogos()` (renderização em `jogos.jsp`) quanto por `JogoRestController.listar()` (`GET /api/jogos`).

| Campo | Antes | Depois | Notas |
|---|---|---|---|
| `jogo` | `JogoMongoDTO` | *(sem mudança)* | |
| `resumo` | `ResumoJogo` | *(sem mudança)* | `resumo.pendentes` passa a também controlar se `dezenasAcertadasUltimoConcurso` vem populado (FR-004) |
| `custoTotal` | `BigDecimal` | *(sem mudança)* | |
| `ganhoTotal` | `BigDecimal` | *(sem mudança)* | |
| `dezenasAcertadasUltimoConcurso` | *(não existia)* | `List<Integer>` — dezenas jogadas que coincidiram com o resultado do concurso mais recente já apurado dentro do intervalo da teimosinha | FR-001/FR-002/FR-006. Vazio (`List.of()`) quando `resumo.pendentes == 0` (teimosinha expirada, FR-004) ou quando nenhum concurso do intervalo foi apurado ainda (FR-005). Ver `research.md` §1-2. |

## `JogoService`

- `listarComResumo()`: para cada jogo, a mesma lista de `ConferenciaConcurso` (`conferirConcursos(jogo)`) que já alimenta `resumo`/`ganhoTotal` passa a alimentar também `dezenasAcertadasUltimoConcurso`, via nova função privada (ex. `dezenasAcertadasUltimoConcurso(List<ConferenciaConcurso>, ResumoJogo)`, espelhando o padrão de `resumo(...)`/`ganhoTotal(...)`).
- Nenhuma outra função de `JogoService` muda.

## `jogos.jsp` — dezenas grandes do card

| Antes | Depois |
|---|---|
| `<span class="dezena grande">${numero}</span>` para toda dezena jogada, sem distinção | `<span class="dezena grande ${item.dezenasAcertadasUltimoConcurso.contains(numero) ? 'acertada' : ''}">${numero}</span>` — mesma classe `acertada` já usada no painel de sorteios premiados e em `jogo.jsp` (ver `research.md` §3) |

## `estilo.css`

| Regra | Antes | Depois |
|---|---|---|
| `.dezena.acertada` | `background: #166534; color: #fff;` | `background: #dcfce7; color: #166534;` (mesmo par de `.badge.premiado`) — afeta igualmente a coluna "Dezenas jogadas/sorteadas (acertos destacados)" do painel de sorteios premiados e de `/jogos/{id}` (FR-007, Clarifications) |

Nenhum campo, DTO ou consulta relacionados a `ConferenciaConcurso`, `ResumoJogo`, `Loteria` ou `PremioService` é alterado.
