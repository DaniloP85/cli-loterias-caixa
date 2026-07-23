# Contract: `GET /api/jogos` e `GET /api/jogos/{id}/conferencia`

Ambos os endpoints já existem (`JogoRestController`, inalterado nesta feature — nenhum novo endpoint é criado, ver `research.md` §4). Esta feature altera apenas o **shape da resposta** de `GET /api/jogos` (novo campo em `JogoComResumo` + novo getter derivado em `JogoMongoDTO`, que também aparece em `GET /api/jogos/{id}/conferencia` porque os dois endpoints serializam o mesmo `JogoMongoDTO` aninhado).

## `GET /api/jogos`

Sem mudança de assinatura (`JogoRestController.listar()` → `List<JogoComResumo>`). Mudança no shape de cada item da lista:

```jsonc
[
  {
    "jogo": {
      "id": "...",
      "loteria": "megasena",
      "numeros": [18, 21, 23, 43, 55, 58],
      "concursoInicial": 3667,
      "quantidadeConcursos": 74,
      "descricao": "...",
      "criadoEm": "...",
      "concursoFinal": 3740,
      "teimosinha": true            // NOVO — quantidadeConcursos > 1
    },
    "resumo": { "conferidos": 74, "premiados": 3, "naoPremiados": 71, "pendentes": 0 },
    "custoTotal": 370.00,
    "ganhoTotal": 45.50,
    "dezenasAcertadasUltimoConcurso": [18, 21],  // agora populado mesmo com pendentes == 0 (FR-001)
    "concursoComparado": 3740                     // NOVO — null quando nenhum concurso do intervalo foi apurado ainda
  }
]
```

**Regras do novo shape**:
- `jogo.teimosinha`: `true` sse `jogo.quantidadeConcursos > 1` (equivalente a `jogo.concursoFinal > jogo.concursoInicial`).
- `concursoComparado`: número do concurso mais recente já apurado dentro de `[concursoInicial, concursoFinal]`; `null` se nenhum concurso do intervalo foi apurado ainda.
- `dezenasAcertadasUltimoConcurso`: dezenas jogadas que coincidiram com o concurso `concursoComparado`; lista vazia sse `concursoComparado == null` (não depende mais de `resumo.pendentes`).
- Invariante: `concursoComparado == null` ⇔ `dezenasAcertadasUltimoConcurso == []`.

## `GET /api/jogos/{id}/conferencia`

Sem mudança de assinatura nem de comportamento (`JogoRestController.conferencia(id)` → `ConferenciaJogo`). Passa a ser consumido também pelo JS de paginação de `jogos.jsp` (não só pelo painel "Sorteios premiados"), mas a resposta em si não muda:

```jsonc
{
  "jogo": { "...": "...", "teimosinha": true },  // mesmo JogoMongoDTO, ganha "teimosinha" pelo motivo acima
  "resumo": { "conferidos": 74, "premiados": 3, "naoPremiados": 71, "pendentes": 0 },
  "concursos": [
    { "concurso": 3667, "dataSorteio": "...", "dezenasSorteadas": [...], "dezenasAcertadas": [18], "acertos": 1, "situacao": "NAO_PREMIADO", "premio": null },
    // ... um item por concurso em [concursoInicial, concursoFinal], em ordem ascendente,
    // incluindo situacao == "PENDENTE" (dezenasAcertadas == [] nesse caso)
    { "concurso": 3740, "dataSorteio": "...", "dezenasSorteadas": [...], "dezenasAcertadas": [18, 21], "acertos": 2, "situacao": "NAO_PREMIADO", "premio": null }
  ]
}
```

**Uso pelo cliente para a paginação (FR-005 a FR-009)**: filtrar `concursos` por `situacao !== "PENDENTE"`, já vem ordenado ascendente por `concurso` — essa sublista é a sequência navegável de voltar/avançar, cada item já trazendo `dezenasAcertadas` prontas para reaplicar o destaque sem nenhuma outra chamada.
