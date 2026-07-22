# Implementation Plan: Separar "Meus jogos" em página de cadastro e página de conferência

**Branch**: `003-cadastro-conferencia-jogos` | **Date**: 2026-07-22 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/003-cadastro-conferencia-jogos/spec.md`

## Summary

Divide a página única `/jogos` (`jogos.jsp`) em duas páginas de apresentação, sem novas dependências, sem mudança de API/dados: (1) uma nova página `GET /jogos/cadastro` (`jogos-cadastro.jsp`) que hospeda apenas o formulário de cadastro de jogo (loteria, volante, concurso inicial, quantidade de concursos, descrição) — ao salvar com sucesso, permanece na página com confirmação inline + link para a conferência, sem `location.reload()`; (2) a rota existente `GET /jogos` (`jogos.jsp`, reduzida) passa a ser a página de conferência — lista de jogos cadastrados + tabela "Sorteios premiados" — sem o formulário/volante. A navegação principal (`cabecalho.jspf`) ganha duas entradas ("Cadastrar jogo" e "Conferir jogos") no lugar da única aba "Meus jogos". A página de detalhe `/jogos/{id}` (`jogo.jsp`) não muda de conteúdo, apenas passa a ser associada à aba de conferência.

## Technical Context

**Language/Version**: Java 17 (Spring Boot), JSP (Jakarta EL/JSTL), CSS puro, JS vanilla embutido nas páginas

**Primary Dependencies**: Spring Boot Web + MVC (JSP view resolver), nenhuma dependência nova

**Storage**: N/A — feature não toca em MongoDB, `JogoMongoDTO` ou qualquer repositório

**Testing**: Não há suíte automatizada no repo (ver CLAUDE.md); validação manual via `quickstart.md`

**Target Platform**: Navegador desktop e mobile, servido pelo Tomcat embutido do WAR

**Project Type**: Web monolith (single project) — mudança restrita a `PaginasController`, duas views JSP (`jogos.jsp` reduzida + nova `jogos-cadastro.jsp`), `jogo.jsp` (valor de `abaAtiva`) e `comum/cabecalho.jspf` (nav)

**Performance Goals**: N/A — mudança de marcação/roteamento estático, sem impacto de performance mensurável

**Constraints**: Nenhuma mudança de API, DTO persistido ou regra de premiação/conferência (FR-010); a rota `GET /jogos` continua respondendo (reaproveitada como página de conferência, satisfazendo FR-011 sem necessidade de redirect); comportamento de exclusão de jogo (`DELETE /api/jogos/{id}`) inalterado (FR-005); volante/JS de seleção de dezenas preservado integralmente, apenas movido para a nova página de cadastro

**Scale/Scope**: 1 classe controller (1 `@GetMapping` novo + 1 existente com model reduzido), 1 arquivo JSP reduzido (`jogos.jsp`), 1 arquivo JSP novo (`jogos-cadastro.jsp`), 1 arquivo JSP com 1 atributo ajustado (`jogo.jsp` via controller), 1 include (`cabecalho.jspf`) — sem novos endpoints REST, sem novas entidades

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

O arquivo `.specify/memory/constitution.md` deste projeto ainda está no estado placeholder do template (nenhum princípio foi ratificado) — não há gates de constituição aplicáveis. Nenhuma violação a justificar.

## Project Structure

### Documentation (this feature)

```text
specs/003-cadastro-conferencia-jogos/
├── plan.md              # This file (/speckit-plan command output)
├── research.md          # Phase 0 output (/speckit-plan command)
├── data-model.md        # Phase 1 output (/speckit-plan command)
├── quickstart.md        # Phase 1 output (/speckit-plan command)
└── tasks.md             # Phase 2 output (/speckit-tasks command - NOT created by /speckit-plan)
```

Sem `contracts/`: a feature não cria, muda nem remove nenhum contrato de API REST — `ConcursoRestController` e `JogoRestController` permanecem exatamente como estão (FR-010); a mudança é inteiramente de roteamento de páginas JSP (`PaginasController`) e apresentação.

### Source Code (repository root)

Projeto único (web monolith Spring Boot existente — ver CLAUDE.md). Nenhuma estrutura nova; a mudança toca apenas arquivos já existentes e adiciona uma view JSP nova:

```text
src/main/java/br/com/dpsnqmk/controller/web/PaginasController.java   # novo @GetMapping("/jogos/cadastro"); jogos() (GET /jogos) reduz o model (sem `loterias`); jogo() (GET /jogos/{id}) passa abaAtiva="jogos-conferencia"
src/main/webapp/WEB-INF/jsp/jogos.jsp                                 # reduzida: remove o card "Cadastrar jogo" (form + volante + tabela-precos + script de volante); vira a página de conferência (lista de jogos + Sorteios premiados); empty-state passa a linkar para /jogos/cadastro
src/main/webapp/WEB-INF/jsp/jogos-cadastro.jsp                        # NOVA: card "Cadastrar jogo" (form + volante), script de montar volante/alternar/contador/cadastrar migrado daqui; cadastrar() success handler troca location.reload() por confirmação inline + link para /jogos
src/main/webapp/WEB-INF/jsp/jogo.jsp                                  # sem mudança de marcação (abaAtiva vem do controller)
src/main/webapp/WEB-INF/jsp/comum/cabecalho.jspf                      # troca a aba única "Meus jogos" por duas entradas: "Cadastrar jogo" (/jogos/cadastro, aba jogos-cadastro) e "Conferir jogos" (/jogos, aba jogos-conferencia)
```

**Structure Decision**: Nenhuma reestruturação de projeto — divisão de uma view JSP existente em duas, dentro do monolito existente (Option 1 / single project, já em uso pelo repositório). `GET /jogos` é reaproveitada como página de conferência (em vez de introduzir um novo path e redirecionar a rota antiga), o que satisfaz FR-011 (endereço antigo continua funcionando) sem lógica de redirect adicional.

## Complexity Tracking

*Sem violações de constituição a justificar — seção não aplicável.*
