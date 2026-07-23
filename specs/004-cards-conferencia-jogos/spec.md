# Feature Specification: Layout em cards na página de conferência de jogos

**Feature Branch**: `004-cards-conferencia-jogos`

**Created**: 2026-07-22

**Status**: Draft

**Input**: User description: "Essa página de Conferência a gente vai alterar bastante comportamento dela [...] vamos construir um layout novo não vai ser mais tabela, vamos mudar para cards [...] usar o layout de Grid onde a gente coloca no máximo quatro na mesma coluna [...] o custo vai ser a diferença das teimosinhas [...] multiplicado pelo valor unitário [...] o ganho [...] eu só quero saber o valor total do prêmio dessa teimosinha [...] o badge premiado ele se torna um link que vai mostrar a tabela de sorteios premiados [só] daquela teimosinha [...] os cards sempre visíveis e essa segunda tabela [...] só vai aparecer caso o usuário clique no badge premiado."

## Clarifications

### Session 2026-07-22

- Q: Ao clicar no badge "premiado" de um card, como a tabela de sorteios premiados deve se comportar e onde deve aparecer? → A: Painel único e compartilhado, abaixo de toda a grade de cards — mostra sempre uma teimosinha por vez; clicar em outro badge substitui o conteúdo pelo da nova teimosinha; clicar no mesmo badge novamente fecha o painel.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Lista de jogos em cards, não mais em tabela (Priority: P1)

Como usuário, quero ver meus jogos cadastrados (teimosinhas) organizados em cards — em vez da tabela atual — para que cada jogo fique visualmente mais claro e organizado, com a loteria e os concursos como título, as dezenas em destaque no corpo do card e um botão de exclusão direto no card.

**Why this priority**: É a mudança estrutural que todas as outras dependem — sem o card existir, não há onde colocar custo total, ganho total nem o badge expansível de premiados. Entrega valor por si só (melhor legibilidade) mesmo antes das demais mudanças.

**Independent Test**: Acessar a página de conferência com um ou mais jogos cadastrados e confirmar que a lista aparece como uma grade de cards (não mais tabela), com no máximo 4 cards por linha, cada um mostrando loteria + concursos no título, dezenas em destaque, descrição e botão de exclusão funcionando como hoje.

**Acceptance Scenarios**:

1. **Given** o usuário tem jogos cadastrados, **When** ele acessa a página de conferência, **Then** cada jogo é exibido como um card individual, e não mais como uma linha de tabela.
2. **Given** a tela é larga o suficiente, **When** os cards são exibidos, **Then** no máximo 4 cards aparecem lado a lado na mesma linha.
3. **Given** um card exibido, **When** o usuário observa seu título, **Then** vê a loteria e o intervalo de concursos da teimosinha juntos (por exemplo "lotofácil de 3667 até 3740"), no lugar das colunas separadas "Loteria" e "Concursos" de hoje.
4. **Given** um card exibido, **When** o usuário observa seu corpo, **Then** vê as dezenas jogadas com mais destaque visual do que tinham na coluna da tabela antiga, e a descrição do jogo (quando informada).
5. **Given** um card exibido, **When** o usuário clica no botão de exclusão do card (marcado como "X"), **Then** o sistema pede confirmação e remove o jogo, com o mesmo comportamento que a tabela tem hoje.

---

### User Story 2 - Custo total da teimosinha (Priority: P2)

Como usuário, quero ver o custo total de cada teimosinha (quantidade de concursos jogados multiplicada pelo valor unitário da aposta), e não apenas o valor de uma única aposta como é exibido hoje, para saber quanto realmente gastei naquela teimosinha.

**Why this priority**: Corrige uma informação financeira que hoje está incompleta (mostra só o custo de uma aposta, não do conjunto de concursos jogados) — importante, mas depende do card existir (US1) para ter onde ser exibida.

**Independent Test**: Cadastrar (ou usar) uma teimosinha que abrange mais de um concurso e confirmar que o valor de "Custo" exibido no card é a quantidade de concursos abrangidos multiplicada pelo valor unitário da aposta daquela loteria/quantidade de dezenas — não apenas o valor de uma aposta isolada.

**Acceptance Scenarios**:

1. **Given** uma teimosinha que abrange mais de um concurso, **When** o usuário observa o custo exibido no card, **Then** o valor corresponde à quantidade de concursos abrangidos multiplicada pelo valor unitário da aposta.
2. **Given** uma teimosinha que abrange exatamente um concurso, **When** o usuário observa o custo exibido, **Then** o valor é igual ao de uma única aposta (o mesmo comportamento de hoje, como caso particular da regra acima).

---

### User Story 3 - Ganho total da teimosinha (Priority: P2)

Como usuário, quero ver o ganho total de cada teimosinha — a soma dos valores de todos os prêmios que ela já recebeu — sem precisar somar manualmente ou olhar concurso por concurso, para saber rapidamente o retorno financeiro daquela teimosinha específica.

