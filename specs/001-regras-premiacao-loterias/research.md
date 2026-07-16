# Research: Regras de Aposta e Premiação por Loteria

## 1. Formato real da API da Caixa para valores de prêmio por faixa

**Decision**: Consumir o campo `listaRateioPremio` da resposta da API da Caixa
(mesmo endpoint já usado por `HttpServiceImpl`/`ImportacaoService`), com o
formato confirmado ao vivo em `.../api/megasena` (concurso 3031, 2026-07-14):

```json
"listaRateioPremio": [
  { "descricaoFaixa": "6 acertos", "faixa": 1, "numeroDeGanhadores": 0, "valorPremio": 0.0 },
  { "descricaoFaixa": "5 acertos", "faixa": 2, "numeroDeGanhadores": 24, "valorPremio": 63791.87 },
  { "descricaoFaixa": "4 acertos", "faixa": 3, "numeroDeGanhadores": 1859, "valorPremio": 1357.52 }
]
```

**Rationale**: `ConcursoDTO` hoje não mapeia esse campo (só existe
`valorTotalPremioFaixaUm`, que é só a faixa principal) — é preciso adicionar
o mapeamento para viabilizar FR-004/FR-007.

**Alternativas consideradas**: Recalcular o valor localmente a partir dos
percentuais documentados em `ideas/*.md` — rejeitada explicitamente por
FR-007 (a arrecadação/rateio real tem ajustes que os percentuais de
documentação não capturam com precisão suficiente; a Caixa já publica o
valor final).

## 2. Como casar `acertos` do jogo do usuário com a `faixa` da API

**Decision**: Casar por **`descricaoFaixa`**, extraindo o número de acertos
do texto (`"N acertos"` / `"0 acertos"` para a faixa especial da Lotomania),
em vez de assumir um índice fixo de `faixa`.

**Rationale**: O campo `faixa` (1, 2, 3...) é a ordem de importância da
Caixa, não o número de acertos, e essa ordem **muda por loteria**: na
Lotomania a faixa 1 = 20 acertos e a faixa 7 = 0 acertos (ver
`ideas/lotomania.md` seção 5.1), enquanto em Mega-Sena/Quina/Lotofácil a
faixa 1 é sempre a de mais acertos. `descricaoFaixa` já contém o número de
acertos como texto em todas as 4 loterias, então dá pra evitar manter uma
tabela de mapeamento faixa→acertos por loteria (o que a documentação de
`ideas/megasena.md` seção 7 já alerta ser ambíguo na fonte).

**Alternativas consideradas**: Tabela hardcoded faixa→acertos por loteria —
rejeitada por exigir manutenção extra e duplicar uma informação que já vem
no texto de `descricaoFaixa`.

## 3. "Faixa sem ganhador" vs. falha ao buscar

**Decision**: Dois estados distintos, ambos sem mostrar um valor numérico:
- **Sem ganhador na faixa** (`numeroDeGanhadores == 0` no item de
  `listaRateioPremio` casado por `descricaoFaixa`): a Caixa respondeu, mas
  não há prêmio pago nessa faixa naquele concurso (acumulado). É um dado
  válido, não uma falha.
- **Indisponível** (busca sob demanda falhou, concurso legado sem rateio
  persistido e API inacessível no momento): falha de infraestrutura,
  temporária.

**Rationale**: São causas diferentes e o texto exibido ao usuário deve
diferenciar (edge case do spec pede "não houve ganhador nesta faixa" só
para o primeiro caso; `FR-007b` pede "indisponível" só para o segundo).

## 4. Onde persistir o rateio por faixa

**Decision**: Novo campo de nível superior em `ConcursoMongoDTO` (ex.:
`rateioPremios: List<RateioPremioMongoDTO>`, campo Mongo `rateio_premios`),
**separado** de `historial` (`FeaturesDTO`).

**Rationale**: `historial` alimenta exclusivamente o dataset de ML
(`DatasetService`/`LinhaDataset`) — por FR-010, esse dataset não deve mudar.
Manter o rateio fora de `historial` evita qualquer acoplamento acidental com
o export de ML e deixa claro que são dados de domínios diferentes
(features estatísticas vs. informação financeira de premiação).

