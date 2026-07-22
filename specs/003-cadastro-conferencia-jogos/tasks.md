---

description: "Task list for feature: Separar \"Meus jogos\" em página de cadastro e página de conferência"
---

# Tasks: Separar "Meus jogos" em página de cadastro e página de conferência

**Input**: Design documents from `/specs/003-cadastro-conferencia-jogos/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, quickstart.md

**Tests**: Not requested — este repositório não tem framework de testes automatizados configurado (`CLAUDE.md`); validação é manual via `quickstart.md`, referenciada como tasks abaixo.

**Organization**: Tasks agrupadas por user story (US1/US2/US3, prioridades de `spec.md`); cada fase é incrementalmente testável de forma independente, na ordem P1 → P2 → P3.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Pode rodar em paralelo (arquivos diferentes, sem dependência entre si)
- **[Story]**: A qual user story a task pertence (US1, US2, US3)
- Paths são exatos, relativos à raiz do repo (Spring Boot monolito único — ver `plan.md`)

---

## Phase 1: Setup

**Purpose**: Estabelecer uma baseline funcionando antes de tocar em qualquer arquivo (sem testes automatizados neste repo, então um build limpo é o único gate automático)

- [X] T001 Confirmar que a baseline builda: `mvn clean package -DskipTests` (sem mudanças de código ainda — só checkpoint pré-mudança) — BUILD SUCCESS

---

## Phase 2: Foundational

**Purpose**: N/A — não há pré-requisito bloqueante compartilhado entre US1, US2 e US3 além do que já está descrito nos próprios arquivos existentes (`PaginasController.java`, `jogos.jsp`, `cabecalho.jspf`). Nenhuma infraestrutura nova é necessária (sem novo endpoint REST, sem nova entidade — ver `data-model.md`), então esta fase é pulada, seguindo o mesmo padrão já usado em `specs/002-ajustes-ui-jogos/tasks.md`.

---

## Phase 3: User Story 1 - Página exclusiva para cadastrar jogos (Priority: P1) 🎯 MVP

**Goal**: Criar `GET /jogos/cadastro` (`jogos-cadastro.jsp`) contendo só o formulário de cadastro de jogo (loteria, volante, concurso inicial, quantidade de concursos, descrição); ao salvar com sucesso, a página permanece com confirmação inline + link para a conferência, sem lista de jogos nem tabela de sorteios premiados

**Independent Test**: Acessar `/jogos/cadastro`, confirmar que só o formulário aparece, cadastrar um jogo e confirmar que a página permanece com uma mensagem de confirmação e um link para `/jogos` (`quickstart.md` Cenário 1)

### Implementation for User Story 1

- [X] T002 [P] [US1] Em `src/main/java/br/com/dpsnqmk/controller/web/PaginasController.java`, adicionar `@GetMapping("/jogos/cadastro")` `cadastroJogo(Model model)`: monta `List<ConfigLoteria>` (mesma lógica hoje usada em `jogos()`, via `Arrays.stream(Loteria.values())...`), `model.addAttribute("loterias", loterias)`, `model.addAttribute("abaAtiva", "jogos-cadastro")`, retorna `"jogos-cadastro"`
- [X] T003 [P] [US1] Criar `src/main/webapp/WEB-INF/jsp/jogos-cadastro.jsp`: `<c:set var="titulo" value="Cadastrar jogo — Loterias Caixa"/>`, `<h1>Cadastrar jogo</h1>`, copiar de `jogos.jsp` o card `<div class="card form-jogo"><h2>Cadastrar jogo (teimosinha)</h2>...</div>` (form completo com `<select id="campo-loteria">`, campos de concurso/qtd/descrição, `#contador-dezenas`, `#volante`, botão salvar, `#erro-jogo`), acrescentar um `<p id="sucesso-jogo" class="sucesso" hidden>Jogo cadastrado! <a href="/jogos">ver na conferência</a></p>` logo após `#erro-jogo`, e migrar para o `<script>` desta página as funções `configurar()`, `montarVolante()`, `alternar(evento)`, `atualizarContador()` e a chamada final `configurar()` (copiadas de `jogos.jsp`)
- [X] T004 [US1] Em `jogos-cadastro.jsp`, reescrever `cadastrar(evento)`: no sucesso (`resposta.status === 201`), remover `location.reload()`; em vez disso, limpar `selecionadas = []`, remover a classe `.selecionada` de todos os `.dezena-volante` marcados, resetar `#campo-concurso`, `#campo-qtd` (para `1`) e `#campo-descricao`, chamar `atualizarContador()`, esconder `#erro-jogo` e exibir `#sucesso-jogo` (depends on T003) — implementado via nova função `limparFormulario()` chamada no sucesso, mantendo `configurar()`/`montarVolante()`/`alternar()`/`atualizarContador()` intactos
- [X] T005 [P] [US1] Em `src/main/resources/static/css/estilo.css`, adicionar uma regra `.sucesso` (mesmo padrão de cor de `.badge.premiado`, ex. `color: #166534;`) ao lado da regra `.erro` existente, para estilizar a confirmação inline
- [X] T006 [US1] Validar manualmente o Cenário 1 do `quickstart.md` (`/jogos/cadastro` mostra só o formulário; ao salvar, permanece na página com confirmação + link para `/jogos`; volante e campos resetados para novo cadastro) (depends on T004, T005) — validado rodando a aplicação localmente (`java -jar target/loterias-caixa.war`, sem MongoDB neste ambiente) e via `curl http://localhost:8091/jogos/cadastro`: retorna 200, HTML contém só o card "Cadastrar jogo" (sem lista de jogos nem "Sorteios premiados"), `#sucesso-jogo` e a regra `.sucesso` presentes; fluxo de submit/reset de formulário não pôde ser exercitado fim a fim sem MongoDB, mas foi validado por revisão de código (`limparFormulario()` + exibição de `#sucesso-jogo` no branch de sucesso do `fetch`)