**Why this priority**: Junto com o custo total (US2), fecha o balanço financeiro que o usuário quer ver de cada teimosinha — mesma prioridade, também depende do card (US1) existir.

**Independent Test**: Usar uma teimosinha com pelo menos um concurso premiado e confirmar que o valor de "Ganhos" exibido no card é a soma dos valores de prêmio de todos os concursos premiados daquela teimosinha (não a contagem de acertos, e sim o valor em R$).

**Acceptance Scenarios**:

1. **Given** uma teimosinha com um ou mais concursos premiados, **When** o usuário observa o ganho exibido no card, **Then** o valor é a soma dos prêmios recebidos nesses concursos, em reais.
2. **Given** uma teimosinha sem nenhum concurso premiado ainda, **When** o usuário observa o ganho exibido no card, **Then** o valor mostrado é R$ 0,00.

---

### User Story 4 - Sorteios premiados por teimosinha, sob demanda (Priority: P3)

Como usuário, quero clicar no badge "premiado" de um card específico e ver a tabela de sorteios premiados só daquela teimosinha — não mais uma lista combinada de todos os meus jogos — para conseguir focar nos resultados de uma teimosinha por vez.

**Why this priority**: Refinamento sobre a experiência de consulta dos prêmios — a divisão em cards e os totais (US1-US3) já entregam valor sozinhos; esta história melhora como o usuário navega até o detalhe dos prêmios de uma teimosinha específica.

**Independent Test**: Com pelo menos duas teimosinhas cadastradas, cada uma com concursos premiados diferentes, clicar no badge "premiado" de uma delas e confirmar que a tabela exibida mostra somente os sorteios premiados daquela teimosinha — a outra teimosinha não aparece nessa tabela.

**Acceptance Scenarios**:

1. **Given** a página de conferência carregada, **When** o usuário observa a página, **Then** nenhum painel de sorteios premiados aparece visível até que ele clique em algum badge "premiado".
2. **Given** um card com pelo menos um concurso premiado, **When** o usuário clica no badge "premiado" desse card, **Then** um único painel aparece abaixo de toda a grade de cards, mostrando somente os sorteios premiados daquela teimosinha (loteria, concurso, apuração, dezenas acertadas, acertos, prêmio).
3. **Given** o painel de sorteios premiados de uma teimosinha já está visível, **When** o usuário clica novamente no badge "premiado" do mesmo card, **Then** o painel é fechado.
4. **Given** o painel de sorteios premiados de uma teimosinha está visível, **When** o usuário clica no badge "premiado" de outro card, **Then** o conteúdo do painel é substituído pelos sorteios premiados dessa nova teimosinha (apenas uma teimosinha por vez é exibida no painel).
5. **Given** um card cujo badge "premiado" mostra contagem 0, **When** o usuário clica nele, **Then** uma mensagem indica que não há prêmios ainda para aquela teimosinha, em vez de uma tabela vazia sem explicação.

---

### Edge Cases

