---

description: "Task list for feature: Destaque persistente e paginação de concursos em cards de teimosinha"
---

# Tasks: Destaque persistente e paginação de concursos em cards de teimosinha

**Input**: Design documents from `/specs/006-navegacao-acertos-concurso/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/, quickstart.md

**Tests**: Not requested — este repositório não tem framework de testes automatizados configurado (`CLAUDE.md`); validação é manual via `quickstart.md`, referenciada como tasks abaixo.

**Organization**: Tasks agrupadas por user story (US1-US3, prioridades de `spec.md`). As mudanças de DTO/service (`concursoComparado`, `isTeimosinha()`, remoção do gate de `pendentes`) são pré-requisito compartilhado das três stories — por isso vão na Fase 2 (Foundational), seguindo o mesmo padrão já usado em `specs/005-destaque-acertos-pendentes/tasks.md`.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Pode rodar em paralelo (arquivos diferentes, sem dependência entre si)
- **[Story]**: A qual user story a task pertence (US1, US2, US3)
- Paths são exatos, relativos à raiz do repo (Spring Boot monolito único — ver `plan.md`)

---

## Phase 1: Setup

**Purpose**: Estabelecer uma baseline funcionando antes de tocar em qualquer arquivo (sem testes automatizados neste repo, então um build limpo é o único gate automático)

- [X] T001 Confirmar que a baseline builda: `mvn clean package -DskipTests` (sem mudanças de código ainda — só checkpoint pré-mudança) — BUILD SUCCESS

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Mudanças de DTO/service compartilhadas pelas três user stories — remover o gate `pendentes == 0` (US1), expor `concursoComparado` (US2/US3) e `jogo.teimosinha` (US2/US3) — ver `data-model.md` e `research.md` §1-3

**⚠️ CRITICAL**: Nenhuma story pode ser implementada/validada até esta fase terminar

- [X] T002 [P] Em `src/main/java/br/com/dpsnqmk/dto/JogoMongoDTO.java`, adicionar o getter derivado `public boolean isTeimosinha() { return quantidadeConcursos > 1; }` logo após `getConcursoFinal()` — ver `research.md` §3 e `data-model.md`
- [X] T003 [P] Em `src/main/java/br/com/dpsnqmk/dto/JogoComResumo.java`, adicionar o campo `Integer concursoComparado` (após `dezenasAcertadasUltimoConcurso`, com javadoc curto explicando que é `null` quando nenhum concurso do intervalo foi apurado ainda) — ver `data-model.md`
- [X] T004 Em `src/main/java/br/com/dpsnqmk/service/JogoService.java`:
  - Renomear o método privado `dezenasAcertadasUltimoConcurso(List<ConferenciaConcurso> concursos, ResumoJogo resumo)` para `ultimoConcursoApurado(List<ConferenciaConcurso> concursos)` (remove o parâmetro `resumo` e o gate `if (resumo.getPendentes() == 0) return List.of();`); o método passa a retornar `ConferenciaConcurso` (o primeiro item não-`PENDENTE` percorrendo `concursos` de trás para frente, ou `null` se nenhum for encontrado)
  - Em `listarComResumo()`, chamar esse método uma vez por jogo (`ConferenciaConcurso ultimo = ultimoConcursoApurado(concursos);`) e derivar `List<Integer> dezenasAcertadas = ultimo == null ? List.of() : ultimo.getDezenasAcertadas();` e `Integer concursoComparado = ultimo == null ? null : ultimo.getConcurso();`, passando os dois para o `new JogoComResumo(jogo, resumo, custoTotal, ganhoTotal(concursos), dezenasAcertadas, concursoComparado)`
  - Ver `research.md` §1-2 e `data-model.md` (depends on T003) — `mvn compile` BUILD SUCCESS

**Checkpoint**: `GET /api/jogos` já responde com `jogo.teimosinha` e `concursoComparado` (ver `contracts/api-jogos.md`); o destaque nas dezenas grandes de `jogos.jsp` já deixa de ser escondido em teimosinhas expiradas — a marcação `${item.dezenasAcertadasUltimoConcurso.contains(numero) ? 'acertada' : ''}" já existente em `jogos.jsp` passa a refletir isso automaticamente, sem precisar de nenhuma mudança na JSP (US1 já fica pronta aqui, ver Fase 3)

---

## Phase 3: User Story 1 - Ver o destaque de acertos mesmo em jogos já expirados (Priority: P1) 🎯 MVP

**Goal**: O destaque de acertos nas dezenas grandes do card passa a aparecer também em teimosinhas totalmente concluídas (sem concursos pendentes), sempre referente ao concurso mais recente já apurado do intervalo

