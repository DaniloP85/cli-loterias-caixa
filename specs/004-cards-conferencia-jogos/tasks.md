---

description: "Task list for feature: Layout em cards na página de conferência de jogos"
---

# Tasks: Layout em cards na página de conferência de jogos

**Input**: Design documents from `/specs/004-cards-conferencia-jogos/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/, quickstart.md

**Tests**: Not requested — este repositório não tem framework de testes automatizados configurado (`CLAUDE.md`); validação é manual via `quickstart.md`, referenciada como tasks abaixo.

**Organization**: Tasks agrupadas por user story (US1-US4, prioridades de `spec.md`); a ordem de execução recomendada é P1 → P2 → P2 → P3, já que US2/US3/US4 dependem da grade de cards (US1) existir antes de terem onde renderizar seus dados.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Pode rodar em paralelo (arquivos diferentes, sem dependência entre si)
- **[Story]**: A qual user story a task pertence (US1, US2, US3, US4)
- Paths são exatos, relativos à raiz do repo (Spring Boot monolito único — ver `plan.md`)

---

## Phase 1: Setup

**Purpose**: Estabelecer uma baseline funcionando antes de tocar em qualquer arquivo (sem testes automatizados neste repo, então um build limpo é o único gate automático)

- [X] T001 Confirmar que a baseline builda: `mvn clean package -DskipTests` (sem mudanças de código ainda — só checkpoint pré-mudança) — BUILD SUCCESS

---

## Phase 2: Foundational

**Purpose**: N/A — não há infraestrutura nova compartilhada (sem endpoint novo, sem entidade nova, ver `data-model.md`/`plan.md`). A dependência real desta feature é entre user stories, não uma fase de infraestrutura separada: US2, US3 e US4 dependem da grade de cards existir (US1) para terem onde renderizar custo total, ganho total e o badge clicável — ver "User Story Dependencies" abaixo. Esta fase é pulada, seguindo o mesmo padrão já usado em `specs/002-ajustes-ui-jogos/tasks.md` e `specs/003-cadastro-conferencia-jogos/tasks.md`.

---

## Phase 3: User Story 1 - Lista de jogos em cards, não mais em tabela (Priority: P1) 🎯 MVP

**Goal**: Substituir a `<table>` de jogos cadastrados em `jogos.jsp` por uma grade de cards (máx. 4 por linha), com título "loteria de X até Y", dezenas em destaque, descrição e botão de exclusão "×" no cabeçalho

**Independent Test**: Acessar `/jogos` com jogos cadastrados e confirmar que a lista aparece como cards (não mais tabela), com no máximo 4 por linha, título combinando loteria+concursos, dezenas em destaque, descrição e exclusão funcionando (`quickstart.md` Cenário 1)

### Implementation for User Story 1

- [X] T002 [P] [US1] Em `src/main/resources/static/css/estilo.css`, adicionar a classe `.grade-jogos` (`display: grid; grid-template-columns: repeat(auto-fill, minmax(240px, 1fr)); gap: 1rem;`) e a classe `.botao-fechar` (botão circular "×" no canto superior direito do card, mesmo padrão visual de `.botao.perigo`) — ver `research.md` §1 e §3
- [X] T003 [US1] Em `src/main/webapp/WEB-INF/jsp/jogos.jsp`, substituir o `<table>` da lista de jogos por `<div class="grade-jogos">` com `<c:forEach var="item" items="${jogos}">` gerando um `<div class="card">` por jogo: cabeçalho com `"${item.jogo.loteria} de ${item.jogo.concursoInicial} até ${item.jogo.concursoFinal}"` + `<button class="botao-fechar" onclick="excluir('${item.jogo.id}')">&times;</button>`; corpo com as dezenas usando `.dezena.grande` (reaproveitada de `research.md` §2), a descrição (`<c:out value="${item.jogo.descricao}"/>`, se não vazia) e os badges de resumo (mantém `item.custoAposta` e a marcação de badges como estão por enquanto — renomeação e novo campo ficam para US2/US3) (depends on T002)
- [X] T004 [US1] Validar manualmente o Cenário 1 do `quickstart.md` (grade de cards com máx. 4 por linha, título, dezenas em destaque, descrição, exclusão via "×") (depends on T003) — validado por revisão de código (sem MongoDB/Docker neste ambiente): grid usa `auto-fill, minmax(240px,1fr)` dentro do `main{max-width:1100px}` existente (4 colunas cabem, 5ª não — ver `research.md` §1), título/dezenas/descrição/exclusão confirmados no markup, `excluir(id)` inalterado

**Checkpoint**: User Story 1 completa e testável de forma independente — MVP entregável (grade de cards funcionando, ainda com o valor de custo antigo)

---

## Phase 4: User Story 2 - Custo total da teimosinha (Priority: P2)

**Goal**: O card passa a exibir o custo total da teimosinha (quantidade de concursos × valor unitário da aposta), não mais o valor de uma única aposta

**Independent Test**: Para uma teimosinha com mais de um concurso, confirmar que o "Custo" exibido no card é a quantidade de concursos multiplicada pelo valor unitário da aposta (`quickstart.md` Cenário 2)

### Implementation for User Story 2

- [X] T005 [P] [US2] Em `src/main/java/br/com/dpsnqmk/dto/JogoComResumo.java`, renomear o campo `custoAposta` para `custoTotal` (mesmo tipo `BigDecimal`) — ver `data-model.md`
- [X] T006 [US2] Em `src/main/java/br/com/dpsnqmk/service/JogoService.java`, em `listarComResumo()`, calcular `custoTotal` como `premioService.custoAposta(loteria, jogo.getNumeros().size()).multiply(BigDecimal.valueOf(jogo.getQuantidadeConcursos()))` (não alterar `PremioService.custoAposta`, que continua sendo o valor unitário usado por `/precos`) — ver `research.md` §4 (depends on T005)
- [X] T007 [US2] Em `src/main/webapp/WEB-INF/jsp/jogos.jsp`, atualizar a referência EL no card de `${item.custoAposta}` para `${item.custoTotal}` (depends on T006, T003)
- [X] T008 [US2] Validar manualmente o Cenário 2 do `quickstart.md` (custo total = concursos × valor unitário, inclusive caso de 1 concurso) (depends on T007) — validado por revisão de código (sem MongoDB/Docker neste ambiente): `custoTotal` calculado como unitário × `quantidadeConcursos` em `listarComResumo()`, `PremioService.custoAposta`/endpoint `/precos` confirmados intocados

**Checkpoint**: User Stories 1 e 2 funcionando de forma independente

---

## Phase 5: User Story 3 - Ganho total da teimosinha (Priority: P2)

**Goal**: O card passa a exibir o ganho total da teimosinha (soma dos valores de prêmio de todos os concursos premiados daquele jogo)

**Independent Test**: Para uma teimosinha com concursos premiados, confirmar que "Ganhos" no card é a soma correta dos valores de prêmio; para uma sem prêmios, confirmar R$ 0,00 (`quickstart.md` Cenário 3)

### Implementation for User Story 3

- [X] T009 [US3] Em `src/main/java/br/com/dpsnqmk/dto/JogoComResumo.java`, adicionar o campo `BigDecimal ganhoTotal` (mesmo arquivo de T005 — sequencial) — ver `data-model.md` (depends on T005)
- [X] T010 [US3] Em `src/main/java/br/com/dpsnqmk/service/JogoService.java`, adicionar um método privado (ex. `ganhoTotal(List<ConferenciaConcurso>)`) que soma `premio.getValor()` dos itens com `situacao == PREMIADO && premio.getStatus() == PremioFaixa.VALOR`, reaproveitando a mesma lista de `conferirConcursos(jogo)` já usada para montar `resumo` em `listarComResumo()`, e passar o resultado no novo `JogoComResumo(...)` — ver `research.md` §5 (depends on T006, T009)
- [X] T011 [US3] Em `src/main/webapp/WEB-INF/jsp/jogos.jsp`, exibir "Ganhos" no corpo do card usando `${item.ganhoTotal}` (mesmo padrão de formatação `R$` do custo) (depends on T010, T003)
- [X] T012 [US3] Validar manualmente o Cenário 3 do `quickstart.md` (ganho total correto com prêmios; R$ 0,00 sem prêmios) (depends on T011) — validado por revisão de código: `ganhoTotal` soma `premio.getValor()` para `situacao == PREMIADO && status == VALOR` na mesma varredura de `resumo`; `BigDecimal.ZERO` inicial cobre o caso sem prêmios

**Checkpoint**: User Stories 1, 2 e 3 funcionando de forma independente

---

## Phase 6: User Story 4 - Sorteios premiados por teimosinha, sob demanda (Priority: P3)

**Goal**: O badge "premiado" de cada card abre/fecha/troca um único painel compartilhado com os sorteios premiados daquela teimosinha específica, substituindo a tabela combinada permanente de hoje

**Independent Test**: Com duas teimosinhas premiadas, clicar no badge de uma mostra só os prêmios dela no painel único; clicar em outro badge troca o conteúdo; clicar de novo no mesmo fecha (`quickstart.md` Cenário 4)

### Implementation for User Story 4

- [X] T013 [P] [US4] Em `src/main/resources/static/css/estilo.css`, adicionar a classe modificadora `.badge.premiado.clicavel` (`cursor: pointer` + destaque no `:hover`), sem alterar a regra base `.badge.premiado` (para não afetar `jogo.jsp`) — ver `research.md` §8
- [X] T014 [US4] Em `src/main/webapp/WEB-INF/jsp/jogos.jsp`, adicionar um container oculto após a `.grade-jogos` (ex. `<div id="painel-premiados" class="card" hidden></div>`) que vai receber o conteúdo montado em JS (depends on T003)
- [X] T015 [US4] Em `jogos.jsp`, no badge "premiado" de cada card, adicionar a classe `clicavel` e `onclick="alternarPremiados('${item.jogo.id}')"` (depends on T013, T014)
- [X] T016 [US4] Em `jogos.jsp`, implementar no `<script>` a função `alternarPremiados(id)` (fecha o painel se `id` já é o jogo aberto; senão faz `fetch('/api/jogos/' + id + '/conferencia')`, filtra `concursos` por `situacao === 'PREMIADO'`, reconstrói o conteúdo do painel — tabela com loteria/concurso/apuração/dezenas com acertos destacados (usando `conferencia.jogo.numeros` + `dezenasAcertadas`)/acertos/prêmio, ou uma mensagem de ausência de prêmios se a lista filtrada estiver vazia — e mostra o painel, guardando o `id` em uma variável `jogoAbertoId`) — ver `research.md` §6 e `contracts/api-jogos.md` (depends on T015)
- [X] T017 [US4] Em `jogos.jsp`, remover os dois blocos `<c:if>` da tabela combinada "Sorteios premiados" (vazio e com dados) e a legenda de cores fixa acima dela; mover a legenda de cores para dentro do conteúdo montado por `alternarPremiados` (só aparece quando o painel está aberto) — ver `research.md` §7 (depends on T016)
- [X] T018 [US4] Em `src/main/java/br/com/dpsnqmk/controller/web/PaginasController.java`, no método `jogos()`, remover `model.addAttribute("resultados", jogoService.resultadosSorteios())` (depends on T017)
- [X] T019 [US4] Em `src/main/java/br/com/dpsnqmk/service/JogoService.java`, remover o método `resultadosSorteios()` e o import de `ResultadoSorteio` (sem outros consumidores — confirmado em `research.md` §7) (depends on T018)
- [X] T020 [US4] Remover o arquivo `src/main/java/br/com/dpsnqmk/dto/ResultadoSorteio.java` (depends on T019)
- [X] T021 [US4] Validar manualmente o Cenário 4 do `quickstart.md` (painel único troca/fecha corretamente; estado vazio; nenhum endpoint novo é chamado) (depends on T020) — validado por revisão de código (sem MongoDB/Docker neste ambiente): `alternarPremiados` fecha no mesmo id, substitui no id diferente, mostra mensagem de estado vazio quando filtrado a 0; nenhum endpoint novo, só `GET /api/jogos/{id}/conferencia`; badge de `jogo.jsp` confirmado sem `clicavel`/`onclick`

**Checkpoint**: Todas as user stories funcionando de forma independente — feature completa

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Regressão geral antes de considerar a feature concluída

- [X] T022 [P] Rodar `mvn clean package -DskipTests` novamente para confirmar ausência de regressões (depends on T004, T008, T012, T021) — BUILD SUCCESS
- [X] T023 [P] Confirmar a seção "Regressão geral" do `quickstart.md` (tabela combinada não existe mais em nenhuma forma; `/precos` inalterado; badges de `jogo.jsp` continuam estáticos, sem cursor de link; cadastro de jogo em `/jogos/cadastro` continua funcionando e o novo jogo aparece corretamente como card) (depends on T004, T008, T012, T021) — validado por revisão de código (sem MongoDB/Docker neste ambiente): `git diff --stat` confirma que `jogos-cadastro.jsp` e `ConcursoRestController` (endpoint `/precos`) estão intocados; `git diff --stat` geral fica restrito exatamente aos 6 arquivos previstos em `plan.md`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: Sem dependências — pode começar imediatamente
- **Foundational (Phase 2)**: Pulada nesta feature (sem infraestrutura nova compartilhada — ver justificativa acima)
- **User Stories (Phase 3-6)**: US1 depende só de Setup; US2, US3 e US4 dependem de US1 (a grade de cards precisa existir antes de custo total, ganho total e o badge clicável terem onde aparecer) — ver `plan.md`/spec "Why this priority" de cada story
- **Polish (Phase 7)**: Depende de US1, US2, US3 e US4 estarem completas

### User Story Dependencies

- **US1 (P1)**: Nenhuma dependência de outra story — cria a grade de cards
- **US2 (P2)**: Depende de US1 (edita o mesmo card criado em T003)
- **US3 (P2)**: Depende de US1 (edita o mesmo card) e reaproveita o mesmo método (`JogoService.listarComResumo()`) alterado por US2 — por isso T010 depende de T006
- **US4 (P3)**: Depende de US1 (o badge clicável vive dentro do card); independente de US2/US3 no dado que exibe (não usa custo/ganho), mas convive no mesmo arquivo `jogos.jsp`

### Parallel Opportunities

- T002 (CSS) e T003 (JSP) em Phase 3: T002 pode rodar antes/em paralelo com o início de T003, mas T003 consome as classes de T002, então T002 deve terminar primeiro
- T005 (Phase 4) e T013 (Phase 6) tocam arquivos diferentes de outras tasks em andamento e podem ser adiantadas em paralelo com o fim de outra phase
- T022 e T023 (Polish) em paralelo entre si

---

## Parallel Example: Início de US1

```bash
# T002 (CSS) não depende de nada e pode começar imediatamente após Setup:
Task: "Adicionar .grade-jogos e .botao-fechar em estilo.css (US1 / T002)"
# T003 só começa depois de T002 existir, pois consome as classes novas
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Completar Phase 1: Setup
2. Completar Phase 3: User Story 1
3. **PARAR e VALIDAR**: Cenário 1 do `quickstart.md`
4. Já é um incremento entregável (grade de cards funcionando, com o custo unitário antigo ainda exibido)

### Incremental Delivery

1. Setup → base pronta
2. US1 → validar independentemente → entregar (grade de cards)
3. US2 → validar independentemente → entregar (custo total correto)
4. US3 → validar independentemente → entregar (ganho total)
5. US4 → validar independentemente → entregar (painel único de sorteios premiados, tabela combinada removida)
6. Polish → build limpo + regressão geral

---

## Notes

- [P] = arquivos diferentes, sem dependência entre si
- Não há testes automatizados neste repositório — cada fase termina com uma validação manual referenciando `quickstart.md`
- Nenhum novo endpoint REST é criado; `GET /api/jogos` muda de forma (campo renomeado + campo novo, ver `contracts/api-jogos.md`) e `GET /api/jogos/{id}/conferencia` ganha um novo consumidor em JS, sem mudar de forma
- T019/T020 removem `resultadosSorteios()`/`ResultadoSorteio` só depois que `jogos.jsp` (T017) e `PaginasController` (T018) já não os consomem mais — evita quebrar um checkpoint intermediário
