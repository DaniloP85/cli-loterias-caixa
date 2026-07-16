# Contracts: Regras de Aposta e Premiação por Loteria

Todos os endpoints abaixo seguem o padrão já estabelecido em
`ConcursoRestController`/`JogoRestController` (JSON, `Loteria` via
`StringParaLoteriaConverter`, 404 via `ApiExceptionHandler`).

## Novo endpoint: tabela de preços de referência (US3 / FR-006)

```
GET /api/loterias/{loteria}/precos
```

**200 OK**

```json
[
  { "quantidadeDezenas": 6, "valor": 6.00 },
  { "quantidadeDezenas": 7, "valor": 42.00 }
]
```

Para Lotomania, retorna uma lista com um único item
(`quantidadeDezenas: 50, valor: 3.00`).

**400** se `{loteria}` não for uma das 4 loterias suportadas (mesmo
comportamento já existente via `StringParaLoteriaConverter`).

## Alteração: `GET /api/jogos` (FR-002 / US1)

Cada item de `JogoComResumo` ganha o campo `custoAposta`:

```json
{
  "jogo": { "loteria": "megasena", "numeros": [1,2,3,4,5,6], "...": "..." },
  "resumo": { "conferidos": 3, "premiados": 1, "naoPremiados": 2, "pendentes": 0 },
  "custoAposta": 6.00
}
```

Sempre presente, mesmo para jogos com todos os concursos `PENDENTE` (FR-002
explicitamente cobre esse caso).

## Alteração: `GET /api/jogos/{id}/conferencia` (FR-004 / FR-005)

Cada `ConferenciaConcurso` dentro de `ConferenciaJogo.concursos` ganha o
campo `premio` (nullable):

```json
{
  "concurso": 2870,
  "situacao": "PREMIADO",
  "acertos": 4,
  "premio": { "valor": 1357.52, "status": "VALOR" },
  "...": "..."
}
```

- `situacao == PREMIADO` e valor publicado → `premio.status = "VALOR"`,
  `premio.valor` preenchido.
- `situacao == PREMIADO` mas faixa acumulada/sem ganhador → `premio.status
  = "SEM_GANHADOR"`, `premio.valor = null`.
- `situacao == PREMIADO` mas busca sob demanda falhou (concurso legado,
  API indisponível) → `premio.status = "INDISPONIVEL"`, `premio.valor =
  null`.
- `situacao` = `NAO_PREMIADO` ou `PENDENTE` → `premio = null` (FR-005).

## Alteração: página `/jogos` (tabela "Sorteios premiados", FR-004)

Cada linha de `ResultadoSorteio` (já filtrada para `PREMIADO`, ver
`JogoService.resultadosSorteios()`) ganha o mesmo campo `premio` descrito
acima — nunca `null` nessa tabela porque só há linhas `PREMIADO` ali, mas
`status` pode ser `SEM_GANHADOR` ou `INDISPONIVEL`.

## Sem alteração de contrato

- `GET /api/loterias/{loteria}/export` (CSV) e
  `GET /api/loterias/{loteria}/export?formato=json` — FR-010 mantém o
  dataset de ML intocado.
- `POST /api/jogos`, `DELETE /api/jogos/{id}` — sem mudança de request/response.
- `GET /api/loterias/{loteria}/estatisticas`, `/concursos`, `/concursos/{numero}`
  — sem mudança (o novo campo `rateioPremios` aparece em
  `GET /api/loterias/{loteria}/concursos/{numero}` só porque é o mesmo
  `ConcursoMongoDTO` serializado — não é uma mudança de contrato
  intencional desta feature, é efeito colateral aceitável de adicionar o
  campo ao documento).
