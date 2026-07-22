---

description: "Task list for feature: Ajustes de UI na página de jogos (grid do volante e remoção da tabela de preços)"
---

# Tasks: Ajustes de UI na página de jogos (grid do volante e remoção da tabela de preços)

**Input**: Design documents from `/specs/002-ajustes-ui-jogos/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, quickstart.md

**Tests**: Not requested — this repository has no automated test framework configured (`CLAUDE.md`); validação é manual via `quickstart.md`, referenciada como tasks abaixo.

**Organization**: Tasks agrupadas por user story (US1/US2, prioridades de `spec.md`); cada uma toca arquivos diferentes e é independentemente testável.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Pode rodar em paralelo (arquivos diferentes, sem dependência)
- **[Story]**: A qual user story a task pertence (US1, US2)
- Paths são exatos, relativos à raiz do repo (Spring Boot monolito único — ver `plan.md`)

---

## Phase 1: Setup

**Purpose**: Estabelecer uma baseline funcionando antes de tocar em qualquer arquivo (sem testes automatizados neste repo, então um build limpo é o único gate automático)

- [X] T001 Confirmar que a baseline builda: `mvn clean package -DskipTests` (sem mudanças de código ainda — só checkpoint pré-mudança)

---

## Phase 2: Foundational

**Purpose**: N/A — não há pré-requisito bloqueante compartilhado entre US1 e US2 nesta feature. US1 toca só `estilo.css`; US2 toca `jogos.jsp` e `PaginasController.java`. Nenhum arquivo ou infraestrutura nova é compartilhada entre as duas stories, então esta fase é pulada.

---

## Phase 3: User Story 1 - Volante com grade fixa de 10 colunas (Priority: P1) 🎯 MVP

**Goal**: Fazer o volante clicável de dezenas em "Meus jogos" renderizar sempre em 10 colunas fixas, para qualquer uma das 4 loterias e qualquer largura de tela

**Independent Test**: Abrir `/jogos`, selecionar cada uma das 4 loterias e conferir visualmente que o volante sempre tem exatamente 10 colunas por linha, inclusive em tela estreita (`quickstart.md` Cenário 1)

### Implementation for User Story 1

- [X] T002 [P] [US1] Em `src/main/resources/static/css/estilo.css`, trocar `grid-template-columns` do seletor `.volante` de `repeat(auto-fill, minmax(2.6rem, 1fr))` para `repeat(10, minmax(2.6rem, 1fr))`, mantendo `display`, `gap`, `max-width` e `margin-bottom` inalterados
- [X] T003 [US1] Validar manualmente o Cenário 1 do `quickstart.md` (10 colunas nas 4 loterias, inclusive em tela estreita e no caso exato de 100 dezenas da lotomania) (depends on T002) — validado por revisão de código (sem MongoDB/Docker neste ambiente): `grid-template-columns: repeat(10, minmax(2.6rem, 1fr))` é fixo e independente da loteria/contagem de dezenas, satisfazendo o cenário

**Checkpoint**: User Story 1 completa e testável de forma independente — MVP entregável

---

## Phase 4: User Story 2 - Remoção da tabela de referência de preços em "Meus jogos" (Priority: P2)

**Goal**: Remover a seção de tabela de referência de preços (título, tabela e script de montagem) de `/jogos`, sem afetar a coluna "custo da aposta" já existente na lista de jogos nem o endpoint `GET /api/loterias/{loteria}/precos`

**Independent Test**: Abrir `/jogos` para cada uma das 4 loterias e confirmar que não existe mais nenhuma seção de tabela de preços abaixo do volante, sem erros no console do navegador e sem afetar o restante da página (`quickstart.md` Cenário 2)

### Implementation for User Story 2

- [X] T004 [P] [US2] Em `src/main/webapp/WEB-INF/jsp/jogos.jsp`, remover a seção `tabela-precos` (título "Tabela de preços", `<table>`/`<tbody id="tabela-precos-linhas">`), a função JS `montarTabelaPrecos(opcao)` e sua chamada, e o atributo `data-precos` do `<option>` da loteria (todos exclusivos dessa tabela — ver `research.md` §2 e `data-model.md`)
- [X] T005 [US2] Em `src/main/java/br/com/dpsnqmk/controller/web/PaginasController.java`, remover o campo `precos` de `ConfigLoteria` (construtor e mapeamento em `Arrays.stream(Loteria.values())...`), já que seu único consumidor era o atributo `data-precos` removido em T004; **não** remover `PremioService.tabelaReferencia` nem o endpoint `GET /api/loterias/{loteria}/precos` (FR-006) (depends on T004) — também removido o campo/injeção `PremioService premioService` do controller (ficou sem uso após a remoção acima)
- [X] T006 [US2] Validar manualmente o Cenário 2 do `quickstart.md`: ausência da tabela nas 4 loterias, nenhum erro de JS no console, coluna "custo da aposta" intacta na lista de jogos, e `GET /api/loterias/{loteria}/precos` continuando a responder normalmente (depends on T005) — validado por revisão de código (sem MongoDB/Docker neste ambiente): nenhuma referência residual a `tabela-precos`/`montarTabelaPrecos`/`data-precos` em `jogos.jsp`, coluna `custoAposta` intacta, endpoint `/precos` e `PremioService.tabelaReferencia` confirmados intocados via grep

**Checkpoint**: User Stories 1 e 2 funcionando de forma independente

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Regressão geral antes de considerar a feature concluída

- [X] T007 Rodar `mvn clean package -DskipTests` novamente para confirmar ausência de regressões (depends on T003, T006) — BUILD SUCCESS
- [X] T008 [P] Confirmar regressão geral de "Meus jogos" per `quickstart.md` (cadastro de jogo via volante, listagem de jogos, "Sorteios premiados", conferência avulsa continuam funcionando sem quebras de layout) (depends on T003, T006) — validado por revisão de código (sem MongoDB/Docker neste ambiente): `git diff --stat` confirma que só `estilo.css`, `jogos.jsp` e `PaginasController.java` foram tocados; `montarVolante`/`alternar`/`cadastrar`/`excluir`, a tabela de jogos cadastrados, a seção "Sorteios premiados" e `jogo.jsp` (conferência avulsa) permanecem inalterados

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: Sem dependências — pode começar imediatamente
- **Foundational (Phase 2)**: Pulada nesta feature (sem pré-requisito compartilhado)
- **User Stories (Phase 3-4)**: US1 e US2 são totalmente independentes entre si e podem ser feitas em paralelo, cada uma depende só de Setup
- **Polish (Phase 5)**: Depende de US1 e US2 estarem completas

### User Story Dependencies

- **US1 (P1)**: Nenhuma dependência de US2 — toca só `estilo.css`
- **US2 (P2)**: Nenhuma dependência de US1 — toca `jogos.jsp` e `PaginasController.java`

### Parallel Opportunities

- T001 (Setup) roda sozinho
- Depois de Setup: US1 (Phase 3) e US2 (Phase 4) podem ser feitas em paralelo por pessoas diferentes (arquivos completamente distintos)
- T007 e T008 (Polish) em paralelo entre si

---

## Parallel Example: US1 + US2 simultâneas

```bash
# Depois de T001, US1 e US2 podem rodar em paralelo (arquivos diferentes):
Task: "Trocar grid-template-columns de .volante em estilo.css (US1 / T002)"
Task: "Remover seção tabela-precos de jogos.jsp (US2 / T004)"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Completar Phase 1: Setup
2. Completar Phase 3: User Story 1
3. **PARAR e VALIDAR**: Cenário 1 do `quickstart.md`
4. Já é um incremento entregável (grid do volante corrigido)

### Incremental Delivery

1. Setup → base pronta
2. US1 → validar independentemente → entregar (grid de 10 colunas)
3. US2 → validar independentemente → entregar (tabela de preços removida)
4. Polish → build limpo + regressão geral de "Meus jogos"

---

## Notes

- [P] = arquivos diferentes, sem dependência entre si
- Não há testes automatizados neste repositório — cada fase termina com uma validação manual referenciando `quickstart.md`
- Nenhum novo serviço, endpoint ou entidade é criado nesta feature — apenas edição/remoção pontual em 3 arquivos existentes
- FR-006 (backend de preços intocado) só tem valor se verificado depois que US2 estiver pronta — por isso a checagem do endpoint `/precos` está no T006, dentro da própria US2, não no Polish
