---
name: "github-board-status"
description: "Move a issue de uma tarefa entre as colunas do board 'Acompanhamento' (#8) no GitHub Projects. Invocar automaticamente ao iniciar (in-progress) e ao concluir (in-review) a implementação de uma tarefa vinculada a uma issue."
argument-hint: "<numero-da-issue> <backlog|in-progress|in-review|done>"
user-invocable: true
disable-model-invocation: false
---

## User Input

```text
$ARGUMENTS
```

`$ARGUMENTS` deve conter `<numero-da-issue> <coluna>`, onde `<coluna>` é uma das strings `backlog`, `in-progress`, `in-review` ou `done` (aceitar variações de maiúsculas/hífen).

## Contexto do board

- Repo: `DaniloP85/cli-loterias-caixa`
- Board: "Acompanhamento", project number `8`, owner `DaniloP85`
- Project ID: `PVT_kwHOAPGjU84Bd1DE`
- Campo Status ID: `PVTSSF_lAHOAPGjU84Bd1DEzhYTuUc`
- Opções do campo Status:
  - `backlog` → `f75ad846`
  - `in-progress` → `47fc9ee4`
  - `in-review` → `df73e18b`
  - `done` → `98236657`

Se algum destes IDs falhar (comando retorna "not found" ou similar), os IDs podem ter mudado — redescubra antes de tentar de novo:

```bash
gh project view 8 --owner DaniloP85 --format json --jq .id
gh project field-list 8 --owner DaniloP85 --format json
```

## Passos

Use sempre o `--jq` embutido do próprio `gh` (não depende do binário `jq` externo, que pode não estar instalado no ambiente).

1. Localizar o item do board correspondente à issue:

```bash
gh project item-list 8 --owner DaniloP85 --format json --limit 200 \
  --jq ".items[] | select(.content.number == <NUMERO_DA_ISSUE>) | .id"
```

2. Se não retornar nada, a issue ainda não foi adicionada ao board — adicionar e capturar o item-id:

```bash
gh project item-add 8 --owner DaniloP85 \
  --url https://github.com/DaniloP85/cli-loterias-caixa/issues/<NUMERO_DA_ISSUE> \
  --format json --jq .id
```

3. Com o item-id em mãos, trocar o Status para a opção correspondente:

```bash
gh project item-edit --id <ITEM_ID> \
  --project-id PVT_kwHOAPGjU84Bd1DE \
  --field-id PVTSSF_lAHOAPGjU84Bd1DEzhYTuUc \
  --single-select-option-id <OPTION_ID>
```

4. Não é necessário narrar cada chamada ao usuário — só reportar se o comando falhar (nesse caso, investigar e não deixar a issue num estado incoerente).

## Quando invocar automaticamente durante implementação

- **Início de tarefa**: assim que começar a implementar uma tarefa (`T0xx` de um `tasks.md`, já convertida em issue via `speckit-taskstoissues`), invocar com `in-progress`.
- **Fim de tarefa**: assim que essa tarefa estiver implementada e validada (antes de seguir para a próxima), invocar com `in-review`.
- **Não** mover para `done` a partir daqui — isso acontece quando o PR correspondente é mergeado ou a issue é fechada manualmente (fluxo já coberto ao fechar PRs).
