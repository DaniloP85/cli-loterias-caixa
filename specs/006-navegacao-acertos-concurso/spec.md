# Feature Specification: Destaque persistente e paginação de concursos em cards de teimosinha

**Feature Branch**: `006-navegacao-acertos-concurso`

**Created**: 2026-07-23

**Status**: Draft

**Input**: User description (consolidado a partir de duas rodadas de refinamento): "Destaque de acertos em jogos expirados + indicação do concurso de comparação + navegação entre concursos. Removemos a restrição implementada em 005-destaque-acertos-pendentes que só mostrava o destaque de acertos (bolinhas verdes) nas dezenas grandes enquanto o jogo tivesse concursos pendentes — o destaque deve aparecer também para jogos expirados (sem pendentes), sempre referente ao último concurso já apurado do intervalo do jogo. Cada card passa a exibir um rótulo indicando com qual concurso o destaque atual está comparando (ex.: 'comparação com o concurso 3740'). Regra simplificada de navegação (ver `card_tela_conferencia.md`): se o jogo é uma teimosinha (intervalo com mais de um concurso), o card ganha controles de paginação — voltar `<` e avançar `>` — que percorrem, um de cada vez, os concursos já apurados do intervalo, recalculando o destaque e o rótulo a cada passo. Se o jogo não é uma teimosinha (intervalo de um único concurso), os controles de paginação não aparecem; o card mostra apenas o rótulo indicando o concurso comparado. Fora de escopo: qualquer versão mobile do fluxo de cadastro/conferência de jogos."

## Clarifications

### Session 2026-07-23

- Q: Uma teimosinha (intervalo com mais de um concurso) cujo intervalo ainda não teve NENHUM concurso apurado — a FR-002 já diz que não há destaque nem rótulo nesse caso. Os controles de voltar/avançar devem aparecer (desabilitados) mesmo sem rótulo, ou ficam ocultos junto com o rótulo? → A: Mostrar controles desabilitados — o card já sinaliza que é uma teimosinha paginável, mesmo sem nenhum concurso apurado ainda.
- Q: Com os controles de paginação visíveis (porém desabilitados) numa teimosinha sem nenhum concurso apurado, o que o rótulo de comparação deve exibir, já que não há número de concurso para mostrar? → A: Texto indicativo de espera (ex.: "aguardando apuração") no lugar do número do concurso.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Ver o destaque de acertos mesmo em jogos já expirados (Priority: P1)

Como usuário, quero ver no card de um jogo já concluído (sem concursos pendentes) o destaque das dezenas que coincidiram com o último concurso apurado do seu intervalo, para conferir o resultado final da aposta sem precisar abrir nenhum painel adicional.

**Why this priority**: É o problema relatado pelo usuário — hoje, exatamente quando um jogo termina (o momento em que o resultado final importa mais), o destaque desaparece porque a regra atual (005-destaque-acertos-pendentes) só o exibe enquanto há concursos pendentes. Sem esta mudança, as outras histórias não têm o que exibir para jogos expirados.

**Independent Test**: Com um jogo totalmente concluído (todos os concursos do intervalo já apurados, nenhum pendente) que teve pelo menos um acerto no último concurso do intervalo, acessar `/jogos` e confirmar que as dezenas coincidentes aparecem destacadas nas dezenas grandes do card, da mesma forma que já acontece hoje para jogos ainda ativos.

**Acceptance Scenarios**:

1. **Given** um jogo totalmente concluído (sem concursos pendentes), **When** o usuário acessa `/jogos`, **Then** as dezenas do card que coincidem com o resultado do último concurso do intervalo aparecem destacadas, do mesmo jeito que hoje já acontece para jogos ativos.
2. **Given** um jogo ainda ativo (com concursos pendentes), **When** o usuário acessa `/jogos`, **Then** o comportamento de destaque continua exatamente como hoje (sem mudanças para este caso).
3. **Given** um jogo (ativo ou expirado) cujo intervalo ainda não teve nenhum concurso apurado, **When** o usuário observa o card, **Then** nenhuma dezena aparece destacada, pois não há nenhum concurso apurado para comparar.

---

### User Story 2 - Saber com qual concurso o destaque está comparando (Priority: P2)

Como usuário, quero ver no card a qual concurso o destaque atual das dezenas se refere, para não precisar adivinhar ou contar manualmente qual foi o concurso apurado usado na comparação.