**Checkpoint**: User Story 1 completa e testável de forma independente — MVP entregável

---

## Phase 4: User Story 2 - Página exclusiva para conferir jogos (Priority: P2)

**Goal**: Reduzir `GET /jogos` (`jogos.jsp`) para conter só a lista de jogos cadastrados e a tabela "Sorteios premiados", sem o formulário/volante de cadastro

**Independent Test**: Com jogos cadastrados, acessar `/jogos` e confirmar que a lista + "Sorteios premiados" aparecem sem nenhum formulário de cadastro; excluir um jogo continua funcionando; com zero jogos, a mensagem de lista vazia linka para `/jogos/cadastro` (`quickstart.md` Cenário 2)

### Implementation for User Story 2

- [X] T007 [US2] Em `src/main/webapp/WEB-INF/jsp/jogos.jsp`, remover o `<div class="card form-jogo">...</div>` (form + volante) inteiro, e do `<script>` remover as funções `configurar()`, `montarVolante()`, `alternar(evento)`, `atualizarContador()`, `cadastrar(evento)` e a chamada final `configurar()` — manter apenas `excluir(id)` no script restante (essas funções passam a existir só em `jogos-cadastro.jsp`, criado em T003)
- [X] T008 [US2] Em `jogos.jsp`, atualizar a mensagem de lista vazia de `"Nenhum jogo cadastrado ainda. Cadastre acima a sua primeira teimosinha."` para um texto que não referencia mais "acima" e inclui um link `<a href="/jogos/cadastro">cadastre seu primeiro jogo</a>` (depends on T007)
- [X] T009 [P] [US2] Em `src/main/java/br/com/dpsnqmk/controller/web/PaginasController.java`, no método `jogos()` (`GET /jogos`), remover a construção e o `model.addAttribute("loterias", loterias)` (não é mais consumido por esta view após T007) — mantém `jogos`, `resultados` e `abaAtiva`
- [X] T010 [US2] Validar manualmente o Cenário 2 do `quickstart.md` (`/jogos` mostra só lista + "Sorteios premiados", sem form; exclusão funciona; estado vazio linka para `/jogos/cadastro`) (depends on T008, T009) — validado por `mvn clean package -DskipTests` (BUILD SUCCESS) e revisão de código: `jogos.jsp` não contém mais form/volante/`data-precos`/scripts de cadastro, só `excluir(id)`; mensagem de lista vazia linka `/jogos/cadastro`; `PaginasController.jogos()` não constrói mais `loterias`. Não foi possível exercitar `/jogos` fim a fim (sem MongoDB neste ambiente — `GET /jogos` retorna 500 por `MongoTimeoutException` ao conectar em `localhost:27017`, confirmado via stack trace nos logs; comportamento pré-existente, não relacionado a esta mudança) — `/jogos/cadastro`, que não depende de Mongo, segue retornando 200

