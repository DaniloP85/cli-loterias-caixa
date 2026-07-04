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
docker-compose up -d --build         # app on :8080 + mongodb (+ postgres, unrelated/unused)
docker-compose logs -f loterias-web
```

There are no automated tests in this repo currently.

## Architecture

Single Spring Boot app (`MainApplication`, extends `SpringBootServletInitializer`, has `@EnableRetry` — required for the `@Retryable` in `HttpServiceImpl` to actually work).

- `service/ImportacaoService` — background import. `iniciar(loteria, completo)` guards against concurrent runs per lottery and submits the work to an internal `ExecutorService`. The loop discovers the latest concurso number from the API (request without a concurso number), then fetches each concurso, computes features and saves via the repository. **Resume logic**: starts from the highest concurso already in Mongo (`completo=true` deletes the lottery's documents and reimports from 1). Progress is tracked per lottery in `StatusImportacao` objects held in a `ConcurrentHashMap`, exposed by the status endpoint. **SSE**: `assinar(loteria)` returns an `SseEmitter` (registered per lottery in a `CopyOnWriteArrayList`) that immediately receives the current status; the import loop publishes a `status` event after *every* insert, and completion/error sends a final event and completes the emitters. Dead emitters are dropped silently — SSE failures must never break an import.
- `service/JogoService` — "Meus jogos" (teimosinha) feature: `criar` validates dezenas against the `Loteria` range, `conferir` walks the concurso interval `[concursoInicial, concursoFinal]`, intersects the played numbers with each stored drawing and classifies PREMIADO/NAO_PREMIADO (via `Loteria.premiado(acertos)` — min prize tier per lottery; lotomania also wins with 0 hits) or PENDENTE when the concurso isn't in the base yet.
- `service/EstatisticaService` — MongoTemplate aggregations for the dashboard: per-dezena frequency (`$unwind` + `$group`) and averages of the stored features. Uses raw Mongo field names (`numeros_sorteados`, `historial.desvio_padrao`) because the aggregations are untyped.
- `service/DatasetService` — builds the flat `LinhaDataset` rows and writes CSV (columns `concurso,data,n1..nK,soma,...`; K = max drawn-numbers count found).
- `service/impl/HttpServiceImpl` — `RestTemplate` wrapper for the Caixa API, `@Retryable` (5 attempts, exponential backoff) on 5xx.
- `controller/api/ConcursoRestController` — `/api/loterias/{loteria}/...`: importacao (POST, 202/409), importacao/status, importacao/eventos (SSE), concursos (paged), concursos/{numero}, estatisticas, export (two mappings: default CSV as `byte[]`, `params = "formato=json"` for JSON — do not merge them back into one `ResponseEntity<?>` method, the declared generic type is what makes Spring pick the right converter).
- `controller/api/JogoRestController` — `/api/jogos`: POST (201), GET list with resumo, GET `{id}/conferencia`, DELETE (204).
- `controller/web/PaginasController` — JSP pages in three tabs (`abaAtiva` model attribute drives nav highlight): *manutencao* (`/` cards with SSE progress bar + Atualizar/Reconstruir buttons, `/loterias/{loteria}` paged table, `.../concursos/{numero}` detail, `.../dashboard` Chart.js), *jogos* (`/jogos` list+form, `/jogos/{id}` conferência), *ml* (`/ml` placeholder for a future AWS integration).
- `controller/StringParaLoteriaConverter` — binds the `{loteria}` path variable to the `Loteria` enum; invalid names become 400 via `ApiExceptionHandler` (handles `MethodArgumentTypeMismatchException` and `ConcursoNaoEncontradoException` → 404).
- `repository/ConcursoRepository` — Spring Data Mongo repository over `ConcursoMongoDTO` (`@Document(collection = "resultados")`). Note the interval query uses `GreaterThanEqual…LessThanEqual` instead of `Between` — Spring Data Mongo's `Between` generates exclusive `$gt/$lt`.
- `repository/JogoRepository` — repository over `JogoMongoDTO` (`@Document(collection = "jogos")`).
- `dto/` — `ConcursoDTO` (raw Caixa API shape), `ConcursoMongoDTO` + `FeaturesDTO` (persisted shape), `JogoMongoDTO` (persisted jogo), `EstatisticasDTO`, `LinhaDataset` (export row), `ConferenciaJogo`/`ConferenciaConcurso`/`ResumoJogo`/`JogoComResumo` (conferência view models — getter classes on purpose, they render in JSP).
- `utility/MyMath`, `records/AltosBaixos`, `records/ParesImparesFeature` — pure feature computation.
- `enums/Loteria` — per-lottery `(min, max)` range; `nome()` is the canonical lowercase name used both in the Caixa API URL path and in the Mongo `loteria` field. **Only MEGASENA, LOTOFACIL, QUINA, LOTOMANIA are supported.** The Caixa API for other games (e.g. `federal`) returns a different JSON shape (prize tickets, not drawn numbers), so adding one is not just an enum entry.

### JSP views

`src/main/webapp/WEB-INF/jsp/` — `home.jsp`, `concursos.jsp`, `concurso.jsp`, `dashboard.jsp` + shared `comum/cabecalho.jspf`/`rodape.jspf`. Taglibs are the Jakarta ones (`jakarta.tags.core`, `jakarta.tags.fmt`). **JSP EL does not reliably resolve Java records** on Tomcat 10.1 — model attributes rendered in JSP must be classes with getters (see `PaginasController.CardLoteria`), `Page`, `Map`, or the Lombok DTOs. Static assets live in `src/main/resources/static/`.

### Data notes

- The Mongo `loteria` field stores `Loteria.nome()` (e.g. `megasena`). Documents written by the old CLI used the API's `tipoJogo` value instead (e.g. `mega-sena`), so databases populated by the old version should be reimported once with `POST /api/loterias/{loteria}/importacao?completo=true`.
- There is no unique index on `(loteria, concurso)`; resume logic normally prevents duplicates, but a `completo=true` run is the way to clean up.

### MongoDB connection

`application.properties` reads `MONGODB_HOST/PORT/DATABASE/USERNAME/PASSWORD` env vars with localhost defaults; `docker-compose.yaml` points the app at the `mongodb` service. Credentials are placeholders, not real secrets.

### Caixa API TLS certificate

The Caixa API's certificate isn't trusted by the default JVM truststore. `install-caixa-cert.sh` downloads and imports it into `$JAVA_HOME/lib/security/cacerts`; it runs at Docker image build time. When running outside Docker, run it manually once before importing.

### Logging

`logback-spring.xml` emits structured JSON to console only (via `logstash-logback-encoder`); there is a CONSOLE (plain-text) appender defined but not wired to the root logger.
