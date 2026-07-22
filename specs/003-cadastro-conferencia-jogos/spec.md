# Feature Specification: Separar "Meus jogos" em página de cadastro e página de conferência

**Feature Branch**: `003-cadastro-conferencia-jogos`

**Created**: 2026-07-22

**Status**: Draft

**Input**: User description: "Mais uma alteração que eu quero aqui eu quero criar uma página só para cadastrar ou seja o mecanismo que a gente tem para cadastrar ele terá uma parte na exclusiva. será uma página somente para cadastrar jogos e não nesta página de Conferência vamos dizer assim essa página que a gente tem de meus jogos ela tem que ser quebrado em duas vai ser uma página para cadastrar jogos e uma outra página para conferir os jogos e aí nessa página de conferir os jogos a gente entra no detalhe depois para não ficar muito grande atividade"

## Clarifications

### Session 2026-07-22

- Q: Depois que o cadastro de um jogo é concluído com sucesso na nova página de cadastro, o que deve acontecer? → A: Fica na página de cadastro, mostra uma mensagem de confirmação inline e um link/botão para ir à página de conferência — permite cadastrar outro jogo em seguida sem sair da página.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Página exclusiva para cadastrar jogos (Priority: P1)

Como usuário, quero uma página dedicada só ao cadastro de um novo jogo (teimosinha) — escolha da loteria, volante clicável de dezenas, concurso inicial, quantidade de concursos e descrição — sem a lista de jogos já cadastrados nem a tabela de sorteios premiados aparecendo junto, para que o formulário de cadastro fique curto, focado e sem distrações.

**Why this priority**: É a ação que o usuário executa com mais atenção (escolher dezenas, conferir a loteria certa) — hoje ela compete visualmente com a lista de jogos e a tabela de sorteios premiados na mesma página. Isolar o cadastro é o pedido central desta mudança.

**Independent Test**: Pode ser testado acessando a página de cadastro isoladamente, preenchendo o formulário e salvando um jogo, sem que a lista de jogos cadastrados ou a tabela de sorteios premiados apareçam nessa página em nenhum momento.

**Acceptance Scenarios**:

1. **Given** o usuário acessa a página de cadastro, **When** a página termina de carregar, **Then** apenas o formulário de cadastro (seleção de loteria, volante de dezenas, concurso inicial, quantidade de concursos, descrição e botão salvar) é exibido.
2. **Given** o usuário está na página de cadastro, **When** ele observa a página, **Then** não há lista de jogos cadastrados nem tabela de "sorteios premiados" nela.
3. **Given** o usuário preenche o formulário corretamente e salva, **When** o cadastro é concluído com sucesso, **Then** o sistema permanece na página de cadastro, exibe uma confirmação inline e um link para ver o jogo na página de conferência, com o formulário pronto para um novo cadastro em seguida.

---

### User Story 2 - Página exclusiva para conferir jogos (Priority: P2)

Como usuário, quero uma página dedicada para ver os jogos que já cadastrei — com dezenas, concursos, descrição, custo da aposta, resumo de acertos (premiado/não premiado/pendente) e a tabela de "sorteios premiados" — sem o formulário de cadastro de um novo jogo aparecendo no topo, para focar em acompanhar os resultados sem a distração do formulário.

**Why this priority**: É a tela para a qual o usuário volta com mais frequência (checar se um jogo foi premiado), mas hoje ela nasce sempre abaixo do formulário de cadastro. Depende da página de cadastro existir separadamente (US1) para fazer sentido como divisão.

**Independent Test**: Pode ser testado acessando a página de conferência isoladamente, com um ou mais jogos já cadastrados, e verificando que a lista de jogos e a tabela de sorteios premiados aparecem, mas o formulário/volante de cadastro de um novo jogo não aparece nela.

**Acceptance Scenarios**:

1. **Given** o usuário tem jogos cadastrados, **When** ele acessa a página de conferência, **Then** vê a lista de jogos cadastrados (loteria, dezenas, concursos, descrição, custo, resumo de acertos) e a tabela de "sorteios premiados", sem nenhum formulário de cadastro de novo jogo na página.
2. **Given** o usuário ainda não tem nenhum jogo cadastrado, **When** ele acessa a página de conferência, **Then** vê uma mensagem de lista vazia com um caminho claro (link/botão) para a página de cadastro.
3. **Given** o usuário está na página de conferência, **When** ele exclui um jogo da lista, **Then** o jogo é removido normalmente, do mesmo jeito que funciona hoje.

---

### User Story 3 - Navegação clara entre as duas páginas (Priority: P3)

Como usuário, quero conseguir ir de uma página para a outra (de cadastro para conferência e vice-versa) através de links visíveis na navegação, sem precisar digitar endereços, para não me perder entre as duas telas que antes eram uma só.

**Why this priority**: Refinamento de navegação — a divisão em si (US1 e US2) já entrega valor sozinha; esta história garante que o usuário não perca o caminho entre as duas telas novas.

**Independent Test**: Pode ser testado navegando pelo menu/topo do site a partir de qualquer página do sistema e confirmando que é possível chegar tanto à página de cadastro quanto à página de conferência em no máximo um clique a partir da navegação principal.

**Acceptance Scenarios**:

1. **Given** o usuário está em qualquer página do sistema, **When** ele olha a navegação principal, **Then** encontra um caminho direto tanto para "cadastrar jogo" quanto para "conferir jogos".
2. **Given** o usuário está na página de cadastro, **When** ele quer apenas consultar os jogos já cadastrados, **Then** encontra um link direto para a página de conferência sem precisar voltar à navegação principal.

