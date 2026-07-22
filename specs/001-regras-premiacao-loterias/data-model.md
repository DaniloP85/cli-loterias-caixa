# Data Model: Regras de Aposta e Premiação por Loteria

## Entidades novas/alteradas

### TabelaPrecoAposta (estática, não persistida)

Dado de referência embutido em `enums/Loteria`, no mesmo espírito de
`minDezenas`/`maxDezenas`. Não é uma classe Mongo — é uma tabela em memória
por `Loteria`.

| Campo | Tipo | Notas |
|---|---|---|
| quantidadeDezenas | int | chave da tabela; para Lotomania só existe a entrada 50 |
| valor | BigDecimal | valor oficial da tabela de preços (`ideas/*.md` seção 2) |

Regras de validação: nenhuma em runtime além da já existente
(`JogoService.criar` já garante `minDezenas <= dezenas.size() <= maxDezenas`
antes de qualquer cálculo de custo — FR do edge case "fora do intervalo não
deve gerar erro" já é satisfeito por essa validação anterior).

View model para US3 (getters, renderizado em JSP):

```java
class PrecoAposta {
    int quantidadeDezenas;
    BigDecimal valor;
}
```

### RateioPremioMongoDTO (persistido, novo campo em `ConcursoMongoDTO`)

```java
class RateioPremioMongoDTO {
    int faixa;                 // ordem da Caixa (1..N) — NÃO usar como nº de acertos
    String descricaoFaixa;     // ex.: "6 acertos", "0 acertos" — fonte da vdd p/ casar com `acertos`
    int numeroDeGanhadores;
    BigDecimal valorPremio;
}
```

`ConcursoMongoDTO` ganha:

```java
@Field(name = "rateio_premios")
private List<RateioPremioMongoDTO> rateioPremios; // null/vazio = ainda não capturado (concurso legado)
```

Mapeado a partir do novo campo em `ConcursoDTO` (API da Caixa):

```java
@JsonProperty("listaRateioPremio")
private List<RateioPremioDTO> listaRateioPremio;

class RateioPremioDTO {
    @JsonProperty("faixa") int faixa;
    @JsonProperty("descricaoFaixa") String descricaoFaixa;
    @JsonProperty("numeroDeGanhadores") int numeroDeGanhadores;
    @JsonProperty("valorPremio") double valorPremio;
}
```

Isolado de `historial`/`FeaturesDTO` — ver research.md seção 4 (não afeta
`DatasetService`/`LinhaDataset`/export de ML, por FR-010).

### PremioFaixa (view/computado, não persistido)

Resultado de `PremioService.valorPremio(...)`, usado para popular
`ConferenciaConcurso`/`ResultadoSorteio`:

```java
class PremioFaixa {
    BigDecimal valor;        // null quando status != VALOR
    String status;           // "VALOR" | "SEM_GANHADOR" | "INDISPONIVEL"
}
```

- `VALOR`: `rateioPremios` (persistido ou recém-buscado) tem uma entrada com
  `descricaoFaixa` batendo `acertos` e `numeroDeGanhadores > 0` → `valor` =
  `valorPremio` daquela entrada.
- `SEM_GANHADOR`: entrada encontrada, mas `numeroDeGanhadores == 0` (faixa
  acumulada, sem ganhador naquele concurso) → `valor = null`.
- `INDISPONIVEL`: `rateioPremios` ausente e a busca sob demanda na API da
  Caixa falhou (timeout/erro) → `valor = null`.

### Alterações em DTOs existentes

- **`ConferenciaConcurso`** (`dto/ConferenciaConcurso.java`): novo campo
  `PremioFaixa premio` (só populado quando `situacao == PREMIADO`; `null`
  para `NAO_PREMIADO`/`PENDENTE`, conforme FR-005).
- **`ResultadoSorteio`** (`dto/ResultadoSorteio.java`): novo campo
  `PremioFaixa premio` (a tabela "Sorteios premiados" já filtra só
  `PREMIADO`, então sempre populado aqui).
- **`JogoComResumo`** (`dto/JogoComResumo.java`) e/ou `JogoMongoDTO`: novo
  campo computado `BigDecimal custoAposta` (não persistido — calculado a
  partir de `loteria` + `numeros.size()` toda vez que o jogo é carregado,
  já que é derivado, não um dado próprio do jogo).

## Fluxo de resolução de prêmio (para implementação)

1. `JogoService.conferirContra(resultado, numerosJogados, loteria)` calcula
   `acertos` (já existe) e `situacao` (já existe).
2. Se `situacao == PREMIADO`: chama
   `premioService.valorPremio(resultado, loteria, acertos)`:
   a. Se `resultado.getRateioPremios()` não vazio → casar por
      `descricaoFaixa` (research.md seção 2) → `PremioFaixa` (`VALOR` ou
      `SEM_GANHADOR`).
   b. Se vazio/nulo → buscar via `HttpService.recuperarConcurso(...)`
      (research.md seção 5):
      - sucesso → mapear `listaRateioPremio` → `RateioPremioMongoDTO`,
        `concursoRepository.save(resultado)` (cache), repetir passo (a).
      - falha → `PremioFaixa` com `status = INDISPONIVEL`.
3. `situacao != PREMIADO` → `premio = null` (nada a resolver).

## Sem mudança de schema para

- `JogoMongoDTO` (o custo é derivado, não precisa ser persistido).
- `FeaturesDTO` / dataset de ML (fora de escopo, FR-010).
