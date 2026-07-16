# Implementation Plan: Regras de Aposta e Premiação por Loteria

**Branch**: `001-regras-premiacao-loterias` | **Date**: 2026-07-15 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/001-regras-premiacao-loterias/spec.md`

**Note**: This template is filled in by the `/speckit-plan` command; its definition describes the execution workflow.

## Summary

Exibir, em "Meus jogos", o custo de cada aposta cadastrada (com base na
quantidade de dezenas marcadas) e o valor em reais do prêmio de cada jogo
classificado como PREMIADO — usando as tabelas de preço documentadas em
`ideas/*.md` para o custo e o rateio oficial já publicado pela Caixa
(`listaRateioPremio`, campo hoje não mapeado) para o prêmio. Concursos
importados antes desta feature não têm o rateio persistido: nesses casos o
valor é buscado sob demanda na API da Caixa (reaproveitando `HttpService`) e
cacheado no próprio documento após o primeiro sucesso, sem exigir
reimportação. Uma tabela de referência de preços por loteria (US3) é
adicionada como seção dentro da tela de cadastro de jogo (`jogos.jsp`). O
dataset de ML (`DatasetService`) não é alterado.

## Technical Context

**Language/Version**: Java 17

**Primary Dependencies**: Spring Boot 3.5.6 (`spring-boot-starter-web`,
`spring-boot-starter-data-mongodb`), Spring Retry (`@Retryable` em
`HttpServiceImpl`, já reaproveitado pela busca sob demanda desta feature),
Tomcat embedded + `tomcat-embed-jasper`/Jakarta JSTL (JSP), Lombok

**Storage**: MongoDB — coleção `resultados` (`ConcursoMongoDTO`, ganha o
campo `rateio_premios`) e `jogos` (`JogoMongoDTO`, sem mudança de schema —
custo é derivado, não persistido)

**Testing**: Nenhum framework de teste automatizado configurado neste
repositório hoje (confirmado em `CLAUDE.md`); validação desta feature é
manual, via `quickstart.md`

**Target Platform**: Linux server (WAR executável com Tomcat embarcado,
via Docker ou `java -jar`), acessado por navegador (JSP)

**Project Type**: web — monólito único (REST + JSP), não há split
frontend/backend

**Performance Goals**: Não aplicável — ferramenta de uso pessoal, baixo
volume de requisições; a única preocupação de performance é não travar a
exibição da página "Meus jogos" esperando uma chamada de rede lenta à API
da Caixa para concursos legados (mitigado por cache-on-first-success,
ver research.md §5)

**Constraints**: JSP EL (Tomcat 10.1/Jakarta) não resolve records de forma
confiável — todo novo modelo de view renderizado em JSP (`PrecoAposta`,
`PremioFaixa`, campos novos em `JogoComResumo`/`ConferenciaConcurso`/
`ResultadoSorteio`) deve ser classe com getters, seguindo o padrão já usado
em `ConferenciaConcurso`/`ResumoJogo`/`PaginasController.CardLoteria`

**Scale/Scope**: 4 loterias suportadas, dezenas de milhares de concursos
histórico total, uso por um único usuário — sem requisitos de
escalabilidade horizontal

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

`.specify/memory/constitution.md` está com os placeholders originais do
template (`[PRINCIPLE_1_NAME]`, etc.) — nenhum princípio foi ratificado
para este projeto. Não há gates formais a verificar; esta seção é tratada
como **PASS por ausência de constituição**, seguindo em vez disso as
convenções já estabelecidas no código existente (documentadas em
`CLAUDE.md` e reproduzidas nas restrições de `Technical Context` acima).

## Project Structure

### Documentation (this feature)

```text
specs/001-regras-premiacao-loterias/
├── plan.md              # This file (/speckit-plan command output)
├── research.md          # Phase 0 output (/speckit-plan command)
├── data-model.md        # Phase 1 output (/speckit-plan command)
├── quickstart.md        # Phase 1 output (/speckit-plan command)
├── contracts/
│   └── api.md           # Phase 1 output (/speckit-plan command)
└── tasks.md             # Phase 2 output (/speckit-tasks command - NOT created by /speckit-plan)
```

### Source Code (repository root)

Monólito Spring Boot único já existente — esta feature estende arquivos
existentes e adiciona poucos novos, sem criar novos módulos/projetos:

```text
src/main/java/br/com/dpsnqmk/
├── enums/
│   └── Loteria.java                 # + tabela de preço por quantidade de dezenas (custoAposta)
├── dto/
│   ├── ConcursoDTO.java             # + listaRateioPremio / RateioPremioDTO (API da Caixa)
│   ├── ConcursoMongoDTO.java        # + rateioPremios (List<RateioPremioMongoDTO>)
│   ├── RateioPremioMongoDTO.java    # novo
│   ├── PremioFaixa.java             # novo — view model (getters, JSP-safe)
│   ├── PrecoAposta.java             # novo — view model da tabela de referência (US3)
│   ├── ConferenciaConcurso.java     # + campo premio (PremioFaixa)
│   ├── ResultadoSorteio.java        # + campo premio (PremioFaixa)
│   └── JogoComResumo.java           # + campo custoAposta (BigDecimal)
├── service/
│   ├── PremioService.java           # novo — custo, tabela de referência, resolução de prêmio
│   ├── JogoService.java             # passa a chamar PremioService em conferirContra/listarComResumo
│   └── ImportacaoService.java       # converter() passa a mapear listaRateioPremio também
├── controller/
│   ├── api/ConcursoRestController.java  # + GET /api/loterias/{loteria}/precos
│   └── web/PaginasController.java       # ConfigLoteria (ou novo model) carrega tabela de preços p/ jogos.jsp
└── repository/
    └── ConcursoRepository.java      # sem novo método (save() já existe, reutilizado p/ cache)

src/main/webapp/WEB-INF/jsp/
└── jogos.jsp                        # + seção de tabela de referência de preços; + custo/prêmio nas tabelas existentes
```

Não há diretório `tests/` no projeto hoje (ver `Technical Context` —
nenhum framework de teste configurado); esta feature não introduz um.

**Structure Decision**: Single project (monólito Spring Boot já existente).
Não se aplica a Option 2/3 do template (não há split frontend/backend nem
mobile) — tudo roda no mesmo WAR, REST + JSP, como o restante do sistema.

## Complexity Tracking

*Não se aplica — o Constitution Check não apontou violações (não há
constituição ratificada para este projeto).*

## Post-Design Constitution Check

*Re-avaliação após Phase 1 (data-model.md, contracts/, quickstart.md).*

Sem mudanças de conclusão: nenhum princípio ratificado para verificar. O
design em `data-model.md`/`contracts/api.md` mantém os padrões já
existentes no repositório (view models com getters para JSP, DTOs Mongo
com `@Field` explícito, endpoints REST sob `/api/loterias/{loteria}/...`,
retry reaproveitado de `HttpServiceImpl`) — nada introduzido foge do estilo
arquitetural atual. **PASS**.