**Alternativas consideradas**: Colocar dentro de `FeaturesDTO` — rejeitada,
misturaria conceitos e arriscaria vazar para o CSV/JSON de ML sem intenção.

## 5. Busca sob demanda + cache para concursos legados (FR-007a/FR-007b)

**Decision**: Reusar `HttpService.recuperarConcurso(url)` (já usado por
`ImportacaoService`, já tem `@Retryable` para 5xx) com a mesma URL
`urlBase + loteria.nome() + "/" + concurso`. Quando um `ConcursoMongoDTO` for
lido para conferência/exibição e `rateioPremios` estiver vazio/nulo: buscar,
mapear para `RateioPremioMongoDTO`, salvar de volta no documento existente
via `ConcursoRepository.save(...)`, e então usar o valor. Se a busca falhar
(exception), logar e devolver o marcador "indisponível" sem interromper a
listagem (mesmo padrão de `ImportacaoService`/SSE: falha de rede não pode
quebrar a exibição).

**Rationale**: Reaproveita infraestrutura de retry/HTTP já testada em
produção pelo processo de importação; evita duplicar client HTTP. O cache é
uma escrita idempotente no mesmo documento (`concurso`+`loteria` já são
únicos na prática pelo fluxo de importação), então não há necessidade de
lock adicional.

**Alternativas consideradas**: Endpoint de migração/reimportação em lote —
rejeitada pela clarificação (FR-007a explicitamente não exige reimportação).

## 6. Tabela de preço da aposta (custo)

**Decision**: Dado estático embutido em `enums/Loteria` (mesmo padrão hoje
usado para `minDezenas`/`maxDezenas`), como uma função/tabela
`quantidadeDezenas -> valor (BigDecimal)`. Lotomania tem uma única entrada
fixa (50 dezenas → R$ 3,00).

**Rationale**: Já é a orientação explícita da seção *Assumptions* do spec
("mesmo padrão hoje usado para os limites de dezenas em `enums/Loteria`").
Os valores vêm diretamente das tabelas oficiais em `ideas/*.md` seção 2.

**Alternativas consideradas**: Coleção Mongo ou arquivo de properties
externo — rejeitadas por complexidade desnecessária para um dado estático
de baixa frequência de mudança (mesma razão já usada para os limites de
dezenas existentes).

## 7. Tipo de dado para valores monetários

**Decision**: `java.math.BigDecimal` para todo valor em reais novo (custo e
prêmio), tanto em DTOs de domínio quanto nos novos campos Mongo.

**Rationale**: Evita erro de arredondamento de ponto flutuante em valores
financeiros exibidos ao usuário (diferente de `FeaturesDTO`, que usa
`double`/`Long` propositalmente para estatística, não dinheiro).

## 8. Onde expor a nova lógica de custo/prêmio no código

**Decision**: Novo `service/PremioService` (nome provisório), com:
- `custoAposta(Loteria, int quantidadeDezenas): BigDecimal`
- `tabelaReferencia(Loteria): List<PrecoAposta>` (US3)
- `valorPremio(ConcursoMongoDTO, Loteria, int acertos): PremioFaixa` (busca
  sob demanda + cache quando necessário, ver seção 5)

`JogoService.conferirContra(...)` passa a chamar `PremioService.valorPremio`
quando `situacao == PREMIADO`, e `JogoService.listarComResumo()`/
`resultadosSorteios()` passam a incluir `custoAposta`.

**Rationale**: Mantém `JogoService` focado em CRUD/conferência de jogos;
`PremioService` isola a nova regra de negócio (preço + rateio), com suas
próprias dependências (`HttpService`, `caixa.api.base-url`) sem inchar o
construtor de `JogoService`.

## 9. Constitution / testes

O arquivo `.specify/memory/constitution.md` está com os placeholders do
template, sem princípios definidos para este projeto — não há gates a
verificar. O `CLAUDE.md` do repositório confirma: "There are no automated
tests in this repo currently." A validação desta funcionalidade será manual,
via `quickstart.md`.
