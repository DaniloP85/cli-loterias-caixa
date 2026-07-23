# Feature Specification: Destaque persistente e navegação de acertos por concurso nos cards de teimosinha

**Feature Branch**: `006-navegacao-acertos-concurso`

**Created**: 2026-07-23

**Status**: Draft

**Input**: User description: "Destaque de acertos em jogos expirados + indicação do concurso de comparação + navegação para concursos anteriores. Removemos a restrição implementada em 005-destaque-acertos-pendentes que só mostrava o destaque de acertos (bolinhas verdes) nas dezenas grandes enquanto o jogo tivesse concursos pendentes — o destaque deve aparecer também para jogos expirados (sem pendentes), sempre referente ao último concurso já apurado do intervalo do jogo. Cada card passa a exibir um rótulo indicando com qual concurso o destaque atual está comparando (ex.: 'comparação com o concurso 3740'). Um botão '<' ao lado desse rótulo permite navegar para o concurso anterior dentro do intervalo do jogo, recalculando e re-destacando as dezenas acertadas daquele concurso e atualizando o rótulo. Fora de escopo: qualquer versão mobile do fluxo de cadastro/conferência de jogos."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Ver o destaque de acertos mesmo em teimosinhas já expiradas (Priority: P1)

Como usuário, quero ver no card de uma teimosinha já concluída (sem concursos pendentes) o destaque das dezenas que coincidiram com o último concurso apurado do seu intervalo, para conferir o resultado final da aposta sem precisar abrir nenhum painel adicional.

**Why this priority**: É o problema relatado pelo usuário — hoje, exatamente quando uma teimosinha termina (o momento em que o resultado final importa mais), o destaque desaparece porque a regra atual (005-destaque-acertos-pendentes) só o exibe enquanto há concursos pendentes. Sem esta mudança, as outras duas histórias não têm o que exibir para jogos expirados.

**Independent Test**: Com uma teimosinha totalmente concluída (todos os concursos do intervalo já apurados, nenhum pendente) que teve pelo menos um acerto no último concurso do intervalo, acessar `/jogos` e confirmar que as dezenas coincidentes aparecem destacadas nas dezenas grandes do card, da mesma forma que já acontece hoje para teimosinhas ainda ativas.

**Acceptance Scenarios**:

1. **Given** uma teimosinha totalmente concluída (sem concursos pendentes), **When** o usuário acessa `/jogos`, **Then** as dezenas do card que coincidem com o resultado do último concurso do intervalo aparecem destacadas, do mesmo jeito que hoje já acontece para teimosinhas ativas.
2. **Given** uma teimosinha ainda ativa (com concursos pendentes), **When** o usuário acessa `/jogos`, **Then** o comportamento de destaque continua exatamente como hoje (sem mudanças para este caso).
3. **Given** uma teimosinha (ativa ou expirada) cujo intervalo ainda não teve nenhum concurso apurado, **When** o usuário observa o card, **Then** nenhuma dezena aparece destacada, pois não há nenhum concurso apurado para comparar.

---

### User Story 2 - Saber com qual concurso o destaque está comparando (Priority: P2)

Como usuário, quero ver no card a qual concurso o destaque atual das dezenas se refere, para não precisar adivinhar ou contar manualmente qual foi o último concurso apurado da teimosinha.

**Why this priority**: Complementa a US1 — sem essa indicação, o destaque manual passa a informação "algo aqui coincidiu", mas não diz com o quê, especialmente relevante agora que o destaque passa a ser exibido também em jogos expirados, cujo intervalo pode ter dezenas de concursos.

**Independent Test**: Em qualquer card de `/jogos` que já exiba o destaque de acertos (US1), confirmar que um rótulo próximo às dezenas informa o número do concurso ao qual aquele destaque se refere.

**Acceptance Scenarios**:

1. **Given** um card exibindo o destaque de acertos do concurso mais recente apurado do intervalo, **When** o usuário observa o card, **Then** um rótulo próximo às dezenas exibe explicitamente o número desse concurso (ex.: "comparação com o concurso 3740").
2. **Given** um jogo cujo intervalo ainda não teve nenhum concurso apurado, **When** o usuário observa o card, **Then** o rótulo de comparação não é exibido (não há concurso para referenciar).

---

### User Story 3 - Navegar para concursos anteriores dentro do intervalo do jogo (Priority: P3)

Como usuário, quero poder voltar a comparação do card para concursos anteriores dentro do intervalo da teimosinha, um de cada vez, para revisar como foram os acertos em sorteios passados daquele mesmo jogo sem sair da tela de conferência.

**Why this priority**: É um refinamento sobre as duas histórias anteriores — só faz sentido depois que o destaque e o rótulo de concurso já existem. Agrega valor de forma independente (navegação concurso a concurso), mas não é o problema mais urgente relatado.

**Independent Test**: Em um card com o rótulo de comparação visível (US2), clicar no controle de navegação para trás e confirmar que o destaque das dezenas grandes e o rótulo do concurso passam a refletir o concurso imediatamente anterior dentro do intervalo do jogo, mantendo essa navegação restrita aos concursos daquele jogo.

**Acceptance Scenarios**:

1. **Given** um card comparando com o concurso mais recente apurado (ex.: 3740) de um jogo cujo intervalo é 3667–3740, **When** o usuário aciona o controle de voltar, **Then** o card passa a destacar os acertos do concurso 3739 e o rótulo é atualizado para refletir esse concurso.
2. **Given** o card já está comparando com o concurso 3739 do mesmo exemplo, **When** o usuário aciona o controle de voltar novamente, **Then** o card passa a comparar com o concurso 3738.
3. **Given** o card está comparando com o primeiro concurso do intervalo do jogo (concurso inicial), **When** o usuário observa o controle de voltar, **Then** o controle fica indisponível/desativado, impedindo navegar para um concurso fora do intervalo do jogo.
4. **Given** o usuário navegou para um concurso anterior em um card, **When** ele recarrega a página `/jogos`, **Then** o card volta a exibir, por padrão, o destaque do concurso mais recente apurado (a navegação não é lembrada entre visitas).
5. **Given** duas teimosinhas diferentes exibidas simultaneamente em `/jogos`, **When** o usuário navega para um concurso anterior em uma delas, **Then** a outra teimosinha continua exibindo seu próprio concurso mais recente apurado, sem ser afetada.

---

### Edge Cases

- Jogo com intervalo de um único concurso (`concursoInicial` igual a `concursoFinal`): se esse concurso já foi apurado, o destaque e o rótulo aparecem normalmente, mas o controle de voltar já nasce indisponível (não há concurso anterior dentro do intervalo).
- Concurso mais recente apurado teve zero acertos: nenhuma dezena aparece destacada, mas o rótulo de comparação com aquele concurso continua sendo exibido normalmente.
- Jogo ativo com concursos pendentes no fim do intervalo: a navegação para trás nunca encontra um concurso pendente pelo caminho, pois os pendentes ficam sempre no final (mais recente) do intervalo, nunca entre concursos já apurados.
- Usuário navega para trás em um card e, em seguida, a base é atualizada com um novo concurso apurado para aquele jogo: como o estado de navegação não é lembrada entre visitas (ver cenário 4 da US3), uma nova visita à página volta a mostrar o novo concurso mais recente apurado.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: O sistema DEVE destacar, nas dezenas grandes do card de um jogo, os acertos do concurso mais recente já apurado do intervalo do jogo, independentemente de o jogo ainda ter concursos pendentes ou já estar totalmente concluído (revoga a restrição de 005-destaque-acertos-pendentes que escondia o destaque ao não restar nenhum concurso pendente).
- **FR-002**: Quando o intervalo de um jogo ainda não tiver nenhum concurso apurado, o sistema NÃO DEVE exibir destaque nem rótulo de comparação para esse card.
- **FR-003**: O sistema DEVE exibir, junto às dezenas destacadas de cada card, um rótulo indicando explicitamente o número do concurso ao qual aquele destaque se refere.
- **FR-004**: O sistema DEVE oferecer, junto ao rótulo de comparação, um controle que permita ao usuário navegar para o concurso imediatamente anterior dentro do intervalo do jogo.
- **FR-005**: Ao navegar para um concurso anterior, o sistema DEVE recalcular e atualizar tanto o destaque nas dezenas grandes quanto o rótulo, refletindo os acertos e o número desse concurso especificamente.
- **FR-006**: O controle de navegar para trás DEVE ficar indisponível quando o concurso em exibição for o primeiro do intervalo do jogo (`concursoInicial`), impedindo navegar para fora do intervalo.
- **FR-007**: A navegação para concursos anteriores é um estado momentâneo da visualização: ao recarregar ou revisitar a página, cada card DEVE voltar a exibir por padrão o concurso mais recente já apurado do seu intervalo.
- **FR-008**: A navegação para concursos anteriores em um card NÃO DEVE afetar o que é exibido nos demais cards de outros jogos.