**Independent Test**: Com uma teimosinha totalmente concluída (todos os concursos do intervalo já apurados) que teve pelo menos um acerto no concurso mais recente do intervalo, acessar `/jogos` e confirmar que as dezenas coincidentes aparecem destacadas (`quickstart.md` Cenário 1)

### Implementation for User Story 1

- Nenhuma implementação adicional: a remoção do gate já foi feita em **T004** (Foundational) e `jogos.jsp` já lê `item.dezenasAcertadasUltimoConcurso` sem nenhuma condição de `pendentes` — esta fase só valida o resultado.

- [X] T005 [US1] Validar manualmente o Cenário 1 do `quickstart.md` (destaque aparece em teimosinha expirada, comparando com `GET /api/jogos`) (depends on T004) — validado por revisão de código (sem MongoDB/Docker neste ambiente): `ultimoConcursoApurado(concursos)` não recebe mais `resumo`/não checa `pendentes`, percorre a lista de trás para frente e retorna o primeiro concurso não-`PENDENTE` independentemente de `resumo.pendentes`; `jogos.jsp:23` já lia `item.dezenasAcertadasUltimoConcurso.contains(numero)` sem nenhuma condição de `pendentes`, então o destaque passa a aparecer também em teimosinhas totalmente apuradas sem exigir mudança na JSP

**Checkpoint**: User Story 1 completa e testável de forma independente — MVP entregável

---

## Phase 4: User Story 2 - Saber com qual concurso o destaque está comparando (Priority: P2)

**Goal**: Cada card exibe um rótulo indicando o número do concurso ao qual o destaque atual se refere (ou um texto de espera, para teimosinha sem nenhum concurso apurado ainda)

**Independent Test**: Em qualquer card que já exiba o destaque de acertos (US1), confirmar que um rótulo próximo às dezenas informa o número do concurso comparado; confirmar que jogos não-teimosinha sem apurado não mostram rótulo nenhum (`quickstart.md` Cenário 2)

### Implementation for User Story 2

- [X] T006 [US2] Em `src/main/webapp/WEB-INF/jsp/jogos.jsp`, logo após o bloco `dezenas-grandes`, adicionar o rótulo de comparação com a lógica de `data-model.md` (tabela "rótulo e controles de paginação — estado inicial"):
  - `item.concursoComparado != null` → exibir "comparação com o concurso ${item.concursoComparado}"
  - `item.jogo.teimosinha && item.concursoComparado == null` → exibir o texto de espera "aguardando apuração" no lugar do número
  - `!item.jogo.teimosinha && item.concursoComparado == null` → não exibir nada (nem o bloco do rótulo)
  - Estrutura sugerida em JSTL: `<c:if test="${item.jogo.teimosinha or item.concursoComparado != null}"> ... </c:if>` envolvendo um `<c:choose>` para o texto do rótulo — os botões de voltar/avançar (dentro do mesmo bloco, só quando `item.jogo.teimosinha`) ficam para a Fase 5 (US3)
  - (depends on T002, T003, T004) — implementado em `jogos.jsp` com `<c:if test="${item.jogo.teimosinha or item.concursoComparado != null}">` envolvendo um `<c:choose>`; ids `dezenas-${item.jogo.id}`/`paginacao-${item.jogo.id}`/`rotulo-${item.jogo.id}` adicionados para os handlers de T009/T010
- [X] T007 [P] [US2] Em `src/main/resources/static/css/estilo.css`, adicionar uma regra de layout `.paginacao-concurso` (flex row, `justify-content: space-between`/`align-items: center`, espaçamento consistente com as demais linhas do card) para a linha rótulo+controles — sem cor nova, reaproveita `.botao`/`.botao:disabled` já existentes para os controles que a Fase 5 vai adicionar
- [X] T008 [US2] Validar manualmente o Cenário 2 do `quickstart.md` (rótulo aparece com o número do concurso em teimosinha e em não-teimosinha já apurados; ausente em não-teimosinha sem apurado) e a parte do Cenário 5 referente ao texto "aguardando apuração" (sem verificar ainda os botões, que são da Fase 5) (depends on T006, T007) — validado por revisão de código (sem MongoDB/Docker neste ambiente): as 4 combinações teimosinha×concursoComparado do bloco `<c:if>`/`<c:choose>` em `jogos.jsp` foram conferidas contra a tabela de `data-model.md` — teimosinha+apurado→rótulo com número; não-teimosinha+apurado→rótulo com número, sem bloco de controles (T009 gate por `item.jogo.teimosinha`); não-teimosinha+sem apurado→bloco inteiro ausente (FR-002); teimosinha+sem apurado→"aguardando apuração" (FR-012)

**Checkpoint**: User Stories 1 e 2 funcionando de forma independente — todo card com concurso apurado mostra o rótulo correto

---

