# Implementation Plan: Ajustes de UI na página de jogos (grid do volante e remoção da tabela de preços)

**Branch**: `002-ajustes-ui-jogos` | **Date**: 2026-07-22 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/002-ajustes-ui-jogos/spec.md`

## Summary

Duas mudanças de apresentação na página "Meus jogos" (`/jogos`, `jogos.jsp`), sem novas dependências nem mudanças de dados: (1) trocar `grid-template-columns` do seletor `.volante` em `estilo.css` de `repeat(auto-fill, minmax(2.6rem, 1fr))` para `repeat(10, minmax(2.6rem, 1fr))`; (2) remover a seção de tabela de referência de preços de `jogos.jsp` (título, `<table>`, script `montarTabelaPrecos()` e o atributo `data-precos` que só alimenta essa tabela), incluindo a limpeza correspondente no lado servidor (`ConfigLoteria.precos` em `PaginasController`), sem tocar em `PremioService.tabelaReferencia` nem no endpoint `GET /api/loterias/{loteria}/precos`.

## Technical Context

**Language/Version**: Java 17 (Spring Boot), JSP (Jakarta EL/JSTL), CSS puro, JS vanilla embutido em `jogos.jsp`

**Primary Dependencies**: Spring Boot Web + MVC (JSP view resolver), nenhuma dependência nova

**Storage**: N/A — feature não toca em MongoDB nem em DTOs persistidos

**Testing**: Não há suíte automatizada no repo (ver CLAUDE.md); validação manual via `quickstart.md`

**Target Platform**: Navegador desktop e mobile, servido pelo Tomcat embutido do WAR

**Project Type**: Web monolith (single project) — mudança restrita a `src/main/resources/static/css/estilo.css`, `src/main/webapp/WEB-INF/jsp/jogos.jsp` e `PaginasController` (limpeza do atributo de model não mais usado)

**Performance Goals**: N/A — mudança de CSS/marcação estática, sem impacto de performance mensurável

**Constraints**: Preservar `min-width: 2.6rem` por célula do volante (FR-002); não remover/alterar `PremioService.tabelaReferencia` nem o endpoint `/precos` (FR-006)

**Scale/Scope**: 1 arquivo CSS (1 regra), 1 arquivo JSP (remoção de markup + JS), 1 classe controller (remoção de campo não mais consumido) — sem novos endpoints, sem novas entidades

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

O arquivo `.specify/memory/constitution.md` deste projeto ainda está no estado placeholder do template (nenhum princípio foi ratificado) — não há gates de constituição aplicáveis. Nenhuma violação a justificar.

## Project Structure

### Documentation (this feature)

```text
specs/002-ajustes-ui-jogos/
├── plan.md              # This file (/speckit-plan command output)
├── research.md          # Phase 0 output (/speckit-plan command)
├── data-model.md        # Phase 1 output (/speckit-plan command)
├── quickstart.md        # Phase 1 output (/speckit-plan command)
└── tasks.md             # Phase 2 output (/speckit-tasks command - NOT created by /speckit-plan)
```

Sem `contracts/`: a feature não cria, muda nem remove nenhum contrato de API — o endpoint `/api/loterias/{loteria}/precos` permanece exatamente como está (FR-006), apenas deixa de ser consumido pela página `/jogos`.

### Source Code (repository root)

Projeto único (web monolith Spring Boot existente — ver CLAUDE.md). Nenhuma estrutura nova; a mudança toca apenas arquivos já existentes:

```text
src/main/resources/static/css/estilo.css                              # regra .volante (grid-template-columns)
src/main/webapp/WEB-INF/jsp/jogos.jsp                                  # remove seção "tabela de preços" + JS montarTabelaPrecos() + atributo data-precos
src/main/java/br/com/dpsnqmk/controller/web/PaginasController.java     # remove campo ConfigLoteria.precos (não usado após a remoção acima)
```

**Structure Decision**: Nenhuma reestruturação — edições pontuais nos 3 arquivos acima, dentro do monolito existente (Option 1 / single project, já em uso pelo repositório).

## Complexity Tracking

*Sem violações de constituição a justificar — seção não aplicável.*
