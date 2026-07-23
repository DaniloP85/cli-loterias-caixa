# Phase 1 Data Model: Destaque persistente e paginação de concursos em cards de teimosinha

Nenhuma entidade persistida no MongoDB é criada, alterada ou removida. Todas as mudanças são: um getter derivado novo, um campo novo em um DTO de apresentação (view model), e o ajuste do cálculo que já alimenta esse DTO — tudo derivado na leitura a partir de dados já computados por `JogoService.conferirConcursos(...)`/`conferir(...)`.

## `JogoMongoDTO` (`dto/JogoMongoDTO.java`)

| Campo/Método | Antes | Depois | Notas |
|---|---|---|---|
| `getConcursoFinal()` | `concursoInicial + quantidadeConcursos - 1` | *(sem mudança)* | |
| `isTeimosinha()` | *(não existia)* | `quantidadeConcursos > 1` | Getter derivado novo (FR-004). Serializado por Jackson como `"teimosinha"` em qualquer JSON que aninhe `JogoMongoDTO` (`GET /api/jogos` → `item.jogo.teimosinha`; `GET /api/jogos/{id}/conferencia` → `conferencia.jogo.teimosinha`). Consumido também pelo JSP via EL (`${item.jogo.teimosinha}`). |

## `JogoComResumo` (`dto/JogoComResumo.java`)

View model já existente, retornado por `JogoService.listarComResumo()` e usado tanto por `PaginasController.jogos()` (renderização em `jogos.jsp`) quanto por `JogoRestController.listar()` (`GET /api/jogos`).

| Campo | Antes | Depois | Notas |
|---|---|---|---|
| `jogo` | `JogoMongoDTO` | *(sem mudança, mas agora expõe `teimosinha`)* | |
| `resumo` | `ResumoJogo` | *(sem mudança)* | `resumo.conferidos` (já existente = `premiados + naoPremiados`) passa a também controlar o estado inicial do botão "voltar" (ver research.md §5) |
| `custoTotal` | `BigDecimal` | *(sem mudança)* | |
| `ganhoTotal` | `BigDecimal` | *(sem mudança)* | |
| `dezenasAcertadasUltimoConcurso` | `List<Integer>` — vazia quando `resumo.pendentes == 0` ou nenhum concurso apurado | `List<Integer>` — vazia **apenas** quando nenhum concurso do intervalo foi apurado ainda; o gate de `pendentes == 0` é removido (FR-001) | Continua vindo do mesmo concurso identificado pela varredura de trás para frente |
| `concursoComparado` | *(não existia)* | `Integer` — número do concurso ao qual `dezenasAcertadasUltimoConcurso` se refere; `null` quando nenhum concurso do intervalo foi apurado ainda | Campo novo (FR-003/FR-012). Vem do mesmo resultado da varredura que já alimenta `dezenasAcertadasUltimoConcurso` — ver `JogoService` abaixo |

## `JogoService`

- Método privado renomeado de `dezenasAcertadasUltimoConcurso(List<ConferenciaConcurso>, ResumoJogo)` para algo como `ultimoConcursoApurado(List<ConferenciaConcurso> concursos)`, que:
  - Não recebe mais `ResumoJogo` (o gate de pendentes é removido).
  - Percorre `concursos` de trás para frente e retorna o primeiro `ConferenciaConcurso` cuja `situacao` não seja `PENDENTE`, ou `null` se nenhum for encontrado.
- `listarComResumo()`: chama esse método uma vez por jogo e deriva os dois campos de `JogoComResumo`:
  ```java
  ConferenciaConcurso ultimo = ultimoConcursoApurado(concursos);
  List<Integer> dezenasAcertadas = ultimo == null ? List.of() : ultimo.getDezenasAcertadas();
  Integer concursoComparado = ultimo == null ? null : ultimo.getConcurso();
  ```
- Nenhuma outra função de `JogoService` muda — `conferirConcursos(...)`, `conferirContra(...)`, `resumo(...)`, `ganhoTotal(...)`, `conferir(id)`, `conferirAvulso(...)` permanecem exatamente como estão.

## `ConferenciaConcurso` (`dto/ConferenciaConcurso.java`)

Sem mudanças — já expõe `concurso`, `situacao` e `dezenasAcertadas`, os três campos que a paginação client-side consome a partir de `GET /api/jogos/{id}/conferencia`.

## `jogos.jsp` — rótulo e controles de paginação (estado inicial, SSR)

| Condição (por card) | Renderização inicial |
|---|---|
| `item.jogo.teimosinha == false` e `item.concursoComparado != null` | Só o rótulo: "comparação com o concurso `${item.concursoComparado}`" — sem controles |
| `item.jogo.teimosinha == false` e `item.concursoComparado == null` | Nada (nem rótulo, nem controles) — FR-002 |
| `item.jogo.teimosinha == true` e `item.concursoComparado == null` | Rótulo com texto de espera ("aguardando apuração") + controles voltar/avançar, ambos `disabled` — FR-012 |
| `item.jogo.teimosinha == true` e `item.concursoComparado != null` | Rótulo "comparação com o concurso `${item.concursoComparado}`" + controle avançar sempre `disabled` (já é o mais recente) + controle voltar `disabled` apenas se `resumo.conferidos <= 1` |

## Estado client-side (JS) — "Seleção de concurso em exibição"

Estado momentâneo por card, não persistido (FR-010), mantido em memória no `jogos.jsp`:

```js
// por jogoId, populado sob demanda no primeiro clique em voltar/avançar
{
  apurados: [ /* ConferenciaConcurso[] com situacao !== 'PENDENTE', ordenado por concurso asc */ ],
  indice: number // índice do concurso atualmente exibido dentro de `apurados`
}
```

- Populado via fetch a `GET /api/jogos/{id}/conferencia` (mesmo endpoint já usado por `alternarPremiados`), filtrando `situacao !== 'PENDENTE'` e ordenando por `concurso` ascendente.
- `indice` inicializado em `apurados.length - 1` (o mais recente) logo após o fetch, para casar com o estado inicial já renderizado no servidor.
- Voltar: `indice = Math.max(0, indice - 1)`. Avançar: `indice = Math.min(apurados.length - 1, indice + 1)`.
- A cada mudança de `indice`, re-renderiza: as dezenas grandes do card (destaque = `apurados[indice].dezenasAcertadas`), o rótulo (`apurados[indice].concurso`), e o `disabled` dos dois botões (voltar desabilitado quando `indice === 0`; avançar quando `indice === apurados.length - 1`).
- Cada card mantém sua própria entrada nesse estado — nenhuma leitura/escrita cruza jogos diferentes (FR-011).
