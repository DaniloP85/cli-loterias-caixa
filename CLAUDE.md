# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

A Java/Spring Boot **web monolith** (REST API + JSP pages) that imports lottery ("loterias") results from the Caixa Econômica Federal public API (`https://servicebus2.caixa.gov.br/portaldeloterias/api/`), computes statistical features per drawing (soma, média, desvio padrão, pares/ímpares, altos/baixos, log-produto), persists them into MongoDB, and exposes the data through JSON endpoints, JSP pages, and a flat CSV/JSON dataset export intended for machine-learning experiments. It evolved from an earlier one-shot picocli CLI (hence the repo name `cli-loterias-caixa`).

## Build & run

```bash
mvn clean package                    # builds target/loterias-caixa.war (skip tests: -DskipTests)
java -jar target/loterias-caixa.war  # executable WAR, serves http://localhost:8080
```

**Packaging is WAR, not JAR** — Spring Boot only supports JSP with war packaging. The WAR is still executable via embedded Tomcat; `tomcat-embed-jasper` and the Jakarta JSTL deps in `pom.xml` are what make JSP work. Do not convert back to jar packaging while JSP views exist.

Docker (bundles MongoDB):

```bash
docker-compose up -d --build         # app on :8080 + mongodb
docker-compose logs -f loterias-web
```

There are no automated tests in this repo currently.

## Architecture

Single Spring Boot app (`MainApplication`, extends `SpringBootServletInitializer`, has `@EnableRetry` — required for the `@Retryable` in `HttpServiceImpl` to actually work).

