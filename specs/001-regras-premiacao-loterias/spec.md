# Feature Specification: Regras de Aposta e Premiação por Loteria

**Feature Branch**: `001-regras-premiacao-loterias`

**Created**: 2026-07-14

**Status**: Draft

**Input**: User description: "como alterar conforme os arquivos colocados na pasta ideias, mas vamos seguir o Spec-Driven Development" — aplicar as regras de valor de aposta e premiação documentadas em `ideas/megasena.md`, `ideas/lotofacil.md`, `ideas/quina.md` e `ideas/lotomania.md` ao sistema, via Spec-Driven Development.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Ver o custo da aposta registrada (Priority: P1)

Como usuário que registra jogos ("Meus jogos"), quero ver quanto custou cada aposta (com base na quantidade de dezenas marcadas) ao lado do jogo, para acompanhar meu gasto sem precisar consultar a tabela de preços da Caixa manualmente.

**Why this priority**: É o dado mais simples e imediato de derivar (não depende do resultado do sorteio) e já se aplica a todo jogo cadastrado, inclusive os PENDENTES.

**Independent Test**: Cadastrar um jogo em cada uma das 4 loterias com diferentes quantidades de dezenas e verificar que o valor exibido bate com a tabela oficial de preços de cada loteria.

**Acceptance Scenarios**:

1. **Given** um jogo de Mega-Sena com 6 dezenas marcadas, **When** o usuário visualiza a lista de jogos, **Then** o sistema mostra o valor R$ 6,00 como custo da aposta.
2. **Given** um jogo de Lotofácil com 18 dezenas marcadas, **When** o usuário visualiza o jogo, **Then** o sistema mostra o valor R$ 2.856,00.
3. **Given** um jogo de Lotomania (sempre 50 dezenas), **When** o usuário visualiza o jogo, **Then** o sistema mostra o valor fixo R$ 3,00, sem depender da quantidade de dezenas.

---

### User Story 2 - Ver o valor do prêmio ganho (Priority: P2)

Como usuário que já confere seus jogos (PREMIADO/NAO_PREMIADO/PENDENTE), quero ver o valor em reais que ganhei quando um jogo é classificado como PREMIADO, para saber o retorno financeiro, não só a faixa de acerto.

**Why this priority**: Depende do resultado oficial do concurso (só se aplica a jogos já sorteados), por isso vem depois do custo, mas é o principal ganho de valor da funcionalidade: hoje a tela de "Sorteios premiados" mostra a faixa de acerto mas não o valor em reais.

**Independent Test**: Selecionar um concurso já importado com jogo(s) PREMIADO(S) e verificar que o valor do prêmio exibido corresponde ao valor oficial publicado pela Caixa para aquela faixa naquele concurso.

**Acceptance Scenarios**:

1. **Given** um jogo classificado como PREMIADO na faixa "Quadra", **When** o usuário visualiza a tabela de sorteios premiados, **Then** o sistema mostra o valor do prêmio pago para essa faixa naquele concurso específico.
2. **Given** um jogo de Lotomania premiado com 0 acertos, **When** o usuário visualiza o resultado, **Then** o sistema mostra o valor do prêmio da faixa "0 acertos" (regra especial já suportada na classificação, agora também no valor).
3. **Given** um jogo ainda PENDENTE (concurso futuro), **When** o usuário visualiza o jogo, **Then** o sistema não mostra valor de prêmio (indisponível até o sorteio ocorrer).

---

### User Story 3 - Consultar tabela de preços por loteria antes de apostar (Priority: P3)

Como usuário que está decidindo quantas dezenas marcar antes de cadastrar um novo jogo, quero consultar a tabela de preços de cada loteria (custo por quantidade de dezenas) dentro do próprio sistema, para comparar opções sem sair para o site da Caixa.

**Why this priority**: É informativo e de apoio à decisão, não bloqueia as outras duas histórias e tem menor frequência de uso do que ver custo/prêmio de jogos já cadastrados.

**Independent Test**: Abrir a tela/tabela de referência de uma loteria e conferir que todos os valores por quantidade de dezenas batem com a tabela oficial publicada.

**Onde aparece**: seção dentro da própria tela de cadastro de jogo (`jogos.jsp`), próxima ao volante clicável — não é uma página separada.

