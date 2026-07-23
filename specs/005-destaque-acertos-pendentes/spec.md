# Feature Specification: Destaque de acertos do concurso mais recente nos cards de teimosinha

**Feature Branch**: `005-destaque-acertos-pendentes`

**Created**: 2026-07-23

**Status**: Draft

**Input**: User description: "Tem uma outra regra que eu quero colocar nesses cards que é a gente sempre vai mostrar os acertos ali nos nas 'dezenas-grandes' que ele jogou Independente se for mais ou menos do que 11 pensando na Lotofácil. Então as dezenas vão estar ali expostas no card e eu sempre vou marcar a dezena que ele teve acerto, se ele acertou uma, devemos marcar ela com a cor que utilizamos no '.badge.premiado' inclusive a cor do texto, só que como a diferença eu vou marcar sempre com o último concurso que teve isso se ele ainda tiver com cursos correndo na teimosia dele [...] enquanto ele tiver concursos pendentes a gente vai marcar no card os acertos que ele teve Independente se foi mais de 11 ou menos de 11 o último concurso sempre vai aparecer para ele [...] no cenário de hoje que ontem foi o concurso 3741, eu não joguei não deveria marcar nas 'dezenas-grandes' porque as duas teimosinhas que tenho não tem aposta para esse concurso, mas quando eu atualizar a base vou poder ver esse comportamento na teimosinha de 3742 ao 3763. [...] na coluna Dezenas jogadas (acertos destacados), podemos suavizar o verde das dezenas acertadas."

## Clarifications

### Session 2026-07-23

- Q: A cor mais suave em "Dezenas jogadas (acertos destacados)" usa a mesma classe CSS `.dezena.acertada` também usada na página individual `/jogos/{id}` (fora de escopo desta feature). Suavizar essa classe globalmente afeta as duas telas. Isso é aceitável, ou o destaque suave deve ficar isolado só no painel de `/jogos`? → A: Suavizar globalmente — a mesma classe compartilhada é ajustada, afetando também `/jogos/{id}`.
- Q: No card, junto com as dezenas grandes destacadas do concurso mais recente, o usuário precisa ver de qual concurso são esses acertos, ou a cor sozinha já basta? → A: Só a cor, sem indicar o número do concurso — o card não exibe qual concurso originou o destaque.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Ver no card os acertos do concurso mais recente, enquanto a teimosinha ainda está ativa (Priority: P1)

Como usuário, quero ver diretamente no card da teimosinha (nas dezenas grandes que joguei) quais números coincidiram com o concurso mais recente já apurado, mesmo quando a quantidade de acertos não atinge a faixa mínima de premiação, para acompanhar meu desempenho recente sem precisar abrir o painel de sorteios premiados.

**Why this priority**: É a regra central pedida — sem ela, não há como diferenciar visualmente uma teimosinha "esquentando" de uma que não acertou nada, e o valor da mudança inteira depende dela.

**Independent Test**: Com uma teimosinha que ainda tem concursos pendentes e já teve pelo menos um concurso do seu intervalo apurado, acessar a página de conferência e confirmar que as dezenas do card que coincidiram com o sorteio desse concurso mais recente aparecem destacadas com a cor do badge "premiado" (fundo e texto), independentemente da quantidade de acertos.

**Acceptance Scenarios**:

1. **Given** uma teimosinha com concursos pendentes e com o concurso mais recente do seu intervalo já apurado, **When** o usuário acessa a página de conferência, **Then** as dezenas do card que coincidem com o resultado desse concurso aparecem destacadas com a cor do badge "premiado" (mesma cor de fundo e de texto).
2. **Given** o concurso mais recente apurado de uma teimosinha teve menos acertos do que a faixa mínima de premiação da loteria (por exemplo, menos de 11 na lotofácil), **When** o usuário observa o card, **Then** os acertos ainda assim aparecem destacados normalmente nas dezenas grandes.
3. **Given** uma teimosinha totalmente concluída (nenhum concurso pendente restante), **When** o usuário observa o card, **Then** nenhuma dezena aparece destacada nas dezenas grandes, mesmo que o último concurso do intervalo tenha tido acertos.
4. **Given** uma teimosinha com concursos pendentes cujo intervalo ainda não teve nenhum concurso apurado (por exemplo, cadastrada para concursos futuros que ainda não foram sorteados), **When** o usuário observa o card, **Then** nenhuma dezena aparece destacada nas dezenas grandes.
5. **Given** uma teimosinha ativa cujo card já mostra o destaque do concurso mais recente, **When** a base é atualizada e um novo concurso dentro do intervalo é apurado, **Then** o destaque passa a refletir os acertos desse novo concurso, substituindo o destaque anterior.

---

### User Story 2 - Suavizar a cor de acerto na tabela de sorteios premiados (Priority: P3)

Como usuário, quero que o verde usado para destacar as dezenas acertadas na coluna "Dezenas jogadas (acertos destacados)" seja mais suave do que o atual, para tornar a leitura da tabela mais confortável.

**Why this priority**: É um ajuste visual isolado, independente da regra de destaque nos cards (US1) — pode ser entregue e percebido sozinho, sem depender de nenhuma outra mudança.

**Independent Test**: Abrir o painel de sorteios premiados de uma teimosinha com concursos premiados e confirmar que a cor de destaque das dezenas acertadas na coluna "Dezenas jogadas (acertos destacados)" é visualmente mais suave que a versão atual, mantendo a legibilidade do número.

**Acceptance Scenarios**:

