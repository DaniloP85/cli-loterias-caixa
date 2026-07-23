# Phase 0 Research: Layout em cards na página de conferência de jogos

Nenhum `NEEDS CLARIFICATION` restou no Technical Context do plan — as decisões abaixo documentam o raciocínio técnico e as alternativas descartadas para os pontos que o spec deixou como "o quê", não "como".

## 1. Grade de cards com no máximo 4 por linha

- **Decision**: nova classe `.grade-jogos` em `estilo.css`: `display: grid; grid-template-columns: repeat(auto-fill, minmax(240px, 1fr)); gap: 1rem;`. Nenhum `max-width` extra é necessário: o container `<main>` já tem `max-width: 1100px` (regra global existente); com `minmax(240px, 1fr)` e `gap: 1rem` (16px), cabem exatamente 4 colunas em 1100px (4×240 + 3×16 = 1008px ≤ 1100px) e uma 5ª coluna não cabe (5×240 + 4×16 = 1264px > 1100px) — o grid nunca excede 4 por linha sem precisar de media query.
- **Rationale**: reaproveita uma constraint global já existente (`main { max-width: 1100px }`) em vez de introduzir um novo breakpoint; `auto-fill` já resolve o caso de tela estreita do FR-001/Edge Cases (menos de 4 por linha, nunca mais que 4) sem CSS adicional.
- **Alternatives considered**: `repeat(4, minmax(0, 1fr))` fixo (mesmo padrão do volante em `specs/002-ajustes-ui-jogos`) — descartado porque o volante *quer* sempre exatamente 10 colunas (nunca menos), enquanto aqui o spec pede explicitamente menos de 4 em telas estreitas (Edge Cases); `repeat(4, ...)` sempre geraria 4 colunas estreitas demais em mobile. Media query dedicada (`@media (min-width: 1100px) { grid-template-columns: repeat(4, 1fr); }`) — descartada por ser redundante com o comportamento natural do `auto-fill` já dentro do `max-width` existente.

## 2. Dezenas em destaque no corpo do card

- **Decision**: reaproveitar a classe já existente `.dezena.grande` (`estilo.css`, hoje usada em `jogo.jsp` para as dezenas do jogo na página de detalhe) em vez de criar uma classe nova.
- **Rationale**: já entrega exatamente o efeito pedido ("mais destaque visual") — fonte maior, padding maior — e mantém consistência visual com a página de detalhe do jogo, sem CSS duplicado.
- **Alternatives considered**: nova classe `.dezena-card` com valores próprios — descartada por duplicar uma regra que já existe e já é usada com o mesmo propósito em outro lugar do sistema.

## 3. Botão de exclusão ("X") no card

- **Decision**: novo elemento no cabeçalho do card, com uma classe CSS nova e pequena (`.botao-fechar`, um "×" circular no canto superior direito do card), chamando o mesmo `excluir(id)` já existente no `<script>` de `jogos.jsp` — sem nenhuma mudança de comportamento (confirmação + `DELETE /api/jogos/{id}` + reload).
- **Rationale**: FR-004/FR-014 pedem exatamente o mesmo comportamento de hoje, só reposicionado visualmente; reaproveitar a função existente evita duplicar lógica de exclusão.
- **Alternatives considered**: reaproveitar `.botao.perigo` (usado hoje) sem estilo novo — descartado porque o mockup (`card_tela_conferencia.md`) pede um controle compacto tipo "X" no cabeçalho, não um botão de texto "excluir" no rodapé do card.

## 4. Custo total da teimosinha (FR-006)

- **Decision**: em `JogoService.listarComResumo()`, calcular `custoTotal = premioService.custoAposta(loteria, jogo.getNumeros().size()).multiply(BigDecimal.valueOf(jogo.getQuantidadeConcursos()))`. Renomear o campo `JogoComResumo.custoAposta` para `custoTotal` (o nome antigo descrevia só o valor unitário, que é exatamente o que deixa de ser exibido). `PremioService.custoAposta(...)` continua retornando o valor unitário sem mudanças — ainda é a fonte usada por `PremioService.tabelaReferencia()` e pelo endpoint `GET /api/loterias/{loteria}/precos`.
- **Rationale**: `jogo.getQuantidadeConcursos()` já existe em `JogoMongoDTO` (não precisa de nenhum dado novo); manter a multiplicação em `JogoService` (não em `PremioService`) porque é `JogoService` quem tem acesso ao jogo específico — `PremioService` continua sendo só a tabela de preços por loteria/quantidade de dezenas, sem depender de um jogo concreto.
- **Alternatives considered**: adicionar um método `PremioService.custoTotal(Loteria, int quantidadeDezenas, int quantidadeConcursos)` — descartado por ser uma multiplicação trivial de um valor que `JogoService` já tem em mãos; criar o método só empurraria a mesma lógica para outro lugar sem ganho.

## 5. Ganho total da teimosinha (FR-007)

