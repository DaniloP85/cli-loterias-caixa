# Implementation Plan: Destaque persistente e paginação de concursos em cards de teimosinha

**Branch**: `006-navegacao-acertos-concurso` | **Date**: 2026-07-23 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/006-navegacao-acertos-concurso/spec.md`

## Summary

Estende a feature 005 (`JogoComResumo.dezenasAcertadasUltimoConcurso`, hoje só visível enquanto `resumo.pendentes > 0`) para: (1) remover essa restrição — o destaque do concurso mais recente apurado passa a aparecer também em teimosinhas expiradas; (2) expor qual concurso está sendo comparado via um novo campo `concursoComparado` em `JogoComResumo`; (3) adicionar, apenas para teimosinhas (`concursoInicial != concursoFinal`), paginação voltar/avançar entre os concursos já apurados do intervalo. A paginação é resolvida inteiramente no cliente reaproveitando o endpoint `GET /api/jogos/{id}/conferencia` já existente (usado hoje pelo painel "Sorteios premiados"), que já retorna todos os concursos do intervalo com `situacao` e `dezenasAcertadas` — nenhum endpoint REST novo é necessário. O estado inicial de cada card (concurso mais recente apurado, com destaque e rótulo) continua renderizado no servidor, como hoje; a navegação é um estado momentâneo em JavaScript, sem persistência.

## Technical Context

**Language/Version**: Java 17 (Spring Boot), JSP (Jakarta EL/JSTL), CSS puro, JS vanilla embutido em `jogos.jsp`

**Primary Dependencies**: Spring Boot Web + MVC (JSP view resolver) + Jackson (serialização do `GET /api/jogos/{id}/conferencia` já usado pelo JS); nenhuma dependência nova

**Storage**: N/A — feature não altera nenhum documento persistido no MongoDB; todo o dado (destaque, concurso comparado, lista de concursos apurados para paginação) é derivado na leitura a partir de dados já computados por `JogoService.conferirConcursos(jogo)` / `JogoService.conferir(id)`

**Testing**: Não há suíte automatizada no repo (ver `CLAUDE.md`); validação manual via `quickstart.md`

**Target Platform**: Navegador desktop e mobile, servido pelo Tomcat embutido do WAR

**Project Type**: Web monolith (single project) — mudança concentrada em `JogoMongoDTO`, `JogoComResumo`, `JogoService`, `jogos.jsp`, `estilo.css`

**Performance Goals**: N/A — mudança de apresentação; a paginação reaproveita o `GET /api/jogos/{id}/conferencia` que já existe (era usado só pelo painel de premiados), sem nova consulta ao Mongo além da que esse endpoint já fazia; o fetch passa a ser disparado também no primeiro clique de voltar/avançar de um card, além do clique no badge "premiados"

**Constraints**: Os controles de paginação só existem para teimosinhas (`FR-004/FR-005/FR-006`); a paginação nunca avança para um concurso pendente (`FR-009`); o estado de navegação não é persistido — recarregar a página sempre volta ao concurso mais recente apurado (`FR-010`); a mudança em `.dezena.acertada`/`.badge.premiado` feita em 005 é reaproveitada sem alteração de cor

**Scale/Scope**: 1 DTO com getter derivado novo (`JogoMongoDTO.isTeimosinha()`), 1 DTO com campo novo (`JogoComResumo.concursoComparado`), 1 service ajustado (`JogoService`: remove o gate de pendentes e passa a derivar também o concurso comparado), 1 JSP (`jogos.jsp`: rótulo + controles de paginação + JS de navegação client-side reaproveitando o fetch existente), 1 CSS (nova regra de layout para a linha rótulo+controles, sem novas cores) — sem novos endpoints REST, sem novas entidades persistidas

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

O arquivo `.specify/memory/constitution.md` deste projeto ainda está no estado placeholder do template (nenhum princípio foi ratificado) — não há gates de constituição aplicáveis. Nenhuma violação a justificar.

## Project Structure

### Documentation (this feature)

```text
specs/006-navegacao-acertos-concurso/
├── plan.md              # This file (/speckit-plan command output)
├── research.md          # Phase 0 output (/speckit-plan command)
├── data-model.md        # Phase 1 output (/speckit-plan command)
├── quickstart.md        # Phase 1 output (/speckit-plan command)
├── contracts/           # Phase 1 output (/speckit-plan command)
└── tasks.md             # Phase 2 output (/speckit-tasks command - NOT created by /speckit-plan)
```

`contracts/` incluído porque `GET /api/jogos` (`JogoRestController.listar()` → `JogoComResumo`) ganha um campo novo (`concursoComparado`) e o `jogo` aninhado (`JogoMongoDTO`) ganha um getter derivado novo (`teimosinha`) — ambos afetam a resposta JSON consumida pelo cliente.

### Source Code (repository root)

Projeto único (web monolith Spring Boot existente — ver `CLAUDE.md`). Nenhuma estrutura nova; a mudança toca apenas arquivos já existentes:

```text
src/main/java/br/com/dpsnqmk/dto/JogoMongoDTO.java            # novo getter derivado: isTeimosinha() (quantidadeConcursos > 1)
src/main/java/br/com/dpsnqmk/dto/JogoComResumo.java           # novo campo: Integer concursoComparado (null quando nenhum concurso apurado)
src/main/java/br/com/dpsnqmk/service/JogoService.java         # listarComResumo(): remove o gate de pendentes==0; deriva concursoComparado junto com dezenasAcertadasUltimoConcurso
src/main/webapp/WEB-INF/jsp/jogos.jsp                          # rótulo "comparação com o concurso X" / "aguardando apuração" + controles voltar/avançar (SSR do estado inicial); JS reaproveita fetch de /api/jogos/{id}/conferencia para paginar no cliente
src/main/resources/static/css/estilo.css                       # nova regra de layout (flex) para a linha rótulo+controles; reaproveita .botao/.botao:disabled já existentes, sem cor nova
```

**Structure Decision**: Nenhuma reestruturação — edições pontuais nos arquivos acima, dentro do monolito existente (Option 1 / single project, já em uso pelo repositório). Nenhum novo endpoint REST é criado; a paginação reaproveita 100% o `GET /api/jogos/{id}/conferencia` que já existe desde a feature 004 (painel "Sorteios premiados").

## Complexity Tracking

*Sem violações de constituição a justificar — seção não aplicável.*
