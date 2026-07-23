# Contracts: endpoints de `/api/jogos` afetados

Nenhum endpoint novo é criado. Um endpoint ganha um campo novo na resposta; nenhum outro muda.

## `GET /api/jogos` — ganha um campo

Implementado por `JogoRestController.listar()` → `JogoService.listarComResumo()` → `List<JogoComResumo>`.

**Antes**:

```json
[
  {
    "jogo": { "...": "..." },
    "resumo": { "conferidos": 74, "premiados": 13, "naoPremiados": 61, "pendentes": 0 },
    "custoTotal": 255.50,
    "ganhoTotal": 187.20
  }
]
```

**Depois**:

```json
[
  {
    "jogo": { "...": "..." },
    "resumo": { "conferidos": 74, "premiados": 13, "naoPremiados": 61, "pendentes": 0 },
    "custoTotal": 255.50,
    "ganhoTotal": 187.20,
    "dezenasAcertadasUltimoConcurso": []
  }
]
```

- `dezenasAcertadasUltimoConcurso`: campo novo, `List<Integer>`. Traz as dezenas jogadas que coincidiram com o resultado do concurso mais recente já apurado dentro do intervalo da teimosinha. Vazio quando a teimosinha não tem mais concursos pendentes (`resumo.pendentes == 0`) ou quando nenhum concurso do intervalo foi apurado ainda. Ver `data-model.md` e `research.md` §1-2.
- `POST /api/jogos`, `DELETE /api/jogos/{id}`, `GET /api/jogos/{id}/conferencia` — sem mudanças.

### Exemplo com destaque presente

Teimosinha lotofácil de `3742` a `3763`, com o concurso `3742` já apurado e `21` concursos ainda pendentes:

```json
{
  "jogo": { "loteria": "lotofacil", "numeros": [1, 2, 3, "..."], "concursoInicial": 3742, "concursoFinal": 3763 },
  "resumo": { "conferidos": 1, "premiados": 0, "naoPremiados": 1, "pendentes": 21 },
  "custoTotal": 73.50,
  "ganhoTotal": 0,
  "dezenasAcertadasUltimoConcurso": [3, 18, 21]
}
```
