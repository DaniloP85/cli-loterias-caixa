# Implementation Plan: Layout em cards na página de conferência de jogos

**Branch**: `004-cards-conferencia-jogos` | **Date**: 2026-07-22 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/004-cards-conferencia-jogos/spec.md`

## Summary

Reformula a lista de jogos cadastrados em `/jogos` (`jogos.jsp`, já reduzida à página de conferência pela feature 003) de tabela para uma grade de cards (máx. 4 por linha), reaproveitando classes CSS já existentes (`.card`, `.dezena.grande`) sempre que possível. Cada card passa a mostrar: título "loteria de X até Y", dezenas em destaque, descrição, **custo total da teimosinha** (unitário × `quantidadeConcursos` — hoje só mostra o unitário) e **ganho total** (soma dos prêmios da própria teimosinha — campo novo). O badge "premiado" vira um controle que abre/fecha/troca o conteúdo de um único painel de sorteios premiados compartilhado, abaixo da grade, populado via o endpoint já existente `GET /api/jogos/{id}/conferencia` (sem novo endpoint). A tabela combinada "Sorteios premiados" (todos os jogos juntos) e o método `JogoService.resultadosSorteios()`/DTO `ResultadoSorteio` que a alimentava são removidos, por ficarem sem uso.

## Technical Context

**Language/Version**: Java 17 (Spring Boot), JSP (Jakarta EL/JSTL), CSS puro, JS vanilla embutido em `jogos.jsp`

**Primary Dependencies**: Spring Boot Web + MVC (JSP view resolver), nenhuma dependência nova

**Storage**: N/A — feature não altera nenhum documento persistido no MongoDB; custo total e ganho total são calculados na leitura a partir de dados já existentes (`JogoMongoDTO.quantidadeConcursos`, `PremioService`)

**Testing**: Não há suíte automatizada no repo (ver `CLAUDE.md`); validação manual via `quickstart.md`

**Target Platform**: Navegador desktop e mobile, servido pelo Tomcat embutido do WAR

**Project Type**: Web monolith (single project) — mudança concentrada em `jogos.jsp`, `estilo.css`, `JogoService`, `JogoComResumo`, `PaginasController`; remoção de `ResultadoSorteio`/`resultadosSorteios()` por ficarem sem uso

**Performance Goals**: N/A — mudança de apresentação; o cálculo de ganho total reaproveita a mesma varredura de concursos já feita hoje por `listarComResumo()` (não adiciona nenhuma chamada nova à API da Caixa por jogo)

**Constraints**: Grade de cards nunca deve exceder 4 colunas por linha, mesmo em telas largas (FR-001); página de detalhe `/jogos/{id}` (`jogo.jsp`) não pode ser afetada — nenhuma classe CSS nova pode vazar para ela (Assumption do spec)

**Scale/Scope**: 1 arquivo JSP (reescrita da seção de lista + novo painel + JS), 1 arquivo CSS (grade de cards + botão de exclusão + badge clicável), 1 service (`JogoService`: novo cálculo de custo/ganho total, remoção de `resultadosSorteios()`), 1 DTO alterado (`JogoComResumo`), 1 DTO removido (`ResultadoSorteio`), 1 controller (remove atributo de model não mais usado) — sem novos endpoints REST, sem novas entidades persistidas

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

O arquivo `.specify/memory/constitution.md` deste projeto ainda está no estado placeholder do template (nenhum princípio foi ratificado) — não há gates de constituição aplicáveis. Nenhuma violação a justificar.

## Project Structure

### Documentation (this feature)

```text
specs/004-cards-conferencia-jogos/
├── plan.md              # This file (/speckit-plan command output)
├── research.md          # Phase 0 output (/speckit-plan command)
├── data-model.md        # Phase 1 output (/speckit-plan command)
├── quickstart.md        # Phase 1 output (/speckit-plan command)
├── contracts/           # Phase 1 output (/speckit-plan command)
└── tasks.md              # Phase 2 output (/speckit-tasks command - NOT created by /speckit-plan)
```

`contracts/` incluído porque `GET /api/jogos` (consumido também por integrações externas ao JSP, ver `JogoRestController`) muda de forma: o campo `custoAposta` passa a representar o total da teimosinha (renomeado para `custoTotal`) e um novo campo `ganhoTotal` é adicionado. `GET /api/jogos/{id}/conferencia` é reaproveitado sem nenhuma mudança de forma — só ganha um novo consumidor (o painel de sorteios premiados em JS).

### Source Code (repository root)

Projeto único (web monolith Spring Boot existente — ver `CLAUDE.md`). Nenhuma estrutura nova; a mudança toca apenas arquivos já existentes, mais a remoção de um DTO que fica sem uso:

```text
src/main/webapp/WEB-INF/jsp/jogos.jsp                                  # tabela → grade de cards; remove seção "Sorteios premiados" combinada; novo painel único + JS de toggle/troca
src/main/resources/static/css/estilo.css                               # .grade-jogos (grid, máx. 4 colunas), .botao-fechar (X do card), badge premiado clicável (escopado, sem afetar jogo.jsp)
src/main/java/br/com/dpsnqmk/service/JogoService.java                  # listarComResumo(): custoTotal = unitário × quantidadeConcursos; novo cálculo de ganhoTotal reaproveitando a varredura já feita para o resumo; remove resultadosSorteios()
src/main/java/br/com/dpsnqmk/dto/JogoComResumo.java                    # custoAposta renomeado para custoTotal; novo campo ganhoTotal
src/main/java/br/com/dpsnqmk/dto/ResultadoSorteio.java                 # removido — único consumidor (resultadosSorteios()) deixa de existir
src/main/java/br/com/dpsnqmk/controller/web/PaginasController.java     # jogos(): remove model.addAttribute("resultados", ...)
```

**Structure Decision**: Nenhuma reestruturação — edições pontuais nos arquivos acima, dentro do monolito existente (Option 1 / single project, já em uso pelo repositório). Nenhum novo endpoint REST é criado; o painel de sorteios premiados por teimosinha reaproveita `GET /api/jogos/{id}/conferencia`, que já retorna tudo que o painel precisa (dezenas jogadas, dezenas acertadas, prêmio por concurso).

## Complexity Tracking

*Sem violações de constituição a justificar — seção não aplicável.*