**Acceptance Scenarios**:

1. **Given** o usuário está na tela de cadastro de um novo jogo de Quina, **When** ele consulta a tabela de referência, **Then** vê o custo para cada quantidade válida de dezenas (5 a 15).
2. **Given** o usuário está na tela de cadastro de um jogo de Lotomania, **When** ele consulta a tabela de referência, **Then** vê apenas o valor fixo (não há variação por quantidade).

---

### Edge Cases

- Jogo com quantidade de dezenas fora do intervalo permitido pela `Loteria` (ex.: min/max já validados em `JogoService.criar`) não deve gerar erro ao calcular custo — a validação de intervalo já impede o cadastro.
- Concurso premiado em que a Caixa não publicou valor de prêmio para a faixa (ex.: faixa acumulada sem ganhador) deve ser tratado sem quebrar a exibição — mostrar "não houve ganhador nesta faixa" ou equivalente, em vez de um valor.
- Loterias com mais de um jogo do usuário premiado no mesmo concurso e mesma faixa devem somar ou listar os valores de forma que o usuário entenda que cada jogo tem seu próprio prêmio (mesmo repetindo dezenas).
- Valores de tabela de preço desatualizados em relação a eventuais reajustes futuros da Caixa não fazem parte desta funcionalidade (ver Assumptions).

## Clarifications

### Session 2026-07-15

- Q: Concursos já importados antes desta funcionalidade não têm os valores de prêmio por faixa persistidos (o DTO atual só guarda o valor da faixa principal); como tratar jogos PREMIADOS desses concursos legados? → A: Buscar o valor de prêmio sob demanda direto na API da Caixa no momento da exibição, sem persistir/fazer backfill.
- Q: Ao buscar o valor de prêmio sob demanda para um concurso legado, se a busca falhar (API indisponível/timeout), o que exibir? → A: Cachear o valor localmente após a primeira busca bem-sucedida, para não depender da API nas exibições seguintes; em caso de falha na busca, seguir o padrão já usado no projeto (ex.: SSE) de degradar sem quebrar a página — exibir o prêmio como indisponível nessa exibição, sem interromper a listagem.
- Q: Onde a tabela de referência de preços por loteria (US3) deve aparecer na interface? → A: Seção dentro da própria tela de cadastro de jogo (`jogos.jsp`), próxima ao volante clicável.
- Q: Custo/prêmio devem também aparecer no dataset de ML exportado (`DatasetService`, CSV/JSON), ou ficar restritos às páginas/API de "Meus jogos"? → A: Ficar restrito a "Meus jogos" (páginas JSP + API de jogos) — o dataset de ML por concurso não muda nesta versão.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: O sistema DEVE calcular o valor (custo) de uma aposta a partir da loteria e da quantidade de dezenas marcadas, usando as tabelas de preço oficiais documentadas em `ideas/megasena.md`, `ideas/lotofacil.md`, `ideas/quina.md` e `ideas/lotomania.md`.
- **FR-002**: O sistema DEVE exibir o custo calculado junto de cada jogo cadastrado em "Meus jogos", incluindo jogos PENDENTES (concurso ainda não sorteado).
- **FR-003**: Para a Lotomania, o sistema DEVE tratar o custo como valor fixo (R$ 3,00), independentemente da quantidade de dezenas (sempre 50).
- **FR-004**: O sistema DEVE exibir, para cada jogo classificado como PREMIADO, o valor do prêmio correspondente à faixa de acerto obtida naquele concurso específico.
- **FR-005**: O sistema NÃO DEVE exibir valor de prêmio para jogos PENDENTES ou NAO_PREMIADOS.
- **FR-006**: O sistema DEVE oferecer uma consulta de referência (por loteria) mostrando o custo da aposta para cada quantidade válida de dezenas suportada por aquela loteria, apresentada como seção da própria tela de cadastro de jogo (`jogos.jsp`), próxima ao volante clicável, em vez de página separada.
- **FR-007**: Os valores de prêmio exibidos DEVEM ser os valores oficiais de rateio já publicados pela Caixa para o concurso e faixa em questão, e NÃO um valor recalculado localmente a partir dos percentuais de distribuição documentados nos arquivos de `ideas/` — esses percentuais servem apenas de referência/documentação, não como fonte de cálculo do valor exibido ao usuário.
- **FR-007a**: Para concursos que ainda não têm o valor de prêmio por faixa persistido localmente (concursos importados antes desta funcionalidade existir), o sistema DEVE buscar esse valor sob demanda diretamente na API da Caixa no momento da exibição, sem exigir reimportação completa da base nem executar backfill automático.
- **FR-007b**: Após uma busca sob demanda bem-sucedida (FR-007a), o sistema DEVE cachear/persistir o valor obtido, para não depender de nova chamada à API da Caixa em exibições futuras do mesmo concurso/faixa. Se a busca falhar, o sistema DEVE exibir o prêmio como indisponível nessa exibição, sem interromper a listagem dos demais jogos (mesmo padrão de degradação silenciosa já usado nas SSE de importação).
- **FR-008**: O sistema DEVE suportar o cálculo de custo e a exibição de prêmio apenas para apostas individuais (um jogo = uma aposta); bolão (cotas compartilhadas) está fora de escopo nesta versão.
- **FR-009**: O sistema DEVE aplicar as regras de premiação de concursos normais para as 4 loterias suportadas (Mega-Sena, Lotofácil, Quina, Lotomania); concursos especiais (Mega da Virada, Mega 30 Anos, Lotofácil da Independência, Quina de São João) estão fora de escopo nesta versão.
- **FR-010**: O custo da aposta e o valor do prêmio DEVEM ficar restritos às páginas e à API de "Meus jogos" (jogos cadastrados pelo usuário); o dataset de ML exportado por `DatasetService` (linhas por concurso, para features estatísticas) NÃO DEVE ser alterado por esta funcionalidade.