**Checkpoint**: User Stories 1 e 2 funcionando de forma independente

---

## Phase 5: User Story 3 - Navegação clara entre as duas páginas (Priority: P3)

**Goal**: A navegação principal oferece acesso direto a `/jogos/cadastro` e `/jogos`, com a aba correta destacada em cada página da área de jogos

**Independent Test**: A partir de qualquer página do sistema, alcançar tanto a página de cadastro quanto a de conferência em no máximo 1 clique via nav; a aba ativa correta é destacada em cada uma (`quickstart.md` Cenário 3)

### Implementation for User Story 3

- [X] T011 [P] [US3] Em `src/main/webapp/WEB-INF/jsp/comum/cabecalho.jspf`, trocar `<a href="/jogos" class="aba ${abaAtiva == 'jogos' ? 'ativa' : ''}">Meus jogos</a>` por duas entradas: `<a href="/jogos/cadastro" class="aba ${abaAtiva == 'jogos-cadastro' ? 'ativa' : ''}">Cadastrar jogo</a>` e `<a href="/jogos" class="aba ${abaAtiva == 'jogos-conferencia' ? 'ativa' : ''}">Conferir jogos</a>`
- [X] T012 [US3] Em `PaginasController.jogos()` (`GET /jogos`), trocar `model.addAttribute("abaAtiva", "jogos")` para `model.addAttribute("abaAtiva", "jogos-conferencia")` (depends on T009, mesmo método/arquivo)
- [X] T013 [US3] Em `PaginasController.jogo()` (`GET /jogos/{id}`), trocar `model.addAttribute("abaAtiva", "jogos")` para `model.addAttribute("abaAtiva", "jogos-conferencia")` (depends on T012, mesmo arquivo)
- [X] T014 [US3] Validar manualmente o Cenário 3 do `quickstart.md` (duas abas na nav, destaque correto em cadastro/conferência) e a seção "Regressão geral" (link antigo `/jogos` continua funcionando, `/jogos/{id}` com aba certa) (depends on T011, T012, T013) — validado rodando a aplicação localmente: `curl /jogos/cadastro` mostra as duas abas na nav com "Cadastrar jogo" ativa; `curl /ml` mostra as mesmas duas abas com nenhuma delas ativa (confirma que o nav é global). `/jogos` (endereço antigo) continua mapeado e retornando a página de conferência (só falha por `MongoTimeoutException` neste ambiente sem Mongo, mesma limitação já registrada em T010). `/jogos/{id}` não pôde ser exercitado ao vivo (depende de um jogo já cadastrado no Mongo), validado por revisão de código: `PaginasController.jogo()` seta `abaAtiva="jogos-conferencia"`, igual a `jogos()`

**Checkpoint**: Todas as user stories funcionando de forma independente — feature completa

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Regressão geral antes de considerar a feature concluída