---

### Edge Cases

- Um link/favorito antigo para a página única de "Meus jogos" deve continuar funcionando, levando o usuário para uma das duas novas páginas (a de conferência, por ser a visão de retorno mais frequente) em vez de resultar em erro.
- A página de detalhe de conferência de um jogo específico (aberta a partir de um jogo da lista) continua existindo e é acessada a partir da nova página de conferência; o redesenho aprofundado dessa página de detalhe fica fora do escopo desta mudança (será tratado depois, conforme pedido do usuário).
- Excluir um jogo a partir da página de conferência deve atualizar a lista exibida sem exigir que o usuário volte à página de cadastro.
- Cadastrar um jogo para um concurso futuro continua permitido e continua aparecendo como "pendente" na página de conferência, sem nenhuma mudança nessa regra.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: O sistema DEVE oferecer uma página dedicada exclusivamente ao cadastro de um novo jogo (teimosinha), contendo seleção de loteria, volante de seleção de dezenas, concurso inicial, quantidade de concursos, descrição e ação de salvar.
- **FR-002**: A página de cadastro NÃO DEVE exibir a lista de jogos já cadastrados nem a tabela de "sorteios premiados".
- **FR-003**: O sistema DEVE oferecer uma página dedicada exclusivamente à conferência dos jogos já cadastrados, contendo a lista de jogos (loteria, dezenas, concursos, descrição, custo da aposta, resumo de acertos) e a tabela de "sorteios premiados".
- **FR-004**: A página de conferência NÃO DEVE exibir o formulário/volante de cadastro de um novo jogo.
- **FR-005**: A ação de excluir um jogo cadastrado DEVE continuar disponível a partir da página de conferência, com o mesmo comportamento de hoje (confirmação antes de excluir, lista atualizada após a exclusão).
- **FR-006**: Ao concluir o cadastro de um jogo com sucesso, o sistema DEVE permanecer na página de cadastro, exibir uma confirmação inline do cadastro e oferecer um link direto para vê-lo na página de conferência, mantendo o formulário pronto para um novo cadastro em seguida.
- **FR-007**: A navegação principal do sistema DEVE oferecer acesso direto tanto à página de cadastro quanto à página de conferência, a partir de qualquer página do sistema.
- **FR-008**: Quando não houver nenhum jogo cadastrado, a página de conferência DEVE exibir uma mensagem de estado vazio com um caminho direto (link/botão) para a página de cadastro.
- **FR-009**: A página de detalhe de conferência de um jogo individual (acionada a partir de um item da lista) DEVE continuar acessível a partir da nova página de conferência, sem alterações de conteúdo nesta mudança.
- **FR-010**: Esta reestruturação NÃO DEVE alterar nenhuma API, modelo de dados, regra de premiação/conferência ou comportamento de cálculo já existente — a mudança é restrita à organização das páginas e à navegação entre elas.
- **FR-011**: Um acesso ao endereço antigo da página única de "Meus jogos" DEVE continuar funcionando, direcionando o usuário para a página de conferência.

### Key Entities

Não há novas entidades de dados — o jogo (teimosinha) cadastrado continua o mesmo; esta mudança reorganiza apenas em quais páginas suas informações são exibidas.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Em 100% dos acessos à página de cadastro, apenas o formulário de cadastro de jogo é exibido — sem lista de jogos ou tabela de sorteios premiados.
- **SC-002**: Em 100% dos acessos à página de conferência, a lista de jogos e a tabela de sorteios premiados são exibidas — sem o formulário de cadastro de um novo jogo.
- **SC-003**: Usuários conseguem alternar entre a página de cadastro e a página de conferência em no máximo 1 clique a partir de qualquer uma das duas, sem precisar digitar um endereço.
- **SC-004**: Nenhuma funcionalidade existente (cadastro de jogo, exclusão de jogo, cálculo de custo, resumo de acertos, tabela de sorteios premiados, conferência individual de um jogo) apresenta regressão após a divisão em duas páginas.

## Assumptions

- A página única "Meus jogos" (`/jogos`) é dividida em duas páginas distintas: uma de cadastro e uma de conferência; os endereços exatos de cada página serão definidos na fase de planejamento técnico, mas o endereço atual (`/jogos`) passa a apontar para a página de conferência, por ser a visão à qual o usuário retorna com mais frequência (checar resultados), enquanto o cadastro passa a viver em um endereço novo e dedicado.
- A entrada de navegação hoje chamada "Meus jogos" é substituída por duas entradas na navegação principal (uma para cadastro, outra para conferência), conforme pedido pelo usuário.
- A página de detalhe de conferência de um jogo individual (`/jogos/{id}`) permanece como está nesta mudança — o usuário indicou explicitamente que o aprofundamento dessa página será tratado em uma atividade futura, para não deixar esta mudança muito grande.
- Ajustes visuais já solicitados separadamente para o volante e para a tabela de referência de preços (grid de 10 colunas e remoção da tabela de preços) se aplicam à página de cadastro, já que é ela quem passa a hospedar esse formulário — sem alterar o escopo desta mudança, que é puramente de reorganização em duas páginas.
- Não há controle de acesso ou múltiplos usuários envolvidos — o sistema continua de uso único, sem autenticação, como hoje.
- Nenhuma mudança de API, persistência ou regra de negócio é necessária — a divisão é inteiramente de apresentação (páginas JSP e navegação).