## Phase 5: User Story 3 - Paginar entre os concursos apurados de uma teimosinha (Priority: P3)

**Goal**: Para jogos classificados como teimosinha (intervalo com mais de um concurso), o card ganha controles de voltar/avançar que percorrem os concursos já apurados do intervalo, recalculando destaque e rótulo a cada passo

**Independent Test**: Em um card de teimosinha com o rótulo visível (US2), clicar em voltar e confirmar que destaque e rótulo passam a refletir o concurso anterior apurado; clicar em avançar e confirmar que volta ao concurso seguinte; confirmar que os limites do intervalo apurado desabilitam o controle correspondente (`quickstart.md` Cenários 3 e 4)

### Implementation for User Story 3

- [X] T009 [US3] Em `src/main/webapp/WEB-INF/jsp/jogos.jsp`, dentro do bloco de rótulo criado em T006, adicionar os controles `<button>` de voltar e avançar — renderizados apenas quando `item.jogo.teimosinha` — com o estado `disabled` inicial de `data-model.md` §SSR:
  - avançar: sempre `disabled` no carregamento (o card já nasce no concurso mais recente apurado)
  - voltar: `disabled` quando `item.resumo.conferidos <= 1` (nenhum concurso apurado anterior para onde ir) ou quando `item.concursoComparado == null` (FR-012)
  - Reaproveitar a classe `.botao` (e o `:disabled` já existente em `estilo.css`) para os dois botões; incluir `data-jogo-id="${item.jogo.id}"` para os handlers de T010 identificarem o card
  - (depends on T006, T007) — implementado com `id="voltar-${item.jogo.id}"`/`id="avancar-${item.jogo.id}"` (mais simples que `data-jogo-id` para os handlers de T010 via `getElementById`); condição de `voltar` simplificada para só `item.resumo.conferidos <= 1` — é logicamente equivalente à condição composta original, já que `concursoComparado == null` só ocorre quando `conferidos == 0`, subconjunto de `conferidos <= 1` (achado `I1` da análise `/speckit-analyze`)
- [X] T010 [US3] Em `src/main/webapp/WEB-INF/jsp/jogos.jsp`, no bloco `<script>`, adicionar a lógica de paginação client-side descrita em `data-model.md` §"Estado client-side (JS)":
  - Um objeto/`Map` em memória, por `jogoId`, guardando `{ apurados, indice }`
  - No primeiro clique em voltar/avançar de um card, se ainda não houver estado para aquele `jogoId`, buscar `/api/jogos/{id}/conferencia` (mesmo endpoint de `alternarPremiados`), filtrar `situacao !== 'PENDENTE'`, ordenar por `concurso` ascendente, e inicializar `indice = apurados.length - 1`
  - Voltar: `indice = Math.max(0, indice - 1)`; avançar: `indice = Math.min(apurados.length - 1, indice + 1)`
  - A cada mudança de `indice`, re-renderizar: a classe `acertada` das dezenas grandes daquele card (a partir de `apurados[indice].dezenasAcertadas`), o texto do rótulo (`apurados[indice].concurso`) e o `disabled` dos dois botões (voltar quando `indice === 0`; avançar quando `indice === apurados.length - 1`)
  - Isolar o estado por `jogoId` (um `Map`/objeto por card) para garantir que navegar em um card não afete os demais (FR-011)
  - (depends on T009) — `mvn package -DskipTests` BUILD SUCCESS; tags JSTL balanceados (`grep`/`uniq -c` confirma pares `c:if`/`c:choose`/`c:when`/`c:otherwise`/`c:forEach`)
- [X] T011 [US3] Validar manualmente os Cenários 3 e 4 do `quickstart.md` (paginação voltar/avançar dentro do intervalo apurado de uma teimosinha ativa; limites do intervalo desabilitam o controle correto; navegação não sobrevive a reload; um card não afeta outro) e a parte do Cenário 5 referente aos controles desabilitados em teimosinha sem nenhum apurado (depends on T010) — validado por revisão de código/trace manual (sem MongoDB/Docker neste ambiente): estado inicial (avançar sempre `disabled`, voltar `disabled` sse `conferidos<=1`) confirmado no markup de T009; cada clique em voltar/avançar move `indice` dentro de `[0, apurados.length-1]` e nunca inclui itens `PENDENTE` (filtrados antes de entrar no array), cobrindo FR-008/FR-009; `estadoPaginacao` é uma variável JS em memória (sem `localStorage`/cookie), então um reload perde o estado e a SSR volta a mostrar o concurso mais recente (FR-010); todas as atualizações de DOM são escopadas por `id="...-${item.jogo.id}"`, então cards não se afetam entre si (FR-011); não-teimosinha (jogo C) renderiza só o rótulo pois o `<c:if test="${item.jogo.teimosinha}">` interno das T009 nunca é avaliado como verdadeiro (FR-006); teimosinha sem nenhum apurado (jogo D) cai no `<c:otherwise>` ("aguardando apuração") com ambos os botões desabilitados (FR-012)