**Why this priority**: Complementa a US1 — sem essa indicação, o destaque manual passa a informação "algo aqui coincidiu", mas não diz com o quê, especialmente relevante agora que o destaque passa a ser exibido também em jogos expirados, cujo intervalo pode ter dezenas de concursos.

**Independent Test**: Em qualquer card de `/jogos` que já exiba o destaque de acertos (US1), confirmar que um rótulo próximo às dezenas informa o número do concurso ao qual aquele destaque se refere — independentemente de o jogo ser ou não uma teimosinha (ver US3).

**Acceptance Scenarios**:

1. **Given** um card exibindo o destaque de acertos do concurso mais recente apurado do intervalo, **When** o usuário observa o card, **Then** um rótulo próximo às dezenas exibe explicitamente o número desse concurso (ex.: "comparação com o concurso 3740").
2. **Given** um jogo que não é teimosinha (intervalo de um único concurso) cujo concurso ainda não foi apurado, **When** o usuário observa o card, **Then** o rótulo de comparação não é exibido (não há concurso para referenciar).
3. **Given** um jogo com intervalo de um único concurso (`concursoInicial` igual a `concursoFinal`) já apurado, **When** o usuário observa o card, **Then** o rótulo de comparação é exibido normalmente, mesmo sem nenhum controle de paginação ao lado.
4. **Given** uma teimosinha (intervalo com mais de um concurso) cujo intervalo ainda não teve nenhum concurso apurado, **When** o usuário observa o card, **Then** o rótulo exibe um texto indicativo de espera (ex.: "aguardando apuração") no lugar do número do concurso.

---

### User Story 3 - Paginar entre os concursos apurados de uma teimosinha (Priority: P3)

Como usuário, quero poder avançar e voltar a comparação do card entre os concursos apurados do intervalo de uma teimosinha (jogo com mais de um concurso no intervalo), um de cada vez, para revisar como foram os acertos em qualquer sorteio passado daquele jogo sem sair da tela de conferência.

**Why this priority**: É um refinamento sobre as duas histórias anteriores — só faz sentido depois que o destaque e o rótulo de concurso já existem. Agrega valor de forma independente (paginação concurso a concurso), mas não é o problema mais urgente relatado.

**Independent Test**: Em um card de uma teimosinha (intervalo com mais de um concurso) com o rótulo de comparação visível (US2), clicar no controle de voltar e confirmar que o destaque das dezenas grandes e o rótulo do concurso passam a refletir o concurso imediatamente anterior apurado dentro do intervalo do jogo; em seguida clicar no controle de avançar e confirmar que a comparação volta ao concurso seguinte.

**Acceptance Scenarios**:

1. **Given** um card comparando com o concurso mais recente apurado (ex.: 3740) de uma teimosinha cujo intervalo é 3667–3740, **When** o usuário aciona o controle de voltar, **Then** o card passa a destacar os acertos do concurso 3739 e o rótulo é atualizado para refletir esse concurso.
2. **Given** o card já está comparando com o concurso 3739 do mesmo exemplo, **When** o usuário aciona o controle de avançar, **Then** o card volta a comparar com o concurso 3740 (o mais recente apurado).
3. **Given** o card está comparando com o primeiro concurso apurado do intervalo da teimosinha, **When** o usuário observa o controle de voltar, **Then** o controle fica indisponível/desativado, impedindo navegar para um concurso fora do intervalo apurado.
4. **Given** o card está comparando com o concurso mais recente apurado do intervalo da teimosinha, **When** o usuário observa o controle de avançar, **Then** o controle fica indisponível/desativado, impedindo navegar além do concurso mais recente já apurado.
5. **Given** um jogo cujo intervalo é de um único concurso (`concursoInicial` igual a `concursoFinal` — não é uma teimosinha), **When** o usuário observa o card, **Then** nenhum controle de voltar ou avançar é exibido, apenas o rótulo indicando o concurso comparado.
6. **Given** o usuário navegou para um concurso anterior em um card, **When** ele recarrega a página `/jogos`, **Then** o card volta a exibir, por padrão, o destaque do concurso mais recente apurado (a navegação não é lembrada entre visitas).
7. **Given** duas teimosinhas diferentes exibidas simultaneamente em `/jogos`, **When** o usuário navega para um concurso anterior em uma delas, **Then** a outra teimosinha continua exibindo seu próprio concurso mais recente apurado, sem ser afetada.
8. **Given** uma teimosinha cujo intervalo ainda não teve nenhum concurso apurado, **When** o usuário observa o card, **Then** os controles de voltar e avançar aparecem, porém ambos indisponíveis, e o rótulo exibe um texto indicativo de espera (ex.: "aguardando apuração") no lugar do número do concurso.

