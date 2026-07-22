# Feature Specification: Ajustes de UI na página de jogos (grid do volante e remoção da tabela de preços)

**Feature Branch**: `002-ajustes-ui-jogos`

**Created**: 2026-07-22

**Status**: Draft

**Input**: User description: "A primeira alteração que eu quero é você vai corrigir o CSS da página de cadastro de jogos até aqueles inputs com os números e ali a gente precisa trocar o comportamento do CSS de `.volante` (grid-template-columns de `repeat(auto-fill, minmax(2.6rem, 1fr))` para `repeat(10, minmax(2.6rem, 1fr))` — só o comportamento do grid muda). A outra alteração é remover a tabela de preços (referência de custo por quantidade de dezenas) que aparece em \"Meus jogos\", logo abaixo dos inputs de dezenas — não ficou boa e não faz sentido para a proposta."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Volante com grade fixa de 10 colunas (Priority: P1)

Como usuário cadastrando um jogo em "Meus jogos", ao ver o volante clicável com as dezenas disponíveis, quero que os números fiquem organizados sempre em 10 colunas, para que o layout fique previsível e alinhado (como um volante real de loteria), independentemente da largura da tela ou da quantidade de dezenas da loteria escolhida.

**Why this priority**: É a mudança visual mais visível e usada em todo cadastro de jogo — todo usuário que acessa "Meus jogos" passa pelo volante. Sem grade previsível, o alinhamento visual muda conforme a loteria (25 dezenas na lotofacil, 80 na quina, 100 na lotomania, etc.), o que hoje o layout `auto-fill` deixa inconsistente.

**Independent Test**: Pode ser testado abrindo `/jogos`, selecionando cada uma das 4 loterias (megasena, lotofacil, quina, lotomania) e conferindo visualmente que o volante sempre renderiza em 10 colunas, com a última linha incompleta apenas quando o total de dezenas não for múltiplo de 10.

**Acceptance Scenarios**:

1. **Given** a página "Meus jogos" aberta com a loteria padrão selecionada, **When** o volante é renderizado, **Then** as dezenas aparecem organizadas em exatamente 10 colunas por linha.
2. **Given** o usuário troca a loteria selecionada (ex.: de megasena para lotomania), **When** o volante é reconstruído para a nova faixa de dezenas, **Then** o grid continua fixo em 10 colunas, ajustando apenas o número de linhas.
3. **Given** uma tela estreita (mobile), **When** o volante é exibido, **Then** o grid continua com 10 colunas (a largura de cada célula encolhe, mas o número de colunas não muda).

---

### User Story 2 - Remoção da tabela de referência de preços em "Meus jogos" (Priority: P2)

Como usuário, ao acessar "Meus jogos", não quero mais ver a tabela de referência de preços por quantidade de dezenas logo abaixo do volante, pois essa tabela não agrega valor à proposta da página e polui o formulário de cadastro do jogo.

**Why this priority**: É uma limpeza de UI independente do ajuste do grid; menor impacto que a US1, mas o usuário pediu explicitamente a remoção porque a tabela "não ficou boa".

**Independent Test**: Pode ser testado abrindo `/jogos` e confirmando que não há mais nenhuma seção de "tabela de preços" (título, cabeçalho de colunas ou linhas de valores por quantidade de dezenas) visível abaixo do volante, para nenhuma das 4 loterias.

**Acceptance Scenarios**:

1. **Given** a página "Meus jogos" aberta, **When** a página termina de carregar, **Then** não existe nenhuma seção de tabela de referência de preços abaixo do volante de dezenas.
2. **Given** o usuário troca a loteria selecionada, **When** o volante é reconstruído, **Then** nenhuma tabela de preços é exibida ou remontada para a nova loteria.
3. **Given** a remoção da tabela na página, **When** o restante do formulário de cadastro de jogo (volante, botão de salvar, lista de jogos, "Sorteios premiados") é observado, **Then** essas demais seções continuam funcionando normalmente, sem quebras de layout.