**Checkpoint**: Todas as user stories funcionando de forma independente — feature completa

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Regressão geral antes de considerar a feature concluída

- [X] T012 [P] Rodar `mvn clean package -DskipTests` novamente para confirmar ausência de regressões (depends on T005, T008, T011) — BUILD SUCCESS
- [X] T013 [P] Confirmar a seção "Regressão geral" do `quickstart.md` (grade de cards, badge "premiado" clicável, painel de sorteios premiados, custo/ganho total continuam funcionando sem conflito com o fetch de paginação; `GET /api/jogos` responde com os campos novos, ver `contracts/api-jogos.md`; nenhuma cor nova em `estilo.css`) (depends on T005, T008, T011) — validado por revisão: `git diff --stat` confirma que a mudança de código fica restrita exatamente aos 5 arquivos previstos em `plan.md` (`JogoMongoDTO.java`, `JogoComResumo.java`, `JogoService.java`, `jogos.jsp`, `estilo.css`); `PaginasController`, `JogoRestController`, `ConcursoRepository` e `jogo.jsp` permanecem intocados — nenhum endpoint novo; `git diff estilo.css` mostra só regras novas (`.paginacao-concurso`), nenhuma linha de cor existente alterada; `JogoComResumo`/`JogoMongoDTO` são serializados via getters Lombok/derivados, então `concursoComparado` e `teimosinha` aparecem automaticamente em `GET /api/jogos` sem exigir mudança nos controllers

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: Sem dependências — pode começar imediatamente
- **Foundational (Phase 2)**: Depende de Setup — bloqueia as três user stories (T002-T004 são a base de dados que US1/US2/US3 consomem)
- **User Stories (Phase 3-5)**: Todas dependem de Foundational; US2 depende também da lógica de rótulo estar pronta antes de US3 adicionar os botões no mesmo bloco (T006 antes de T009); US1 é independente de US2/US3
- **Polish (Phase 6)**: Depende de US1, US2 e US3 completas

### User Story Dependencies

- **US1 (P1)**: Depende só de Foundational (T004) — nenhuma implementação própria, só validação
- **US2 (P2)**: Depende de Foundational (T002-T004) — independente de US1; introduz o bloco de rótulo que US3 estende
- **US3 (P3)**: Depende de Foundational e do bloco de rótulo de US2 (T006/T007) — os botões voltar/avançar (T009) são adicionados dentro desse mesmo bloco

### Parallel Opportunities

- T002 e T003 (Foundational, DTOs em arquivos diferentes) em paralelo; T004 depende de T003 (assinatura do construtor)
- T007 (CSS) pode rodar em paralelo com T006 (JSP) — arquivos diferentes
- T012 e T013 (Polish) em paralelo entre si

---

## Parallel Example: Foundational

```bash
# T002 e T003 podem rodar juntos (arquivos diferentes):
Task: "Adicionar isTeimosinha() em JogoMongoDTO.java (T002)"
Task: "Adicionar campo concursoComparado em JogoComResumo.java (T003)"
# T004 só começa depois de T003 (usa o novo campo no construtor de JogoComResumo)
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Completar Phase 1: Setup
2. Completar Phase 2: Foundational (T002-T004 — remove o gate de pendentes e já expõe os campos novos)
3. Completar Phase 3: User Story 1 (só validação — o comportamento já vem de brinde da Foundational)
4. **PARAR e VALIDAR**: Cenário 1 do `quickstart.md`
5. Já é um incremento entregável (destaque persistente em teimosinhas expiradas)

### Incremental Delivery

1. Setup + Foundational → gate removido, campos novos disponíveis em `GET /api/jogos`
2. US1 → validar independentemente → entregar (destaque persistente)
3. US2 → rótulo de comparação → validar → entregar
4. US3 → paginação voltar/avançar → validar → entregar
5. Polish → build limpo + regressão geral

---

## Notes

- [P] = arquivos diferentes, sem dependência entre si
- Não há testes automatizados neste repositório — cada fase termina com uma validação manual referenciando `quickstart.md`
- Nenhum endpoint novo é criado; a paginação (US3) reaproveita `GET /api/jogos/{id}/conferencia`, já existente desde a feature 004 (ver `research.md` §4 e `contracts/api-jogos.md`)
- US1 não tem tasks de implementação própria porque a remoção do gate `pendentes == 0` (T004, Foundational) já é suficiente — `jogos.jsp` já lia `dezenasAcertadasUltimoConcurso` sem condição adicional