1. **Given** o painel de sorteios premiados exibido, **When** o usuário observa a coluna "Dezenas jogadas (acertos destacados)", **Then** as dezenas acertadas aparecem destacadas com uma tonalidade de verde mais suave do que a usada atualmente, mantendo o número legível.

---

### Edge Cases

- Teimosinha recém-cadastrada cujo intervalo de concursos ainda não teve nenhum concurso apurado: nenhuma dezena destacada nas dezenas grandes até que o primeiro concurso desse intervalo seja apurado.
- Teimosinha totalmente expirada (sem concursos pendentes): nenhuma dezena destacada nas dezenas grandes, independentemente do resultado do último concurso do intervalo.
- Concurso mais recente apurado de uma teimosinha teve 0 acertos: nenhuma dezena aparece destacada (não há números coincidentes a marcar), sem erro ou destaque incorreto.
- Duas teimosinhas cadastradas com intervalos que não cobrem o concurso mais recentemente sorteado (por exemplo, uma já expirada e outra começando depois dele): nenhuma das duas exibe destaque até que um concurso dentro do respectivo intervalo seja apurado.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: O sistema DEVE destacar, nas dezenas grandes exibidas no card de uma teimosinha, os números que coincidiram com o resultado do concurso mais recente já apurado dentro do intervalo de concursos daquela teimosinha.
- **FR-002**: O destaque nas dezenas grandes DEVE aparecer independentemente da quantidade de acertos ter atingido ou não a faixa mínima de premiação da loteria daquela teimosinha.
- **FR-003**: O destaque nas dezenas grandes DEVE usar a mesma cor de fundo e a mesma cor de texto já usadas no badge "premiado", sem exibir no card o número do concurso ao qual o destaque se refere (a cor por si só é o indicador).
- **FR-004**: O destaque nas dezenas grandes DEVE aparecer somente enquanto a teimosinha ainda tiver ao menos um concurso pendente; ao não restar nenhum concurso pendente, o card não deve exibir mais nenhum destaque nessas dezenas.
- **FR-005**: Quando o intervalo de uma teimosinha ainda não tiver nenhum concurso apurado, nenhuma dezena grande deve aparecer destacada até que o primeiro concurso desse intervalo seja apurado.
- **FR-006**: Ao ser apurado um novo concurso dentro do intervalo pendente de uma teimosinha, o destaque nas dezenas grandes DEVE passar a refletir os acertos desse concurso mais recente, substituindo qualquer destaque anterior.
- **FR-007**: O sistema DEVE utilizar uma tonalidade de verde mais suave que a atual para destacar dezenas acertadas onde quer que o estilo "Dezenas jogadas / sorteadas (acertos destacados)" seja usado hoje — tanto no painel de sorteios premiados de `/jogos` quanto na tabela "Concurso a concurso" de `/jogos/{id}` — mantendo o número legível.

### Key Entities

- **Jogo (teimosinha)**: entidade já existente; para viabilizar o destaque nas dezenas grandes, passa a ser necessário identificar, para cada teimosinha, qual foi o concurso mais recente já apurado dentro do seu intervalo e quais das dezenas jogadas coincidiram com o resultado desse concurso.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Em 100% das teimosinhas com concursos pendentes e com pelo menos um concurso do intervalo já apurado, o card exibe as dezenas acertadas do concurso mais recente destacadas com a cor do badge "premiado".
- **SC-002**: Em 100% das teimosinhas totalmente concluídas (sem concursos pendentes), nenhuma dezena aparece destacada no card, mesmo com acertos no último concurso do intervalo.
- **SC-003**: Usuários identificam quais dezenas de uma teimosinha ativa acertaram no concurso mais recente sem precisar abrir o painel de sorteios premiados.
- **SC-004**: A cor de destaque da coluna "Dezenas jogadas (acertos destacados)" é percebida como mais suave que a anterior em avaliação visual, mantendo contraste de leitura adequado.

## Assumptions

- "Concurso mais recente já apurado" de uma teimosinha é o de maior número dentro do intervalo `[concursoInicial, concursoFinal]` que já possui resultado importado na base (deixou de estar pendente).
- Considera-se que uma teimosinha "ainda tem concursos pendentes" quando a contagem do badge "pendente" do seu resumo é maior que zero; ao chegar a zero, ela é tratada como expirada para efeito deste destaque, e o destaque nas dezenas grandes deixa de ser exibido.
- O destaque nas dezenas grandes reaproveita exatamente o par de cores (fundo e texto) já usado no badge "premiado" — diferente do estilo escuro usado hoje na coluna "Dezenas jogadas (acertos destacados)".
- Para suavizar a coluna "Dezenas jogadas (acertos destacados)", assume-se reaproveitar esse mesmo par de cores do badge "premiado", unificando visualmente os dois indicadores de acerto; caso o usuário prefira uma tonalidade diferente, isso pode ser ajustado na fase de planejamento.
- A regra de destaque nas dezenas grandes (US1) se aplica somente à página de conferência de jogos (`/jogos`) e aos seus cards; a página de detalhe de um jogo individual (`/jogos/{id}`) permanece fora de escopo para essa regra, como já definido em atividade anterior. Já a suavização de cor (US2, FR-007) reaproveita o mesmo estilo compartilhado entre as duas páginas, então se aplica também a `/jogos/{id}` como efeito colateral aceito (ver Clarifications).
- Apenas o concurso mais recente apurado do intervalo de cada teimosinha influencia o destaque — concursos anteriores do mesmo intervalo não alteram o que é exibido nas dezenas grandes.