- [X] T015 [P] Rodar `mvn clean package -DskipTests` novamente para confirmar ausência de regressões (depends on T006, T010, T014) — BUILD SUCCESS
- [X] T016 [P] Confirmar regressão geral per `quickstart.md` (seção "Regressão geral"): `POST /api/jogos`, `DELETE /api/jogos/{id}` e `GET /api/jogos` inalterados; nenhum novo endpoint REST criado (FR-010) (depends on T006, T010, T014) — validado via `git diff --stat`: apenas `PaginasController.java`, `estilo.css`, `cabecalho.jspf` e `jogos.jsp` foram modificados (+ `jogos-cadastro.jsp` novo); `ConcursoRestController`/`JogoRestController` (onde vivem `POST /api/jogos`, `DELETE /api/jogos/{id}`, `GET /api/jogos`) não aparecem no diff — nenhum endpoint REST criado, alterado ou removido

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: Sem dependências — pode começar imediatamente
- **Foundational (Phase 2)**: Pulada nesta feature (sem pré-requisito compartilhado — ver justificativa acima)
- **User Stories (Phase 3-5)**: Cada uma depende só de Setup; a ordem de execução recomendada é P1 → P2 → P3 para manter sempre um caminho de cadastro funcionando (ver Implementation Strategy), mas nenhuma story bloqueia estruturalmente a outra
- **Polish (Phase 6)**: Depende de US1, US2 e US3 estarem completas

### User Story Dependencies

- **US1 (P1)**: Nenhuma dependência de US2/US3 — cria arquivos novos (`jogos-cadastro.jsp`, método novo em `PaginasController`); testável isoladamente mesmo se `jogos.jsp` ainda não tiver sido reduzida
- **US2 (P2)**: Não depende tecnicamente de US1 (edita só `jogos.jsp` e `PaginasController.jogos()`), mas fica mais coerente para o usuário final ser feita depois de US1 (senão o cadastro fica temporariamente indisponível)
- **US3 (P3)**: Depende de US1 e US2 já existirem — os dois links de navegação só fazem sentido apontando para páginas já divididas

### Parallel Opportunities

- T002 (controller), T003 (nova JSP) e T005 (CSS) em Phase 3 tocam arquivos diferentes e podem ser feitas em paralelo
- T009 (Phase 4) toca um arquivo diferente de T007/T008 e pode ser feita em paralelo com elas
- T011 (Phase 5) toca um arquivo diferente de T012/T013 e pode ser feita em paralelo com elas
- T015 e T016 (Polish) em paralelo entre si

---

## Parallel Example: User Story 1

```bash
# Depois de T001, dentro da Phase 3 (US1):
Task: "Adicionar GET /jogos/cadastro em PaginasController.java (T002)"
Task: "Criar jogos-cadastro.jsp com form + volante migrados (T003)"
Task: "Adicionar regra .sucesso em estilo.css (T005)"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Completar Phase 1: Setup
2. Completar Phase 3: User Story 1
3. **PARAR e VALIDAR**: Cenário 1 do `quickstart.md`
4. Já é um incremento entregável (página de cadastro dedicada, com `/jogos` ainda mostrando o formulário duplicado até a Phase 4)

### Incremental Delivery

1. Setup → base pronta
2. US1 → validar independentemente → entregar (`/jogos/cadastro` funcionando)
3. US2 → validar independentemente → entregar (`/jogos` vira conferência pura)
4. US3 → validar independentemente → entregar (nav com as duas abas)
5. Polish → build limpo + regressão geral

### Parallel Team Strategy

Com múltiplos desenvolvedores, US1 pode começar imediatamente após Setup; US2 e US3 têm dependências leves (ver acima) que tornam a ordem sequencial mais segura para evitar uma janela sem caminho de cadastro visível na navegação.

---

## Notes

- [P] = arquivos diferentes, sem dependência entre si
- Não há testes automatizados neste repositório — cada fase termina com uma validação manual referenciando `quickstart.md`
- Nenhum novo serviço, endpoint REST ou entidade é criado nesta feature — apenas roteamento de páginas (`PaginasController`), uma view JSP nova e edições pontuais em views/nav existentes (FR-010)
- A ordem P1 → P2 → P3 evita uma janela em que a navegação (US3) aponte para páginas ainda não divididas — por isso US3 depende de US1 e US2, mesmo sem dependência de arquivo direta