- Tela estreita (mobile): a grade de cards deve se ajustar para menos de 4 por linha (nunca mais que 4), mantendo a legibilidade das dezenas e dos botões.
- Concurso com valor de prêmio "indisponível" (falha ao consultar a Caixa) dentro de uma teimosinha premiada: esse concurso não deve travar o cálculo do ganho total — ele simplesmente não contribui com valor até que a informação fique disponível.
- Teimosinha totalmente pendente (nenhum concurso conferido ainda): custo total exibido normalmente; ganho total R$ 0,00; badge "premiado" com contagem 0.
- Exclusão de um card cuja teimosinha está com o painel de sorteios premiados aberto no momento: o card desaparece e o painel também é fechado, sem erro.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: O sistema DEVE exibir a lista de jogos cadastrados na página de conferência como uma grade de cards, em vez da tabela usada hoje, com no máximo 4 cards por linha.
- **FR-002**: Cada card DEVE exibir no título a loteria e o intervalo de concursos da teimosinha juntos, substituindo as colunas separadas "Loteria" e "Concursos" da tabela atual.
- **FR-003**: Cada card DEVE exibir as dezenas jogadas com destaque visual maior no corpo do card do que tinham na coluna "Dezenas" da tabela atual, sem alterar quais números são exibidos.
- **FR-004**: Cada card DEVE exibir um controle de exclusão (representado como "X" no card) com o mesmo comportamento de confirmação e remoção já existente hoje.
- **FR-005**: Cada card DEVE exibir a descrição do jogo (mesmo conteúdo da coluna "Descrição" de hoje), quando informada.
- **FR-006**: Cada card DEVE exibir o custo total da teimosinha, calculado como a quantidade de concursos abrangidos pela teimosinha multiplicada pelo valor unitário da aposta — em vez do valor de uma única aposta exibido hoje.
- **FR-007**: Cada card DEVE exibir o ganho total da teimosinha, somando os valores de prêmio de todos os concursos premiados daquele jogo específico.
- **FR-008**: Cada card DEVE exibir o resumo de acertos (badges "premiado", "não premiado" e "pendente"), com a mesma contagem exibida hoje.
- **FR-009**: O badge "premiado" de cada card DEVE funcionar como um controle interativo que, ao ser acionado, exibe em um painel único e compartilhado (abaixo de toda a grade de cards) os sorteios premiados apenas daquela teimosinha (loteria, concurso, apuração, dezenas jogadas com acertos destacados, quantidade de acertos, prêmio).
- **FR-010**: O painel de sorteios premiados DEVE permanecer oculto até que algum badge "premiado" seja acionado pela primeira vez; ao ser acionado novamente no mesmo card, o painel DEVE fechar; ao ser acionado em um card diferente enquanto já está aberto, o conteúdo do painel DEVE ser substituído pelos sorteios premiados da nova teimosinha (apenas uma teimosinha por vez é exibida).
- **FR-011**: A grade de cards DEVE permanecer sempre visível, independente do painel de sorteios premiados estar aberto ou fechado.
- **FR-012**: A seção combinada "Sorteios premiados" (hoje exibida abaixo da lista, somando os prêmios de todos os jogos em uma única tabela permanente) DEIXA DE existir como bloco fixo — em seu lugar, um único painel sob demanda mostra os sorteios premiados de uma teimosinha por vez, acionado pelo badge do respectivo card.
- **FR-013**: Quando uma teimosinha não tiver nenhum sorteio premiado, acionar seu badge "premiado" DEVE exibir uma mensagem indicando a ausência de prêmios para aquela teimosinha específica, em vez de uma tabela vazia.
- **FR-014**: A exclusão de um jogo a partir do seu card DEVE continuar exigindo confirmação do usuário antes de remover, como ocorre hoje.

### Key Entities

- **Jogo (teimosinha)**: entidade já existente, sem mudança na estrutura de dados armazenada; passa a ter duas informações adicionais apresentadas na tela — custo total e ganho total, ambas derivadas a partir de dados já existentes (quantidade de concursos, valor unitário da aposta, prêmios dos concursos premiados).
- **Sorteio premiado de uma teimosinha**: mesmo conceito de "sorteio premiado" já existente no sistema, mas agora sempre apresentado associado e filtrado a uma única teimosinha por vez, em vez de agregado com os de todas as teimosinhas em uma lista só.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Em 100% dos acessos à página de conferência com jogos cadastrados, a lista aparece como cards (não mais tabela), com no máximo 4 cards por linha.
- **SC-002**: Para teimosinhas que abrangem mais de um concurso, o custo total exibido é validado manualmente como quantidade de concursos × valor unitário da aposta, em pelo menos um caso de cada uma das 4 loterias suportadas.
- **SC-003**: Para teimosinhas com concursos premiados, o ganho total exibido é validado manualmente como a soma correta dos valores de prêmio daquela teimosinha.
- **SC-004**: Usuários conseguem visualizar os sorteios premiados de uma teimosinha específica em no máximo 1 clique, sem que prêmios de outras teimosinhas apareçam misturados na mesma tabela.
- **SC-005**: Nenhuma funcionalidade existente (exclusão de jogo, resumo de acertos, conferência individual de um jogo) apresenta regressão após a mudança para o layout em cards.

## Assumptions

- Esta mudança se aplica à página de conferência de jogos (hoje `/jogos`), que passa a hospedar somente a lista de jogos cadastrados e seus resultados — sem o formulário de cadastro, conforme já decidido separadamente na reorganização em página de cadastro + página de conferência.
- A página de detalhe de conferência de um jogo individual (`/jogos/{id}`) não é afetada por esta mudança — continua como está, conforme já definido como fora de escopo em uma atividade anterior.
- O cálculo do ganho total de cada teimosinha usa os mesmos dados de premiação já existentes no sistema (os mesmos que hoje alimentam a tabela combinada de "Sorteios premiados"), calculado no momento da exibição; a decisão de também persistir esse total no banco de dados fica em aberto para a fase de planejamento técnico, sem impacto no comportamento visível ao usuário.
- Existe um único painel de sorteios premiados, compartilhado por todos os cards (não um por card): clicar no badge "premiado" de um card abre o painel (se fechado) ou o fecha (se já estiver mostrando aquela mesma teimosinha); clicar no badge de outra teimosinha enquanto o painel está aberto substitui o conteúdo, sempre mostrando uma teimosinha por vez — conforme decidido em Clarifications.
- Não há paginação nem limite de quantidade de cards nesta mudança — todos os jogos cadastrados continuam sendo exibidos, agora em formato de card em vez de linha de tabela.
- Não há controle de acesso ou múltiplos usuários envolvidos — o sistema continua de uso único, sem autenticação, como hoje.
