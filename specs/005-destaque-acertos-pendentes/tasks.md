---

description: "Task list for feature: Destaque de acertos do concurso mais recente nos cards de teimosinha"
---

# Tasks: Destaque de acertos do concurso mais recente nos cards de teimosinha

**Input**: Design documents from `/specs/005-destaque-acertos-pendentes/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/, quickstart.md

**Tests**: Not requested — este repositório não tem framework de testes automatizados configurado (`CLAUDE.md`); validação é manual via `quickstart.md`, referenciada como tasks abaixo.

**Organization**: Tasks agrupadas por user story (US1-US2, prioridades de `spec.md`). A recoloração de `.dezena.acertada` (`estilo.css`) é compartilhada pelas duas stories — US1 (FR-003) precisa dela para o destaque no card ter a cor certa, e US2 (FR-007) é exatamente essa recoloração — por isso vai na Fase 2 (Foundational) em vez de dentro de uma única story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Pode rodar em paralelo (arquivos diferentes, sem dependência entre si)
- **[Story]**: A qual user story a task pertence (US1, US2)
- Paths são exatos, relativos à raiz do repo (Spring Boot monolito único — ver `plan.md`)

---

## Phase 1: Setup

**Purpose**: Estabelecer uma baseline funcionando antes de tocar em qualquer arquivo (sem testes automatizados neste repo, então um build limpo é o único gate automático)

- [X] T001 Confirmar que a baseline builda: `mvn clean package -DskipTests` (sem mudanças de código ainda — só checkpoint pré-mudança) — BUILD SUCCESS

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Recolorir `.dezena.acertada` para o par de cores de `.badge.premiado` — pré-requisito de **ambas** as user stories (US1 precisa dessa cor para o destaque no card; US2 é literalmente essa mudança de cor, ver `research.md` §3)

**⚠️ CRITICAL**: Nenhuma story pode ser validada visualmente até esta task terminar

- [X] T002 Em `src/main/resources/static/css/estilo.css`, alterar a regra `.dezena.acertada` de `background: #166534; color: #fff;` para `background: #dcfce7; color: #166534;` (mesmo par de cores de `.badge.premiado`) — ver `research.md` §3 e `data-model.md`

**Checkpoint**: Cor compartilhada pronta — US1 e US2 podem ser implementadas/validadas

---

## Phase 3: User Story 1 - Ver no card os acertos do concurso mais recente, enquanto a teimosinha ainda está ativa (Priority: P1) 🎯 MVP

**Goal**: Nas "dezenas-grandes" do card de uma teimosinha, destacar (classe `acertada`, recolorida em T002) os números que coincidiram com o concurso mais recente já apurado do intervalo, só enquanto a teimosinha ainda tiver concursos pendentes

**Independent Test**: Com uma teimosinha ativa (concursos pendentes) e com o concurso mais recente do intervalo já apurado, acessar `/jogos` e confirmar que as dezenas do card que bateram nesse concurso aparecem destacadas com a cor do badge "premiado", independentemente da quantidade de acertos; confirmar ausência de destaque em teimosinha expirada ou sem nenhum concurso apurado ainda (`quickstart.md` Cenários 1-4)

### Implementation for User Story 1

- [X] T003 [P] [US1] Em `src/main/java/br/com/dpsnqmk/dto/JogoComResumo.java`, adicionar o campo `List<Integer> dezenasAcertadasUltimoConcurso` — ver `data-model.md`
- [X] T004 [US1] Em `src/main/java/br/com/dpsnqmk/service/JogoService.java`, adicionar um método privado (ex. `dezenasAcertadasUltimoConcurso(List<ConferenciaConcurso> concursos, ResumoJogo resumo)`) que retorna `List.of()` se `resumo.getPendentes() == 0`, senão percorre `concursos` de trás para frente e retorna `dezenasAcertadas` do primeiro item com `situacao != ConferenciaConcurso.PENDENTE` (ou `List.of()` se nenhum for encontrado); chamar esse método em `listarComResumo()` e passar o resultado no novo `JogoComResumo(...)` — ver `research.md` §1-2 (depends on T003)
- [X] T005 [US1] Em `src/main/webapp/WEB-INF/jsp/jogos.jsp`, no bloco `dezenas-grandes`, trocar `<span class="dezena grande">${numero}</span>` por `<span class="dezena grande ${item.dezenasAcertadasUltimoConcurso.contains(numero) ? 'acertada' : ''}">${numero}</span>` (mesmo padrão condicional já usado em `jogo.jsp`) (depends on T004, T002) — BUILD SUCCESS
- [X] T006 [US1] Validar manualmente os Cenários 1 a 4 do `quickstart.md` (destaque presente em teimosinha ativa com concurso apurado, independente da faixa mínima de premiação; ausente em teimosinha expirada; ausente sem concurso apurado ainda; destaque acompanha a atualização da base) (depends on T005) — validado por revisão de código (sem MongoDB/Docker neste ambiente): `dezenasAcertadasUltimoConcurso` retorna `List.of()` imediatamente quando `pendentes == 0` (Cenário 2); varredura reversa retorna `dezenasAcertadas` do primeiro item não-`PENDENTE` (populado independentemente do resultado ser `PREMIADO`/`NAO_PREMIADO`, cobrindo Cenário 1 mesmo com poucos acertos); cai no `List.of()` final quando todos os concursos do intervalo são `PENDENTE` (Cenário 3); `listarComResumo()` recalcula do zero a cada chamada, então uma nova importação já reflete o concurso mais recente na próxima renderização (Cenário 4)

