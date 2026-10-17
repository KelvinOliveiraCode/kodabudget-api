# KodaBudget API

AI financial assistant built for the challenge **"Developing Your Intelligent API with Speech Recognition and Spring Boot"** (DIO). The user speaks (or types) a command, the API transcribes the audio, an LLM understands the intent, executes a real application function via tool calling, and returns the response — as text or as spoken MP3.

![CI](https://github.com/KelvinOliveiraCode/kodabudget-api/actions/workflows/ci.yml/badge.svg)

## What the project does

Main flow (same as the challenge):

```
audio (multipart) ─▶ transcription ─▶ LLM understands intent ─▶ tool calling
                    (Whisper)         (ChatClient)             (use cases)
                                                                   │
spoken response ◀─ text-to-speech ◀───── text response ◀──────────┘
  (edge-tts)                       (JSON with transcription + response)
```

Features:

- **Register transaction by voice or text** — "spent 45.90 at the market" creates the entry with category inferred by AI;
- **Query by voice or text** — "how much did I spend in September?" returns the monthly summary;
- **Monthly summary** — expenses, revenue, and category breakdown (implemented evolution);
- **Tool audit** — every function call executed by AI is logged with timestamp and arguments (implemented evolution);
- **Traditional REST CRUD** — same use cases exposed as REST endpoints, without AI.

## How to run

Requirements: Java 21 and Maven (or the included wrapper `./mvnw`).

```bash
./mvnw spring-boot:run     # Windows: mvnw.cmd spring-boot:run
```

The API starts at `http://localhost:9091` (Swagger at `/docs`).

### Environment variables

| Variable | Purpose | Default |
|---|---|---|
| `OPENAI_API_KEY` | Chat + tool calling | required in production |
| `OPENAI_BASE_URL` | OpenAI-compatible endpoint | `https://api.openai.com` |
| `OPENAI_CHAT_MODEL` | Chat model | `gpt-4o-mini` |

The API accepts any OpenAI-compatible endpoint — just set `OPENAI_BASE_URL` (Spring AI handles the rest). Transcription uses local Whisper (`faster-whisper`) and spoken response uses `edge-tts` (voice `pt-BR-AntonioNeural`) — voice at no cost, no key required. The `SpeechToTextBridge`/`TextToSpeechBridge` bridges isolate these choices: switching to a paid voice provider later means implementing the interface without touching the flow.

### Local voice mode prerequisites

- Python 3 with `faster-whisper` and `edge-tts` (`pip install faster-whisper edge-tts`);
- `python` and `ffmpeg` available on PATH.

## Test the main flow

Text command:

```bash
curl -X POST http://localhost:9091/api/v1/assistant/chat \
  -H "Content-Type: application/json" \
  -d '{"text":"Register it: spent 45.90 at the market"}'
```

Voice command (any spoken mp3/wav):

```bash
curl -X POST http://localhost:9091/api/v1/assistant/voice \
  -F "file=@command.mp3"
```

Spoken MP3 response:

```bash
curl -X POST http://localhost:9091/api/v1/assistant/voice/audio \
  -F "file=@command.mp3" -o response.mp3
```

### Real executions (from this machine)

Spoken command (pt-BR, local Whisper small + compatible chat model):

> audio: "Hello, please register: I spent twelve reais on today's bus"

```json
{"transcript":"Hello, please register, I spent 12 reais on today's bus.",
 "reply":"Done! I registered your expense of 12 reais on today's bus in the transportation category."}
```

Monthly summary queried by text:

> "Give me the September 2026 summary"

```json
{"reply":"In September 2026 you spent R$ 45.90, all on food, and had no revenue registered."}
```

## Technologies

- Java 21, Spring Boot 3.5 (Web, Data JPA, Validation)
- Spring AI 1.1 (ChatClient, Tool Calling with `@Tool`)
- file-based H2 (switch to PostgreSQL without touching the domain)
- faster-whisper (local STT) and edge-tts (local TTS, pt-BR voice)
- springdoc-openapi, JUnit 5 + AssertJ
- Layered architecture: `domain` / `application` / `infrastructure`

## Architecture

```
domain/          Transaction, Category, TransactionId, repository contract
application/     use cases (create, list, monthly summary) — no framework
infrastructure/  web (controllers), ai (ChatClient + @Tools + audit),
                 voice (local STT/TTS), persistence (JPA)
```

AI never touches the database: tools (`@Tool`) call application use cases, same path as REST endpoints. This keeps business rules in one place — the central principle of the track.

## What I learned

- **Tool calling in practice**: the model does not guess data — it invokes real functions with structured arguments and the final response comes from use cases, not LLM imagination;
- **Stable interfaces**: keeping the voice API behind bridges (`SpeechToTextBridge`/`TextToSpeechBridge`) allowed running everything locally without credentials and switching to OpenAI later without touching the flow;
- **Layers for testing**: use cases were tested with an in-memory fake repository — 6 tests that run offline, without database and without AI key.

## References

- [Official challenge track (DIO)](https://github.com/digitalinnovationone/dio-spring-boot-learning-track)
- [Spring AI Reference](https://docs.spring.io/spring-ai/reference/index.html)
- [Tool Calling API](https://docs.spring.io/spring-ai/reference/api/tools.html)
