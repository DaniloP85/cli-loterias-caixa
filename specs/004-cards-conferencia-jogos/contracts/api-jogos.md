# Contracts: endpoints de `/api/jogos` afetados

Nenhum endpoint novo é criado. Um endpoint muda de forma de resposta; outro ganha um novo consumidor sem mudar de forma.

## `GET /api/jogos` — muda de forma

Implementado por `JogoRestController.listar()` → `JogoService.listarComResumo()` → `List<JogoComResumo>`.

**Antes**:

```json
[
  {
    "jogo": { "...": "..." },
    "resumo": { "conferidos": 74, "premiados": 13, "naoPremiados": 61, "pendentes": 0 },
    "custoAposta": 3.50
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
    "ganhoTotal": 187.20
  }
]
```

- `custoAposta` (valor de uma aposta) é renomeado para `custoTotal` (valor da teimosinha inteira — quantidade de concursos × valor unitário). Ver `data-model.md` e `research.md` §4.
- `ganhoTotal` é um campo novo: soma dos prêmios recebidos pela teimosinha (`0` se nenhum concurso premiado ainda). Ver `research.md` §5.
- `POST /api/jogos`, `DELETE /api/jogos/{id}` — sem mudanças.

## `GET /api/jogos/{id}/conferencia` — mesma forma, novo consumidor

Implementado por `JogoRestController.conferencia(id)` → `JogoService.conferir(id)` → `ConferenciaJogo`. **Nenhuma mudança de forma.** Passa a ser chamado também pelo JavaScript de `jogos.jsp` (painel de sorteios premiados sob demanda, ver `research.md` §6), além do uso já existente pela página de detalhe `/jogos/{id}`.

Forma da resposta (sem mudança), para referência do novo consumidor em JS:

```json
{
  "jogo": { "loteria": "lotofacil", "numeros": [1, 2, 3, "..."], "concursoInicial": 3667, "concursoFinal": 3740, "descricao": "..." },
  "resumo": { "conferidos": 74, "premiados": 13, "naoPremiados": 61, "pendentes": 0 },
  "concursos": [
    {
      "concurso": 3671,
      "dataSorteio": "2026-01-10T00:00:00.000+00:00",
      "dezenasSorteadas": [1, 2, 3, "..."],
      "dezenasAcertadas": [1, 2, 3],
      "acertos": 15,
      "situacao": "PREMIADO",
      "premio": { "valor": 25.50, "status": "VALOR" }
    }
  ]
}
```

O JS do painel filtra `concursos` por `situacao === "PREMIADO"` no cliente (ver `research.md` §6) — nenhum filtro novo do lado do servidor é necessário.
