---

description: "Task list for feature: Regras de Aposta e Premiação por Loteria"
---

# Tasks: Regras de Aposta e Premiação por Loteria

**Input**: Design documents from `/specs/001-regras-premiacao-loterias/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/api.md, quickstart.md

**Tests**: Not requested — this repository has no automated test framework configured (`CLAUDE.md`); validation is manual via `quickstart.md`, referenced as tasks below.

**Organization**: Tasks are grouped by user story (US1/US2/US3, priorities from `spec.md`) so each can be implemented and validated independently.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1, US2, US3)
- Paths are exact, relative to repo root (single Spring Boot project — see `plan.md` Project Structure)

---

## Phase 1: Setup

**Purpose**: Establish a working baseline before touching any file (no automated tests in this repo, so a clean build is the only automatic gate)

- [X] T001 [P] Confirm baseline build passes: `mvn clean package -DskipTests` (no code changes yet — just a pre-change checkpoint)

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Shared building block needed by **both** US1 (custo do jogo) and US3 (tabela de referência) — the per-lottery price table and the service method that reads it

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [X] T002 [P] Add tabela de preço por quantidade de dezenas (`BigDecimal`) ao enum em `src/main/java/br/com/dpsnqmk/enums/Loteria.java`, com os valores oficiais de `ideas/megasena.md`, `ideas/lotofacil.md`, `ideas/quina.md` e `ideas/lotomania.md` (seção 2 de cada); Lotomania recebe uma única entrada fixa (50 dezenas → R$ 3,00)
- [X] T003 Criar `src/main/java/br/com/dpsnqmk/service/PremioService.java` com `custoAposta(Loteria loteria, int quantidadeDezenas): BigDecimal`, lendo a tabela adicionada em T002 (depends on T002)

**Checkpoint**: Foundation ready — US1 e US3 podem começar (US2 é independente desta fase, mas também só pode começar depois dela por convenção de fases)

---

## Phase 3: User Story 1 - Ver o custo da aposta registrada (Priority: P1) 🎯 MVP

**Goal**: Mostrar, ao lado de cada jogo cadastrado em "Meus jogos" (inclusive PENDENTE), o custo da aposta calculado a partir da loteria e da quantidade de dezenas marcadas

**Independent Test**: Cadastrar um jogo em cada uma das 4 loterias com diferentes quantidades de dezenas e verificar que o valor exibido bate com a tabela oficial de preços de cada loteria (`quickstart.md` Cenário 1)

### Implementation for User Story 1

- [X] T004 [P] [US1] Adicionar campo `BigDecimal custoAposta` em `src/main/java/br/com/dpsnqmk/dto/JogoComResumo.java`
- [X] T005 [US1] Em `src/main/java/br/com/dpsnqmk/service/JogoService.java`, popular `custoAposta` em `listarComResumo()` chamando `PremioService.custoAposta(loteria, jogo.getNumeros().size())` (depends on T003, T004)
- [X] T006 [US1] Renderizar `custoAposta` (formatado como R$) como nova coluna na tabela de jogos em `src/main/webapp/WEB-INF/jsp/jogos.jsp` (depends on T005)
- [X] T007 [US1] Validar manualmente o Cenário 1 do `quickstart.md` (6, 7, 18 e 50 dezenas nas 4 loterias, incluindo um jogo PENDENTE) (depends on T006)

**Checkpoint**: User Story 1 completa e testável de forma independente — MVP entregável

---

## Phase 4: User Story 2 - Ver o valor do prêmio ganho (Priority: P2)

**Goal**: Mostrar o valor em reais do prêmio de cada jogo classificado como PREMIADO, usando o rateio oficial já publicado pela Caixa (persistido a partir de agora, e buscado sob demanda + cacheado para concursos legados)

**Independent Test**: Selecionar um concurso já importado com jogo(s) PREMIADO(S) e verificar que o valor do prêmio exibido corresponde ao valor oficial publicado pela Caixa para aquela faixa naquele concurso (`quickstart.md` Cenário 2)

### Implementation for User Story 2

- [X] T008 [P] [US2] Criar `src/main/java/br/com/dpsnqmk/dto/RateioPremioDTO.java` (`faixa`, `descricaoFaixa`, `numeroDeGanhadores`, `valorPremio`) e mapear `listaRateioPremio` em `src/main/java/br/com/dpsnqmk/dto/ConcursoDTO.java`, formato confirmado em `research.md` §1
- [X] T009 [P] [US2] Criar `src/main/java/br/com/dpsnqmk/dto/RateioPremioMongoDTO.java` e adicionar `List<RateioPremioMongoDTO> rateioPremios` (campo Mongo `rateio_premios`) em `src/main/java/br/com/dpsnqmk/dto/ConcursoMongoDTO.java`, separado de `historial`/`FeaturesDTO` (`research.md` §4, protege `FR-010`)
- [X] T010 [US2] Atualizar `converter(...)` em `src/main/java/br/com/dpsnqmk/service/ImportacaoService.java` para mapear `listaRateioPremio` → `rateioPremios`, garantindo que toda nova importação já persista o rateio (depends on T008, T009)
- [X] T011 [P] [US2] Criar `src/main/java/br/com/dpsnqmk/dto/PremioFaixa.java` (getters; `BigDecimal valor`, `String status` com valores `VALOR`/`SEM_GANHADOR`/`INDISPONIVEL`)
- [X] T012 [US2] Implementar `PremioService.valorPremio(ConcursoMongoDTO concurso, Loteria loteria, int acertos): PremioFaixa` em `src/main/java/br/com/dpsnqmk/service/PremioService.java`: casar por `descricaoFaixa` (`research.md` §2), retornar `SEM_GANHADOR` quando a entrada tiver `numeroDeGanhadores == 0`; se `rateioPremios` estiver vazio/nulo, buscar sob demanda via `HttpService.recuperarConcurso(urlBase + loteria.nome() + "/" + concurso)`, mapear e persistir via `ConcursoRepository.save(...)` (cache) antes de resolver; retornar `INDISPONIVEL` se a busca falhar (depends on T009, T011)
- [X] T013 [US2] Adicionar campo `PremioFaixa premio` em `src/main/java/br/com/dpsnqmk/dto/ConferenciaConcurso.java`; popular em `JogoService.conferirContra(...)` só quando `situacao == PREMIADO` (permanece `null` em `NAO_PREMIADO`/`PENDENTE`, por `FR-005`) (depends on T012)
- [X] T014 [US2] Adicionar campo `PremioFaixa premio` em `src/main/java/br/com/dpsnqmk/dto/ResultadoSorteio.java`; popular em `JogoService.resultadosSorteios()` (depends on T012)
- [X] T015 [US2] Renderizar `premio` (valor / "não houve ganhador nesta faixa" / "indisponível no momento") na tabela "Sorteios premiados" em `src/main/webapp/WEB-INF/jsp/jogos.jsp` (depends on T014)
- [X] T016 [US2] Validar manualmente o Cenário 2 do `quickstart.md`, incluindo um concurso legado sem `rateioPremios` persistido (busca sob demanda + cache) e uma falha simulada da API da Caixa (depends on T015)

**Checkpoint**: User Stories 1 e 2 funcionando de forma independente

---

## Phase 5: User Story 3 - Consultar tabela de preços por loteria antes de apostar (Priority: P3)

**Goal**: Mostrar, dentro da própria tela de cadastro de jogo, o custo da aposta para cada quantidade válida de dezenas da loteria selecionada

**Independent Test**: Abrir a tela de cadastro de jogo, selecionar cada loteria e conferir que a seção de referência mostra os valores corretos por quantidade de dezenas (`quickstart.md` Cenário 3)

### Implementation for User Story 3

- [X] T017 [P] [US3] Criar `src/main/java/br/com/dpsnqmk/dto/PrecoAposta.java` (getters; `int quantidadeDezenas`, `BigDecimal valor`)
- [X] T018 [US3] Implementar `PremioService.tabelaReferencia(Loteria loteria): List<PrecoAposta>` iterando `minDezenas..maxDezenas` (uma única entrada para Lotomania) reaproveitando `custoAposta` (depends on T003, T017)
- [X] T019 [P] [US3] Adicionar `GET /api/loterias/{loteria}/precos` em `src/main/java/br/com/dpsnqmk/controller/api/ConcursoRestController.java`, retornando `PremioService.tabelaReferencia(loteria)` (depends on T018)
- [X] T020 [US3] Estender `PaginasController.ConfigLoteria` (ou novo atributo de model) em `src/main/java/br/com/dpsnqmk/controller/web/PaginasController.java` para carregar a tabela de preços usada por `/jogos` (depends on T018)
- [X] T021 [US3] Adicionar seção de tabela de referência de preços em `src/main/webapp/WEB-INF/jsp/jogos.jsp`, próxima ao volante clicável, atualizando conforme a loteria selecionada (reaproveitar o padrão JS de `configurar()` já existente) (depends on T020)
- [X] T022 [US3] Validar manualmente o Cenário 3 do `quickstart.md` nas 4 loterias, incluindo o caso de valor único fixo da Lotomania (depends on T021, T019)

**Checkpoint**: As 3 user stories funcionando de forma independente

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Consistência, regressão e documentação

- [X] T023 [P] Renderizar `premio` também em `src/main/webapp/WEB-INF/jsp/jogo.jsp` (página de conferência de um jogo individual), por consistência com o novo campo em `ConferenciaConcurso` (depends on T013)
- [X] T024 [P] Confirmar que o export do dataset de ML (`DatasetService`/`LinhaDataset`, CSV e JSON) permanece inalterado — regressão de `FR-010` (`quickstart.md` Cenário 4)
- [X] T025 [P] Confirmar que "Sorteios premiados" lista uma linha por jogo (não soma valores) quando múltiplos jogos do usuário acertam o mesmo concurso/faixa (`quickstart.md` Cenário 4)
- [X] T026 Atualizar `CLAUDE.md` (seção de arquitetura) mencionando `PremioService` e o novo campo `rateioPremios` em `ConcursoMongoDTO`
- [X] T027 Rodar `mvn clean package -DskipTests` novamente para confirmar ausência de regressões antes de considerar a feature concluída (depends on all previous tasks)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: Sem dependências — pode começar imediatamente
- **Foundational (Phase 2)**: Depende de Setup — bloqueia US1 e US3 (US2 não depende desta fase, mas segue a ordem de fases por convenção)
- **User Stories (Phase 3-5)**: US1 e US3 dependem de Foundational; US2 é totalmente independente de US1/US3 e pode rodar em paralelo com elas
- **Polish (Phase 6)**: Depende de todas as user stories desejadas estarem completas (T023 depende especificamente de T013/US2)

### User Story Dependencies

- **US1 (P1)**: Depende só de Foundational (T002, T003) — nenhuma dependência de US2/US3
- **US2 (P2)**: Não depende de Foundational nem de US1/US3 — toda a cadeia (T008-T016) é autocontida
- **US3 (P3)**: Depende de Foundational (T003, especificamente `custoAposta`) — nenhuma dependência de US1/US2, mas reaproveita o mesmo `PremioService`

### Parallel Opportunities

- T001 (Setup) roda sozinho
- T002 (Foundational) não tem dependências, T003 depende de T002
- Depois de Foundational: US1 (Phase 3) e US2 (Phase 4) podem ser feitas em paralelo por pessoas diferentes; US3 (Phase 5) também, desde que T003 já exista
- Dentro de US2: T008 e T009 em paralelo (arquivos diferentes); T011 em paralelo com T008/T009
- Dentro de US3: T017 em paralelo com o restante até T018 precisar dele
- T023, T024, T025 (Polish) em paralelo entre si

---

## Parallel Example: User Story 2

```bash
# T008 e T009 em paralelo (arquivos diferentes, sem dependência entre si):
Task: "Criar RateioPremioDTO e mapear listaRateioPremio em ConcursoDTO.java"
Task: "Criar RateioPremioMongoDTO e adicionar rateioPremios em ConcursoMongoDTO.java"

# T011 também pode rodar em paralelo com as duas acima:
Task: "Criar PremioFaixa (dto/PremioFaixa.java)"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Completar Phase 1: Setup
2. Completar Phase 2: Foundational (bloqueia US1 e US3)
3. Completar Phase 3: User Story 1
4. **PARAR e VALIDAR**: Cenário 1 do `quickstart.md`
5. Já é um incremento entregável (custo visível em "Meus jogos")

### Incremental Delivery

1. Setup + Foundational → base pronta
2. US1 → validar independentemente → entregar (MVP)
3. US2 → validar independentemente → entregar (prêmio em reais)
4. US3 → validar independentemente → entregar (tabela de referência)
5. Polish → regressão de ML dataset + consistência de `jogo.jsp` + documentação

---

## Notes

- [P] = arquivos diferentes, sem dependência entre si
- Não há testes automatizados neste repositório — cada fase termina com uma validação manual referenciando `quickstart.md`
- `PremioService` é o único serviço novo; todo o resto é extensão de arquivos existentes
- FR-010 (dataset de ML intocado) e a regra "uma linha por jogo, não soma" só têm valor se verificadas depois que US2 estiver pronta — por isso ficam no Polish, não dentro de US2
