# Phase 1 Data Model: Separar "Meus jogos" em página de cadastro e página de conferência

Sem novas entidades de dados. Esta feature é puramente de roteamento/apresentação (`PaginasController` + JSP) e não introduz, altera nem remove nenhum DTO, documento Mongo ou modelo de domínio (FR-010).

## Impacto em modelos existentes (view models internos, não persistidos)

- **`PaginasController.ConfigLoteria`**: continua existindo sem mudança de forma, mas passa a ser produzido/consumido apenas pelo novo handler `GET /jogos/cadastro` (é o que alimenta o `<select>` de loteria e os `data-*` do volante). O handler `GET /jogos` (conferência) não precisa mais construir essa lista.
- **`JogoComResumo`** (`jogoService.listarComResumo()`): sem mudança de forma; passa a ser exposto apenas ao model de `GET /jogos` (conferência), que é onde a tabela de jogos cadastrados (loteria, dezenas, concursos, descrição, custo, resumo, excluir) é renderizada.
- **`ResultadoSorteio`** (`jogoService.resultadosSorteios()`): sem mudança de forma; permanece exposto apenas ao model de `GET /jogos` (conferência), onde a tabela "Sorteios premiados" é renderizada.
- **`ConferenciaJogo`** (`jogoService.conferir(id)`): sem mudança — continua exclusivo do handler `GET /jogos/{id}` (`jogo.jsp`), fora do escopo desta feature.

## Atributos de model por rota (após a divisão)

| Rota | View | Atributos de model |
|------|------|---------------------|
| `GET /jogos/cadastro` (nova) | `jogos-cadastro.jsp` | `loterias` (`List<ConfigLoteria>`), `abaAtiva="jogos-cadastro"` |
| `GET /jogos` (reaproveitada) | `jogos.jsp` (reduzida) | `jogos` (`List<JogoComResumo>`), `resultados` (`List<ResultadoSorteio>`), `abaAtiva="jogos-conferencia"` |
| `GET /jogos/{id}` (sem mudança de conteúdo) | `jogo.jsp` | `conferencia` (`ConferenciaJogo`), `abaAtiva="jogos-conferencia"` (era `"jogos"`) |

Nenhum atributo novo é introduzido; a mudança é apenas redistribuir os atributos já existentes entre as duas rotas e ajustar o valor de `abaAtiva` para refletir a nova nomenclatura de abas.