- **Decision**: `JogoService.listarComResumo()` já chama `conferirConcursos(jogo)` para montar o `resumo` (contagem de premiados/não premiados/pendentes) — essa mesma lista de `ConferenciaConcurso` já traz `premio` (um `PremioFaixa`) preenchido para cada concurso `PREMIADO` (efeito colateral de `conferirContra(...)`, que já chama `premioService.valorPremio(...)` hoje). O ganho total é somado a partir dessa mesma lista, sem nenhuma chamada adicional: soma `premio.getValor()` para os itens com `situacao == PREMIADO && premio.getStatus() == PremioFaixa.VALOR` (ignora `SEM_GANHADOR`/`INDISPONIVEL`, que não têm valor).
- **Rationale**: zero custo adicional de chamadas à API da Caixa — a varredura e o cálculo de prêmio por concurso já rodam hoje só para contar os badges; esta mudança só aproveita um dado que já estava sendo calculado e descartado. Também **elimina** a varredura duplicada que existia em `resultadosSorteios()` (mencionada como aceitável-mas-redundante em `CLAUDE.md`: "Rendering that page runs the conference twice") — com a tabela combinada removida (FR-012), essa segunda varredura deixa de ser necessária em qualquer lugar.
- **Alternatives considered**: persistir `ganhoTotal` no documento Mongo do jogo — o próprio pedido do usuário deixou essa decisão em aberto para depois; calcular sob demanda (decisão acima) é o caminho mais simples agora e não fecha a porta para persistir depois, se a varredura se tornar um problema de performance em uso real.

## 6. Painel de sorteios premiados por teimosinha (FR-009/FR-010, resolvido em Clarifications)

- **Decision**: nenhum endpoint novo. O painel único (`<div id="painel-premiados" hidden>`, inserido em `jogos.jsp` logo após a grade de cards) é populado em JS via `fetch('/api/jogos/' + id + '/conferencia')` (endpoint já existente, `JogoRestController.conferencia`), filtrando no cliente `conferencia.concursos` por `situacao === 'PREMIADO'` e usando `conferencia.jogo.numeros` (dezenas jogadas) + `concurso.dezenasAcertadas` para destacar acertos — mesma lógica visual que a tabela combinada removida já fazia, só que a partir da resposta de um único jogo em vez de agregada no servidor.
- **Rationale**: `GET /api/jogos/{id}/conferencia` já retorna tudo que o painel precisa (loteria, descrição e dezenas do jogo via `conferencia.jogo`; concurso, data, dezenas sorteadas/acertadas, acertos, prêmio via `conferencia.concursos`) — criar um endpoint novo só para filtrar por `PREMIADO` no servidor duplicaria uma rota já existente sem necessidade.
- **Estado do painel (JS)**: uma variável `jogoAbertoId` guarda o id do jogo cujo painel está aberto (ou `null`). Clique no badge de um jogo: se `jogoAbertoId` já é esse id → esconde o painel e zera a variável (fecha); caso contrário → busca a conferência, reconstrói o conteúdo do painel do zero (removendo o `<tbody>`/mensagem anterior e recriando — "apagar o elemento e sempre reconstruir", conforme o pedido original) e mostra o painel, atualizando `jogoAbertoId`.
- **Exclusão de um jogo com o painel aberto**: `excluir(id)` já faz `location.reload()` no sucesso — a página inteira recarrega, então o painel fecha naturalmente junto; nenhum tratamento especial é necessário para esse edge case.
- **Alternatives considered**: novo endpoint `GET /api/jogos/{id}/sorteios-premiados` retornando só os `PREMIADO` já filtrados no servidor — descartado por duplicar `GET /api/jogos/{id}/conferencia` quase byte a byte (a única diferença seria o filtro, trivial de fazer em JS); manter um painel por card (rejeitado na clarificação) — descartaria a leitura mais literal do pedido original e complicaria a grade de 4 colunas com painéis de altura variável intercalados entre cards de larguras fixas.

## 7. Remoção da tabela combinada "Sorteios premiados" (FR-012)

- **Decision**: remover de `jogos.jsp` os dois blocos `<c:if>` que hoje renderizam a tabela combinada (vazio e com dados) e a legenda de cores fixa acima dela; mover a legenda de cores para dentro do painel único (só aparece quando o painel está aberto). Em `PaginasController.jogos()`, remover `model.addAttribute("resultados", jogoService.resultadosSorteios())`. Em `JogoService`, remover o método `resultadosSorteios()` inteiro (único consumidor era esse atributo de model). Remover o arquivo `dto/ResultadoSorteio.java` (único consumidor era `resultadosSorteios()`).
- **Rationale**: confirmado por busca no código (`grep -rn "resultadosSorteios\|ResultadoSorteio"`) que não há nenhum outro consumidor (nenhum outro controller, service ou JSP usa esse método/DTO) — seguro remover sem deixar código morto, consistente com a postura já adotada nas features anteriores (`specs/002-ajustes-ui-jogos`, que removeu `PremioService` do `PaginasController` quando ficou sem uso).
- **Alternatives considered**: manter `resultadosSorteios()`/`ResultadoSorteio` no código "por via das dúvidas" — descartado por violar a prática já estabelecida no repo de não deixar código morto.

## 8. Badge "premiado" clicável sem afetar `jogo.jsp`

- **Decision**: nova classe modificadora `.badge.premiado.clicavel` em `estilo.css` (`cursor: pointer` + leve destaque no `:hover`), aplicada apenas ao badge dentro do card em `jogos.jsp`. O badge "premiado" em `jogo.jsp` (página de detalhe, linha por concurso) continua sem a classe `clicavel` e sem `onclick`, preservando o comportamento estático de hoje.
- **Rationale**: a classe base `.badge.premiado` já é compartilhada pelas duas páginas; adicionar `cursor: pointer` diretamente nela vazaria a affordance de clique para `jogo.jsp`, que está fora do escopo desta mudança (ver Assumptions do spec).
- **Alternatives considered**: `cursor: pointer` direto em `.badge.premiado` — descartado pelo motivo acima.
