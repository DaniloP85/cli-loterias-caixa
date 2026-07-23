# Implementation Plan: Destaque de acertos do concurso mais recente nos cards de teimosinha

**Branch**: `005-destaque-acertos-pendentes` | **Date**: 2026-07-23 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/005-destaque-acertos-pendentes/spec.md`

## Summary

Nos cards de teimosinha em `/jogos` (`jogos.jsp`, feature 004), as "dezenas-grandes" hoje mostram só os números jogados, sem indicar acertos. Passam a destacar as dezenas que coincidiram com o resultado do **concurso mais recente já apurado** dentro do intervalo da teimosinha — mas só enquanto ela ainda tiver concursos pendentes (FR-001 a FR-006). Reaproveita a mesma varredura de concursos (`conferirConcursos`) já feita hoje em `JogoService.listarComResumo()` para montar `resumo`/`custoTotal`/`ganhoTotal`; nenhuma chamada nova à API da Caixa ou ao Mongo é necessária — só um novo campo derivado em `JogoComResumo`. A cor do destaque reaproveita `.badge.premiado` (fundo/texto claros) e — decisão de design tomada durante a clarificação — a classe já existente `.dezena.acertada` (usada hoje na tabela do painel de sorteios premiados e em `/jogos/{id}`) é recolorida para essas mesmas cores mais suaves (FR-007), permitindo que o card reutilize essa classe em vez de criar uma nova.

## Technical Context

**Language/Version**: Java 17 (Spring Boot), JSP (Jakarta EL/JSTL), CSS puro, JS vanilla embutido em `jogos.jsp`

**Primary Dependencies**: Spring Boot Web + MVC (JSP view resolver), nenhuma dependência nova

**Storage**: N/A — feature não altera nenhum documento persistido no MongoDB; o destaque é derivado na leitura a partir de dados já computados por `JogoService.conferirConcursos(jogo)`

**Testing**: Não há suíte automatizada no repo (ver `CLAUDE.md`); validação manual via `quickstart.md`

**Target Platform**: Navegador desktop e mobile, servido pelo Tomcat embutido do WAR

**Project Type**: Web monolith (single project) — mudança concentrada em `JogoService`, `JogoComResumo`, `jogos.jsp`, `estilo.css`

**Performance Goals**: N/A — mudança de apresentação; nenhuma varredura ou chamada nova é adicionada (o cálculo do concurso mais recente apurado usa a lista de `ConferenciaConcurso` que `listarComResumo()` já monta para cada jogo)

**Constraints**: O destaque só pode aparecer enquanto `resumo.pendentes > 0` (FR-004); a cor de destaque nas dezenas grandes não pode exibir o número do concurso de origem (Clarifications); a suavização de `.dezena.acertada` é global — afeta também `/jogos/{id}` (Clarifications), diferente da restrição de escopo que vale só para a regra de destaque nas dezenas grandes (US1)

**Scale/Scope**: 1 service (`JogoService`: novo cálculo derivado, sem nova consulta), 1 DTO alterado (`JogoComResumo`: novo campo), 1 JSP (`jogos.jsp`: classe condicional nas dezenas grandes), 1 CSS (recolorir `.dezena.acertada`) — sem novos endpoints REST, sem novas entidades persistidas

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

O arquivo `.specify/memory/constitution.md` deste projeto ainda está no estado placeholder do template (nenhum princípio foi ratificado) — não há gates de constituição aplicáveis. Nenhuma violação a justificar.

## Project Structure

### Documentation (this feature)

```text
specs/005-destaque-acertos-pendentes/
├── plan.md              # This file (/speckit-plan command output)
├── research.md          # Phase 0 output (/speckit-plan command)
├── data-model.md        # Phase 1 output (/speckit-plan command)
├── quickstart.md        # Phase 1 output (/speckit-plan command)
├── contracts/           # Phase 1 output (/speckit-plan command)
└── tasks.md              # Phase 2 output (/speckit-tasks command - NOT created by /speckit-plan)
```

`contracts/` incluído porque `GET /api/jogos` (`JogoRestController.listar()` → `JogoComResumo`) ganha um campo novo na resposta.

### Source Code (repository root)

Projeto único (web monolith Spring Boot existente — ver `CLAUDE.md`). Nenhuma estrutura nova; a mudança toca apenas arquivos já existentes:

```text
src/main/java/br/com/dpsnqmk/service/JogoService.java       # listarComResumo(): deriva dezenasAcertadasUltimoConcurso a partir de concursos já computados + resumo.pendentes
src/main/java/br/com/dpsnqmk/dto/JogoComResumo.java          # novo campo: List<Integer> dezenasAcertadasUltimoConcurso
src/main/webapp/WEB-INF/jsp/jogos.jsp                        # dezenas-grandes: classe condicional "acertada" quando numero ∈ dezenasAcertadasUltimoConcurso
src/main/resources/static/css/estilo.css                      # .dezena.acertada: recolorir para o par de cores de .badge.premiado (fundo/texto claros, mais suave que o atual)
```

**Structure Decision**: Nenhuma reestruturação — edições pontuais nos arquivos acima, dentro do monolito existente (Option 1 / single project, já em uso pelo repositório). Nenhum novo endpoint REST é criado; o destaque reaproveita 100% da varredura de concursos que `listarComResumo()` já faz hoje.

## Complexity Tracking

*Sem violações de constituição a justificar — seção não aplicável.*