---

### Edge Cases

- Jogo com intervalo de um único concurso (`concursoInicial` igual a `concursoFinal`): não é considerado uma teimosinha; se esse concurso já foi apurado, o rótulo aparece normalmente, mas nenhum controle de voltar/avançar é exibido. Se esse único concurso ainda não foi apurado, nem rótulo nem controles aparecem.
- Teimosinha (intervalo com mais de um concurso) que ainda não teve nenhum concurso apurado: os controles de voltar e avançar aparecem, porém ambos indisponíveis, e o rótulo exibe um texto indicativo de espera (ex.: "aguardando apuração") no lugar do número do concurso.
- Teimosinha (intervalo com mais de um concurso) que teve apenas um concurso apurado até agora (o restante ainda pendente): os controles de voltar e avançar aparecem, porém ambos indisponíveis, pois não há concurso apurado anterior nem posterior para navegar; o rótulo já exibe o número desse único concurso apurado (não o texto de espera, que é exclusivo do caso sem nenhum apurado).
- Concurso mais recente apurado teve zero acertos: nenhuma dezena aparece destacada, mas o rótulo de comparação com aquele concurso continua sendo exibido normalmente.
- Teimosinha ativa com concursos pendentes no fim do intervalo: o controle de avançar nunca alcança um concurso pendente, pois os pendentes ficam sempre no final (mais recente) do intervalo, nunca entre concursos já apurados — o avançar para no concurso mais recente apurado.
- Usuário navega para trás em um card e, em seguida, a base é atualizada com um novo concurso apurado para aquele jogo: como o estado de navegação não é lembrado entre visitas (ver cenário 6 da US3), uma nova visita à página volta a mostrar o novo concurso mais recente apurado.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: O sistema DEVE destacar, nas dezenas grandes do card de um jogo, os acertos do concurso mais recente já apurado do intervalo do jogo, independentemente de o jogo ainda ter concursos pendentes ou já estar totalmente concluído (revoga a restrição de 005-destaque-acertos-pendentes que escondia o destaque ao não restar nenhum concurso pendente).
- **FR-002**: Quando o intervalo de um jogo que NÃO é teimosinha (concurso único) ainda não tiver esse concurso apurado, o sistema NÃO DEVE exibir destaque nem rótulo de comparação para esse card. Para teimosinhas sem nenhum concurso apurado, ver FR-012.
- **FR-003**: O sistema DEVE exibir, junto às dezenas destacadas de cada card, um rótulo indicando explicitamente o número do concurso ao qual aquele destaque se refere, sempre que houver ao menos um concurso apurado no intervalo do jogo — independentemente de o jogo ser ou não uma teimosinha (para teimosinhas sem nenhum concurso apurado, ver FR-012).
- **FR-004**: O sistema DEVE classificar um jogo como "teimosinha" quando seu intervalo abranger mais de um concurso (`concursoInicial` diferente de `concursoFinal`); um jogo com `concursoInicial` igual a `concursoFinal` NÃO é uma teimosinha.
- **FR-005**: Somente para jogos classificados como teimosinha, o sistema DEVE exibir, junto ao rótulo de comparação, controles de paginação — voltar e avançar — que permitam ao usuário mover a comparação um concurso apurado por vez dentro do intervalo do jogo. Esses controles aparecem mesmo quando ainda não há nenhum concurso apurado (ver FR-012).
- **FR-006**: Para jogos que não são teimosinha (intervalo de um único concurso), o sistema NÃO DEVE exibir nenhum controle de voltar/avançar, apenas o rótulo de comparação (quando houver concurso apurado).
- **FR-007**: Ao acionar voltar ou avançar, o sistema DEVE recalcular e atualizar tanto o destaque nas dezenas grandes quanto o rótulo, refletindo os acertos e o número do concurso apurado exibido após a navegação.
- **FR-008**: O controle de voltar DEVE ficar indisponível quando o concurso em exibição for o primeiro concurso apurado do intervalo do jogo, impedindo navegar para fora do intervalo apurado.
- **FR-009**: O controle de avançar DEVE ficar indisponível quando o concurso em exibição for o concurso mais recente já apurado do intervalo do jogo, impedindo navegar além dele (nunca entra em concursos pendentes).
- **FR-010**: A paginação entre concursos é um estado momentâneo da visualização: ao recarregar ou revisitar a página, cada card DEVE voltar a exibir por padrão o concurso mais recente já apurado do seu intervalo.
- **FR-011**: A navegação em um card NÃO DEVE afetar o que é exibido nos demais cards de outros jogos.
- **FR-012**: Para uma teimosinha cujo intervalo ainda não tiver nenhum concurso apurado, o sistema DEVE exibir os controles de voltar e avançar já indisponíveis (nada para navegar) junto a um rótulo com um texto indicativo de espera (ex.: "aguardando apuração") no lugar do número do concurso.