**Checkpoint**: User Story 1 completa e testável de forma independente — MVP entregável

---

## Phase 4: User Story 2 - Suavizar a cor de acerto na tabela de sorteios premiados (Priority: P3)

**Goal**: A coluna "Dezenas jogadas/sorteadas (acertos destacados)" (painel de sorteios premiados de `/jogos` e tabela "Concurso a concurso" de `/jogos/{id}`) passa a usar um verde mais suave

**Independent Test**: Abrir o painel de sorteios premiados de uma teimosinha premiada e confirmar que as dezenas acertadas aparecem com o verde suave (não mais o escuro anterior), mantendo o número legível (`quickstart.md` Cenário 5)

### Implementation for User Story 2

- Nenhuma implementação adicional: a mudança de cor já foi feita em **T002** (Foundational), compartilhada com US1 — ver `research.md` §3. Esta fase só valida o resultado.

- [X] T007 [US2] Validar manualmente o Cenário 5 do `quickstart.md` (cor suave na coluna "acertos destacados" tanto no painel de `/jogos` quanto na tabela "Concurso a concurso" de `/jogos/{id}`) (depends on T002) — validado por revisão de código (sem MongoDB/Docker neste ambiente): confirmado por `grep` que `jogo.jsp` (tabela "Concurso a concurso"), o painel de sorteios premiados de `jogos.jsp` (legenda e linhas montadas em JS) e o novo destaque nas dezenas grandes (T005) usam todos a mesma classe `.dezena.acertada`, já recolorida em T002

**Checkpoint**: User Stories 1 e 2 funcionando de forma independente — feature completa

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Regressão geral antes de considerar a feature concluída

- [X] T008 [P] Rodar `mvn clean package -DskipTests` novamente para confirmar ausência de regressões (depends on T006, T007) — BUILD SUCCESS
- [X] T009 [P] Confirmar a seção "Regressão geral" do `quickstart.md` (grade de cards, badge clicável, painel único, custo/ganho total da feature 004 continuam funcionando; `GET /api/jogos` responde com o campo novo, ver `contracts/api-jogos.md`) (depends on T006, T007) — validado por revisão de código (sem MongoDB/Docker neste ambiente): `git diff --stat` confirma que a mudança fica restrita exatamente aos 4 arquivos previstos em `plan.md` (`JogoComResumo.java`, `JogoService.java`, `estilo.css`, `jogos.jsp`, com só 2 linhas tocadas em `jogos.jsp`); `jogo.jsp`, `JogoRestController`, `PaginasController` e a lógica de grade/badge/painel da feature 004 permanecem intocados; `JogoComResumo` é serializado via getters Lombok, então o novo campo aparece automaticamente em `GET /api/jogos` sem exigir mudança no controller

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: Sem dependências — pode começar imediatamente
- **Foundational (Phase 2)**: Depende de Setup — bloqueia a validação visual de US1 e US2 (T002 é a mudança de cor que ambas as stories precisam)
- **User Stories (Phase 3-4)**: US1 depende de Foundational (T002) e é independente de US2; US2 depende só de Foundational (T002) — não tem tasks de implementação próprias, só validação
- **Polish (Phase 5)**: Depende de US1 e US2 completas

### User Story Dependencies

- **US1 (P1)**: Depende de Foundational (T002, cor) — nenhuma dependência de US2
- **US2 (P3)**: Depende só de Foundational (T002) — sua "implementação" inteira é a task compartilhada; independente de US1 (não usa `dezenasAcertadasUltimoConcurso` nem toca `jogos.jsp`/`JogoService`)

### Parallel Opportunities

- T003 (DTO) pode começar assim que T002 (Foundational) terminar, em paralelo com qualquer preparação de T004 (mas T004 depende do campo existir)
- T008 e T009 (Polish) em paralelo entre si

---

## Parallel Example: Início de US1

```bash
# T002 (Foundational) precisa terminar primeiro — cor usada tanto no destaque do card (US1) quanto na suavização (US2)
# T003 (DTO) pode começar assim que T002 terminar:
Task: "Adicionar campo dezenasAcertadasUltimoConcurso em JogoComResumo.java (US1 / T003)"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Completar Phase 1: Setup
2. Completar Phase 2: Foundational (T002 — cor compartilhada)
3. Completar Phase 3: User Story 1
4. **PARAR e VALIDAR**: Cenários 1-4 do `quickstart.md`
5. Já é um incremento entregável (destaque nos cards funcionando; a suavização da coluna de acertos já vem de brinde por causa de T002, mesmo antes de "US2" ser formalmente validada)

### Incremental Delivery

1. Setup + Foundational → cor pronta
2. US1 → validar independentemente → entregar (destaque nos cards)
3. US2 → validar (sem código novo) → entregar (cor suave confirmada)
4. Polish → build limpo + regressão geral

---

## Notes

- [P] = arquivos diferentes, sem dependência entre si
- Não há testes automatizados neste repositório — cada fase termina com uma validação manual referenciando `quickstart.md`
- Nenhum endpoint novo é criado; `GET /api/jogos` ganha um campo na resposta (ver `contracts/api-jogos.md`)
- US2 não tem tasks de implementação porque FR-007 e a parte de FR-003 relativa à cor são satisfeitas pela mesma mudança de CSS (T002) — ver `research.md` §3
