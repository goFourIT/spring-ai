# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Spring Boot 4.1 / Spring AI 2.0 chat service on Java 26, backed by a local Ollama model, with Postgres-persisted chat memory and a full OpenTelemetry → Grafana observability stack.

## Commands

```bash
docker compose up -d                  # Ollama, Qdrant, Postgres, OTel collector, Loki, Prometheus, Tempo, Grafana
./mvnw spring-boot:run                # run the app (port 8080)
./mvnw clean package                  # build
./mvnw test                           # all tests
./mvnw test -Dtest=AiApplicationTests#contextLoads   # single test
```

Models are **not** auto-pulled (`pull-model-strategy: never`), so pull them once into the Ollama container:

```bash
docker exec ollama ollama pull llama3.2:3b
docker exec ollama ollama pull mxbai-embed-large
```

The only test is a full `@SpringBootTest`, so it needs the docker-compose services (Postgres, Ollama, OTel) running.

Try the endpoint (omit `conversationId` to start a new conversation; reuse the returned one to continue it):

```bash
curl -X POST localhost:8080/chat -H 'Content-Type: application/json' \
  -d '{"conversationId": null, "content": "What date is it in 10 days?"}'
```

Grafana: http://localhost:3000 (admin/admin), datasources and dashboards provisioned from `observability/grafana/`.

## Architecture

Request flow: `ChatController` (`POST /chat`, `MessageDto` in/out) → `ChatService` → a single `ChatClient` bean built in `configuration/ChatClientConfig`. The service passes the conversation id to advisors via `ChatMemory.CONVERSATION_ID`.

`ChatClientConfig` is where the AI pipeline is wired:
- **Default advisors**: `LogAdvisor` (custom `CallAdvisor`, logs outgoing prompt and model reply including tool calls; ordered `HIGHEST_PRECEDENCE + 400`) and `MessageChatMemoryAdvisor`.
- **Chat memory**: two `ChatMemory` beans exist, distinguished by `@Qualifier` — an in-memory window and a JDBC-backed window (`JdbcChatMemoryRepository`, 10 messages). The advisor uses the persisted one. Spring AI creates its own memory table (`spring.ai.chat.memory.repository.jdbc.initialize-schema: always`).
- **Tools**: `tool/DateTimeTools` (`@Tool` methods) registered via `defaultTools`. New tools should be `@Component`s added to that call.

Persistence: in addition to Spring AI's memory table, the app owns a `conversations`/`messages` schema defined in `src/main/resources/schema.sql` (run on every startup via `spring.sql.init.mode: always`) and mapped by JPA entities in `bo/`. Hibernate is set to `ddl-auto: validate`, so **schema changes go in `schema.sql` and the entities must match it** — Hibernate will not create or alter tables. `messages.sequence_id` is DB-generated from `message_sequence` (read back via `@Generated`) and provides ordering within a conversation.

Qdrant and the Ollama embedding model are configured (`collection-name: spring-ai-demo`, `initialize-schema: false`) but not yet used by any code; the collection must exist before vector store use.

Observability: traces, metrics, and logs are exported over OTLP HTTP to the collector at `localhost:4318`, which fans out to Tempo, Prometheus, and Loki (configs under `observability/`). Logs reach OTel through the `OTEL` appender in `logback-spring.xml`, which only works because `InstallOpenTelemetryAppender` installs the `OpenTelemetry` bean into it at startup. Tracing samples 100%.