### Key Entities

- **Jogo**: entidade já existente. Passa a expor, para cada concurso do seu intervalo `[concursoInicial, concursoFinal]` já apurado, quais dezenas jogadas coincidiram com aquele concurso específico — não apenas do concurso mais recente, mas de qualquer concurso apurado do intervalo, para viabilizar a paginação da US3. Também passa a expor se é uma "teimosinha" (intervalo com mais de um concurso) ou não (intervalo de um único concurso), o que determina se os controles de paginação aparecem.
- **Seleção de concurso em exibição**: estado momentâneo por card (não persistido), representando qual concurso apurado dentro do intervalo do jogo está atualmente sendo usado para o destaque e o rótulo — inicia sempre no concurso mais recente apurado e pode ser movido para trás ou para frente pela paginação da US3, respeitando os limites do intervalo apurado.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Em 100% dos jogos totalmente concluídos (sem concursos pendentes) que tiveram ao menos um concurso apurado, o card exibe o destaque de acertos do último concurso apurado do intervalo — hoje esse percentual é 0%.
- **SC-002**: Em 100% dos cards que exibem destaque, o usuário identifica o número do concurso comparado sem abrir nenhum painel ou tela adicional.
- **SC-003**: Em 100% dos cards de teimosinha (intervalo com mais de um concurso), o usuário revisa os acertos de qualquer concurso apurado dentro do intervalo do jogo permanecendo no card, sem navegar para outra página.
- **SC-004**: Em 100% dos cards de jogos que não são teimosinha (intervalo de um único concurso), nenhum controle de paginação é exibido, evitando confundir o usuário com uma navegação que não se aplica.
- **SC-005**: Ao alcançar o primeiro ou o último concurso apurado do intervalo de uma teimosinha, 100% das tentativas de continuar navegando nessa direção são bloqueadas de forma visível, sem gerar erro ou comparação fora do intervalo apurado do jogo.
- **SC-006**: Em 100% dos cards de teimosinha sem nenhum concurso apurado, os controles de paginação aparecem indisponíveis junto a um texto indicativo de espera, deixando claro que a teimosinha existe mas ainda não há resultado para comparar.

## Assumptions

- "Concurso mais recente já apurado" mantém a mesma definição usada em 005-destaque-acertos-pendentes: o de maior número dentro de `[concursoInicial, concursoFinal]` que já possui resultado importado na base.
- "Teimosinha" é definida, para efeito desta feature, como todo jogo cujo intervalo `[concursoInicial, concursoFinal]` abrange mais de um concurso; um jogo com `concursoInicial` igual a `concursoFinal` (aposta para um único concurso) não é uma teimosinha e por isso não ganha controles de paginação — apenas o rótulo de comparação.
- Esta feature se aplica apenas aos cards da página de conferência de jogos (`/jogos`); a página de detalhe de um jogo individual (`/jogos/{id}`) permanece fora de escopo, como já definido em 005-destaque-acertos-pendentes.
- O layout exato dos controles de paginação (posição, ícones, estado desabilitado) segue o esboço em `card_tela_conferencia.md`; o usuário indicou que fará uma versão mais detalhada da tela — refinamentos puramente visuais não bloqueiam esta especificação, que trata do comportamento e das regras de habilitação.
- O destaque e o rótulo desta feature reaproveitam o mesmo par de cores (fundo/texto do badge "premiado") já definido em 005-destaque-acertos-pendentes; nenhuma nova cor é introduzida.
- Fora de escopo: qualquer versão mobile/app do fluxo de cadastro ou conferência de jogos — tratada como iniciativa futura separada, sem impacto nos requisitos desta feature.
- Fora de escopo: alterar a definição de quais concursos contam como "pendentes" ou o cálculo do resumo (premiados/não premiados/pendentes) do jogo — esta feature altera apenas a exibição do destaque nas dezenas grandes e adiciona a paginação por concurso para teimosinhas.