- `service/ImportacaoService` — background import. `iniciar(loteria, completo)` guards against concurrent runs per lottery and submits the work to an internal `ExecutorService`. The loop discovers the latest concurso number from the API (request without a concurso number), then fetches each concurso, computes features and saves via the repository. **Resume logic**: starts from the highest concurso already in Mongo (`completo=true` deletes the lottery's documents and reimports from 1). Progress is tracked per lottery in `StatusImportacao` objects held in a `ConcurrentHashMap`, exposed by the status endpoint. **SSE**: `assinar(loteria)` returns an `SseEmitter` (registered per lottery in a `CopyOnWriteArrayList`) that immediately receives the current status; the import loop publishes a `status` event after *every* insert, and completion/error sends a final event and completes the emitters. Dead emitters are dropped silently — SSE failures must never break an import.
- `service/JogoService` — "Meus jogos" (teimosinha) feature: `criar` validates dezenas against the `Loteria` range, `conferir` walks the concurso interval `[concursoInicial, concursoFinal]`, intersects the played numbers with each stored drawing and classifies PREMIADO/NAO_PREMIADO (via `Loteria.premiado(acertos)` — min prize tier per lottery; lotomania also wins with 0 hits) or PENDENTE when the concurso isn't in the base yet. `conferirAvulso(loteria, concurso, dezenas)` is the one-off check behind `GET .../concursos/{numero}/conferencia?dezenas=...` (mirrors the user's own `$setIntersection` Mongo query). `resultadosSorteios()` flattens jogo × drawn concurso into `ResultadoSorteio` rows for the "Sorteios premiados" table on `/jogos` — **only PREMIADO rows** (financial return; below-tier hits are omitted by design). Rendering that page runs the conference twice (resumo + rows) — fine for personal use. `listarComResumo()` and `conferirContra(...)` also call `PremioService` for `custoAposta`/`valorPremio` (see below).
- `service/PremioService` — apostado × ganho por jogo: `custoAposta(loteria, quantidadeDezenas)` reads the static price table on `Loteria`; `tabelaReferencia(loteria)` lists it in full for the `/jogos` reference section; `valorPremio(concurso, loteria, acertos)` resolves the prize value of a PREMIADO game by matching `RateioPremioMongoDTO.descricaoFaixa` text against the hit count (not the Caixa `faixa` index, which orders faixas differently per lottery — e.g. Lotomania's faixa 1 is 20 acertos, faixa 7 is 0 acertos). When a concurso predates this feature and has no `rateioPremios` persisted, it fetches the concurso on demand via `HttpService` and caches the result back onto the document (`ConcursoRepository.save`) — never throws, degrades to an "indisponível" `PremioFaixa` on failure (same posture as the SSE emitters in `ImportacaoService`).
- `service/EstatisticaService` — MongoTemplate aggregations for the dashboard: per-dezena frequency (`$unwind` + `$group`) and averages of the stored features. Uses raw Mongo field names (`numeros_sorteados`, `historial.desvio_padrao`) because the aggregations are untyped.
- `service/DatasetService` — builds the flat `LinhaDataset` rows and writes CSV (columns `concurso,data,n1..nK,soma,...`; K = max drawn-numbers count found). Reads only `historial`/`FeaturesDTO` — deliberately untouched by the `rateioPremios` field added for premiação, so the ML export never gains bet-cost/prize columns.
- `service/impl/HttpServiceImpl` — `RestTemplate` wrapper for the Caixa API, `@Retryable` (5 attempts, exponential backoff) on 5xx. Reused by `PremioService` for the on-demand rateio fetch described above.
- `controller/api/ConcursoRestController` — `/api/loterias/{loteria}/...`: importacao (POST, 202/409), importacao/status, importacao/eventos (SSE), concursos (paged), concursos/{numero}, estatisticas, precos (bet-cost reference table per `PremioService.tabelaReferencia`), export (two mappings: default CSV as `byte[]`, `params = "formato=json"` for JSON — do not merge them back into one `ResponseEntity<?>` method, the declared generic type is what makes Spring pick the right converter).
- `controller/api/JogoRestController` — `/api/jogos`: POST (201), GET list with resumo, GET `{id}/conferencia`, DELETE (204).
- `controller/web/PaginasController` — JSP pages in three tabs (`abaAtiva` model attribute drives nav highlight): *manutencao* (`/` cards with SSE progress bar + Atualizar/Reconstruir buttons, `/loterias/{loteria}` paged table, `.../concursos/{numero}` detail, `.../dashboard` heat map of dezena frequency built in plain JS from the estatisticas endpoint), *jogos* (`/jogos` list + clickable volante form + "Sorteios premiados" table with hit dezenas highlighted; `/jogos/{id}` conferência still exists but is no longer linked from the list), *ml* (`/ml` placeholder for a future AWS integration). Home cards show "base atualizada até o concurso X — sorteio de dd/MM/yyyy" derived from Mongo (`findTopByLoteriaOrderByConcursoDesc`) because `StatusImportacao` is in-memory and resets on restart. Registering a jogo for a future concurso is allowed by design (Caixa accepts bets for the next draw until 17h) — it shows as PENDENTE until imported.
- `controller/StringParaLoteriaConverter` — binds the `{loteria}` path variable to the `Loteria` enum; invalid names become 400 via `ApiExceptionHandler` (handles `MethodArgumentTypeMismatchException` and `ConcursoNaoEncontradoException` → 404).
- `repository/ConcursoRepository` — Spring Data Mongo repository over `ConcursoMongoDTO` (`@Document(collection = "resultados")`). The interval lookup is an explicit `@Query` (`findConcursosNoIntervalo`) — Spring Data Mongo derived queries cannot combine two conditions on the same field (throws `InvalidMongoDbApiUsageException` at runtime), and `Between` is exclusive anyway.
- `repository/JogoRepository` — repository over `JogoMongoDTO` (`@Document(collection = "jogos")`).
- `dto/` — `ConcursoDTO` (raw Caixa API shape, now including `listaRateioPremio`/`RateioPremioDTO`), `ConcursoMongoDTO` + `FeaturesDTO` (persisted shape) + `RateioPremioMongoDTO` (persisted prize-per-faixa, field `rateio_premios` — kept separate from `historial`, see `DatasetService` note above), `JogoMongoDTO` (persisted jogo), `EstatisticasDTO`, `LinhaDataset` (export row), `ConferenciaJogo`/`ConferenciaConcurso`/`ResumoJogo`/`JogoComResumo` (conferência view models — getter classes on purpose, they render in JSP; `ConferenciaConcurso`/`ResultadoSorteio` carry a `PremioFaixa` — `null` unless PREMIADO — and `JogoComResumo` carries `custoAposta`), `PremioFaixa` (`VALOR`/`SEM_GANHADOR`/`INDISPONIVEL`), `PrecoAposta` (row of the `/jogos` reference table).
- `utility/MyMath`, `records/AltosBaixos`, `records/ParesImparesFeature` — pure feature computation.
- `enums/Loteria` — per-lottery `(min, max)` number range, `minAcertosPremio` (lowest prize tier), `minDezenas`/`maxDezenas` (how many numbers one bet may mark: megasena 6–20, lotofacil 15–20, quina 5–15, lotomania exactly 50 — official Caixa rules, enforced in `JogoService.criar` and mirrored in the clickable volante on `jogos.jsp`) and `precos` (bet-cost table by dezenas count, official Caixa pricing — same "hardcoded like the dezenas limits" treatment, read by `PremioService`); `nome()` is the canonical lowercase name used both in the Caixa API URL path and in the Mongo `loteria` field. **Only MEGASENA, LOTOFACIL, QUINA, LOTOMANIA are supported.** The Caixa API for other games (e.g. `federal`) returns a different JSON shape (prize tickets, not drawn numbers), so adding one is not just an enum entry.

### JSP views

`src/main/webapp/WEB-INF/jsp/` — `home.jsp`, `concursos.jsp`, `concurso.jsp`, `dashboard.jsp` + shared `comum/cabecalho.jspf`/`rodape.jspf`. Taglibs are the Jakarta ones (`jakarta.tags.core`, `jakarta.tags.fmt`). **JSP EL does not reliably resolve Java records** on Tomcat 10.1 — model attributes rendered in JSP must be classes with getters (see `PaginasController.CardLoteria`), `Page`, `Map`, or the Lombok DTOs. Static assets live in `src/main/resources/static/`.

### Data notes

- The Mongo `loteria` field stores `Loteria.nome()` (e.g. `megasena`). Documents written by the old CLI used the API's `tipoJogo` value instead (e.g. `mega-sena`), so databases populated by the old version should be reimported once with `POST /api/loterias/{loteria}/importacao?completo=true`.
- There is no unique index on `(loteria, concurso)`; resume logic normally prevents duplicates, but a `completo=true` run is the way to clean up.
- Concursos imported before the premiação feature existed have no `rateio_premios` persisted; `PremioService` fetches and caches it lazily on first read instead of requiring a reimport.

### MongoDB connection

`application.properties` reads `MONGODB_HOST/PORT/DATABASE/USERNAME/PASSWORD` env vars with localhost defaults; `docker-compose.yaml` points the app at the `mongodb` service. Credentials are placeholders, not real secrets.

### Caixa API base URL

`caixa.api.base-url` (env `CAIXA_API_BASE_URL`) selects the API host, injected into `ImportacaoService`. Default is `servicebus2.caixa.gov.br` (more stable); `servicebus3.caixa.gov.br` publishes the day's result faster but errors more often — switch by uncommenting the env var in `docker-compose.yaml`.

### Caixa API TLS certificate

The Caixa API's certificate isn't trusted by the default JVM truststore. `install-caixa-cert.sh` downloads the certs of both `servicebus2` and `servicebus3` and imports them into `$JAVA_HOME/lib/security/cacerts` (aliases `caixa-servicebus2`/`caixa-servicebus3`); it runs at Docker image build time. When running outside Docker, run it manually once before importing.

### Logging

`logback-spring.xml` emits structured JSON to console only (via `logstash-logback-encoder`); there is a CONSOLE (plain-text) appender defined but not wired to the root logger.