### Key Entities

- **Jogo (teimosinha)**: entidade já existente. Passa a expor, para cada concurso do seu intervalo `[concursoInicial, concursoFinal]` já apurado, quais dezenas jogadas coincidiram com aquele concurso específico — não apenas do concurso mais recente, mas de qualquer concurso apurado do intervalo, para viabilizar a navegação da US3.
- **Seleção de concurso em exibição**: estado momentâneo por card (não persistido), representando qual concurso dentro do intervalo do jogo está atualmente sendo usado para o destaque e o rótulo — inicia sempre no concurso mais recente apurado e pode ser movido para trás pela navegação da US3.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Em 100% dos jogos totalmente concluídos (sem concursos pendentes) que tiveram ao menos um concurso apurado, o card exibe o destaque de acertos do último concurso apurado do intervalo — hoje esse percentual é 0%.
- **SC-002**: Em 100% dos cards que exibem destaque, o usuário identifica o número do concurso comparado sem abrir nenhum painel ou tela adicional.
- **SC-003**: Usuários revisam os acertos de qualquer concurso anterior dentro do intervalo de um jogo permanecendo no card, sem navegar para outra página.
- **SC-004**: Ao alcançar o primeiro concurso do intervalo de um jogo, 100% das tentativas de continuar navegando para trás são bloqueadas de forma visível, sem gerar erro ou comparação fora do intervalo do jogo.

## Assumptions

- "Concurso mais recente já apurado" mantém a mesma definição usada em 005-destaque-acertos-pendentes: o de maior número dentro de `[concursoInicial, concursoFinal]` que já possui resultado importado na base.
- Esta feature se aplica apenas aos cards da página de conferência de jogos (`/jogos`); a página de detalhe de um jogo individual (`/jogos/{id}`) permanece fora de escopo, como já definido em 005-destaque-acertos-pendentes.
- Não há necessidade de um controle para avançar de volta ao concurso mais recente sem recarregar a página — revisitar/recarregar `/jogos` já cumpre esse papel (FR-007). Um controle de avançar pode ser considerado em iteração futura, mas não faz parte desta feature.
- O destaque e o rótulo desta feature reaproveitam o mesmo par de cores (fundo/texto do badge "premiado") já definido em 005-destaque-acertos-pendentes; nenhuma nova cor é introduzida.
- Fora de escopo: qualquer versão mobile/app do fluxo de cadastro ou conferência de jogos — tratada como iniciativa futura separada, sem impacto nos requisitos desta feature.
- Fora de escopo: alterar a definição de quais concursos contam como "pendentes" ou o cálculo do resumo (premiados/não premiados/pendentes) do jogo — esta feature altera apenas a exibição do destaque nas dezenas grandes e adiciona a navegação por concurso.