### Edge Cases

- Lotomania tem 100 dezenas (múltiplo de 10) — a grade fecha exatamente em 10 linhas de 10 colunas, sem sobra.
- Loterias cuja quantidade de dezenas não é múltiplo de 10 (ex.: lotofacil com 25) devem deixar a última linha incompleta, sem quebrar o alinhamento das colunas anteriores.
- A coluna "custo da aposta" já exibida na tabela de jogos cadastrados (não a tabela de referência de preços) **não** é afetada por esta mudança — apenas a seção de referência de preços por quantidade de dezenas é removida.
- Qualquer atributo de dados (ex.: `data-precos`) e função JS usados exclusivamente para montar a tabela de referência removida devem ser removidos ou neutralizados, evitando código morto ou erros de script.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: O grid do volante de dezenas em "Meus jogos" DEVE ser renderizado com exatamente 10 colunas fixas, independentemente da largura da tela ou da loteria selecionada.
- **FR-002**: A largura mínima de cada célula do volante DEVE continuar sendo respeitada (mínimo de 2.6rem por célula), preservando a legibilidade em telas estreitas.
- **FR-003**: A página "Meus jogos" NÃO DEVE exibir mais a seção de tabela de referência de preços (título, cabeçalho e linhas por quantidade de dezenas) abaixo do volante de dezenas.
- **FR-004**: A remoção da tabela de referência de preços NÃO DEVE afetar a exibição do custo da aposta já calculado por jogo cadastrado (coluna existente na lista de jogos).
- **FR-005**: A remoção da tabela de referência de preços NÃO DEVE deixar código morto (marcação HTML vazia, script JS órfão, ou atributos de dados não utilizados) na página.
- **FR-006**: O endpoint `GET /api/loterias/{loteria}/precos` e o serviço que calcula a tabela de referência (`PremioService.tabelaReferencia`) permanecem inalterados no backend — esta mudança é apenas de apresentação na página "Meus jogos" (a UI deixa de consumir/exibir esses dados, mas a API não é removida).

### Key Entities

Não há novas entidades de dados — mudança restrita a apresentação (CSS e marcação JSP).

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Em 100% das visitas a "Meus jogos", para qualquer uma das 4 loterias suportadas, o volante de dezenas é exibido em uma grade de exatamente 10 colunas.
- **SC-002**: Em 100% das visitas a "Meus jogos", nenhuma tabela de referência de preços é exibida na página.
- **SC-003**: Nenhuma funcionalidade existente da página "Meus jogos" (seleção de loteria, seleção de dezenas no volante, cadastro do jogo, listagem de jogos com custo da aposta, "Sorteios premiados") apresenta regressão após as duas mudanças.

## Assumptions

- "Página de cadastro de jogos" e "Meus jogos" referem-se à mesma página (`/jogos`, `jogos.jsp`), que é onde o volante clicável e a tabela de preços atualmente coexistem — não há uma página separada de cadastro.
- A tabela de referência de preços a ser removida é a seção `tabela-precos` (título "Tabela de preços — ...", montada via JS a partir do atributo `data-precos` do `<option>` da loteria) descrita em `jogos.jsp`, distinta da coluna "custo da aposta" da tabela de jogos já cadastrados — que permanece.
- O backend (`PremioService.tabelaReferencia`, endpoint `/api/loterias/{loteria}/precos`) não precisa ser removido nesta feature; apenas a UI deixa de exibi-lo. Remover o endpoint fica fora de escopo, pois pode ter outros consumidores (ex.: futura tela de ML) e não foi pedido explicitamente.
- Somente a propriedade `grid-template-columns` do seletor `.volante` muda; as demais propriedades (`display`, `gap`, `max-width`, `margin-bottom`) permanecem como estão, conforme pedido explícito do usuário.