### Key Entities *(include if feature involves data)*

- **Tabela de Preço da Aposta**: por loteria, associa uma quantidade de dezenas marcadas ao valor em reais da aposta (fixa, no caso da Lotomania).
- **Prêmio da Aposta**: valor em reais ganho por um jogo específico em um concurso específico, associado à faixa de acerto (ex.: Quadra, Quina, Sena) daquela loteria.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Para 100% das combinações válidas de loteria × quantidade de dezenas suportadas hoje pelo cadastro de jogos, o custo exibido bate com o valor oficial publicado pela Caixa.
- **SC-002**: Todo jogo classificado como PREMIADO passa a exibir também um valor em reais, sem exigir que o usuário consulte outra fonte para saber quanto ganhou.
- **SC-003**: Um usuário decidindo quantas dezenas apostar consegue comparar o custo de todas as quantidades válidas de uma loteria em uma única tela do sistema, sem sair da aplicação.
- **SC-004**: Nenhum valor financeiro (custo ou prêmio) exibido diverge do valor oficialmente publicado pela Caixa para o mesmo jogo/concurso.

## Assumptions

- As tabelas de preço de aposta (custo por quantidade de dezenas) são consideradas estáveis o suficiente para não exigir integração ao vivo com a Caixa — atualizações futuras de preço, se ocorrerem, serão tratadas como manutenção dos dados de referência, mesmo padrão hoje usado para os limites de dezenas em `enums/Loteria`.
- O valor do prêmio de um jogo PREMIADO é derivado do rateio oficial já publicado pela Caixa para o concurso (fonte de verdade), não recalculado localmente — os percentuais de distribuição documentados em `ideas/` servem só como referência de negócio, não como fórmula de cálculo do valor exibido.
- Para concursos legados (importados antes desta funcionalidade), o valor de prêmio por faixa é buscado sob demanda na API da Caixa no momento da exibição, em vez de exigir reimportação ou migração de dados existentes; o valor obtido é cacheado/persistido após a primeira busca bem-sucedida para evitar chamadas repetidas à API.
- Bolão (apostas com cotas compartilhadas) está fora do escopo desta funcionalidade — o sistema hoje só modela aposta individual.
- Concursos especiais (Mega da Virada, Mega 30 Anos, Lotofácil da Independência, Quina de São João) estão fora do escopo desta primeira versão — as regras de percentual documentadas para eles diferem das regras de concurso normal.
