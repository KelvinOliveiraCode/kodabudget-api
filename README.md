# kodabudget-api — Assistente financeiro com IA por voz

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Spring AI](https://img.shields.io/badge/Spring%20AI-1.1-6DB33F?style=flat-square&logo=spring&logoColor=white)
![Tests](https://img.shields.io/badge/tests-6%20offline-brightgreen?style=flat-square)
![CI](https://img.shields.io/github/actions/workflows/status/KelvinOliveiraCode/kodabudget-api/ci.yml?branch=main&style=flat-square&label=CI)

Assistente financeiro conversacional em **Java 21 + Spring Boot 3.5 + Spring AI 1.1**: a pessoa fala (ou digita) um comando, a API transcreve o áudio com **Whisper local**, um LLM entende a intenção, executa uma função real da aplicação via **tool calling** e devolve a resposta — em texto ou falada em MP3 com **edge-tts**. Arquitetura em camadas (`domain` / `application` / `infrastructure`) onde a IA nunca toca no banco: as tools chamam use cases, o mesmo caminho dos endpoints REST.

---

## 🇧🇷 Português

### O que o projeto faz

```
áudio (multipart) ─▶ transcrição ─▶ LLM entende a intenção ─▶ tool calling
                    (Whisper)        (ChatClient)              (use cases)
                                                                  │
resposta falada ◀─ text-to-speech ◀──────── resposta em texto ◀──┘
  (edge-tts)                          (JSON com transcrição + resposta)
```

- **Registrar transação por voz ou texto** — "gastei 45,90 no mercado" cria o lançamento com categoria inferida pela IA;
- **Consultar por voz ou texto** — "quanto gastei em setembro?" devolve o sumário mensal;
- **Sumário mensal** — despesas, receitas e quebra por categoria;
- **Auditoria de tools** — cada chamada de função que a IA executou fica registrada com horário e argumentos;
- **CRUD REST tradicional** — os mesmos use cases expostos como endpoints REST, sem IA.

### Execuções reais

Comando falado (pt-BR, Whisper local + modelo de chat compatível):

> áudio: "Olá, registre por favor: gastei doze reais com o ônibus de hoje"

```json
{"transcript":"Olá, registre por favor, gastei 12 reais com ônibus de hoje.",
 "reply":"Prontinho! Registrei sua despesa de 12 reais com ônibus na categoria transporte."}
```

Sumário mensal consultado por texto ("Me da o resumo do mes de setembro de 2026"):

```json
{"reply":"Em setembro de 2026 você gastou R$ 45,90, todos com alimentação, e não teve nenhuma receita registrada."}
```

### Arquitetura

```
domain/          Transaction, Category, TransactionId, contrato do repositório
application/     use cases (criar, listar, sumário mensal) — sem framework
infrastructure/  web (controllers), ai (ChatClient + @Tools + auditoria),
                 voice (STT/TTS locais), persistence (JPA)
```

A IA nunca toca no banco: as ferramentas (`@Tool`) chamam use cases da aplicação, o mesmo caminho dos endpoints REST. Isso mantém as regras de negócio num lugar só.

### Voz 100% local, zero custo

- STT: **faster-whisper** (local); TTS: **edge-tts** com voz `pt-BR-AntonioNeural` — sem chave, sem custo;
- As pontes `SpeechToTextBridge` / `TextToSpeechBridge` isolam essas escolhas: trocar por um provedor pago depois é implementar a interface, sem mexer no fluxo;
- O LLM aceita **qualquer endpoint OpenAI-compatível** via `OPENAI_BASE_URL`.

### Endpoints

| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/v1/assistant/chat` | Comando em texto |
| POST | `/api/v1/assistant/voice` | Comando em áudio → resposta em texto |
| POST | `/api/v1/assistant/voice/audio` | Comando em áudio → resposta falada em MP3 |
| GET/POST | `/api/v1/transactions` | CRUD REST tradicional (sem IA) |
| GET | `/docs` | Swagger UI |

### Como rodar

Requisitos: Java 21 e Maven (wrapper `./mvnw` incluído); para o modo voz, Python 3 com `faster-whisper` e `edge-tts` (`pip install faster-whisper edge-tts`) e `ffmpeg` no PATH.

```bash
./mvnw spring-boot:run     # Windows: mvnw.cmd spring-boot:run
```

API sobe em `http://localhost:9091` (Swagger em `/docs`).

| Variável | Para quê | Padrão |
|---|---|---|
| `OPENAI_API_KEY` | Chat + tool calling | obrigatória em produção |
| `OPENAI_BASE_URL` | Endpoint OpenAI-compatível | `https://api.openai.com` |
| `OPENAI_CHAT_MODEL` | Modelo de chat | `gpt-4o-mini` |

### O que aprendi

- **Tool calling na prática**: o modelo não adivinha dados — ele invoca funções reais com argumentos estruturados e a resposta final sai dos use cases, não da imaginação do LLM;
- **Interfaces estáveis valem ouro**: manter a API de voz atrás de pontes permitiu rodar tudo local sem credencial;
- **Camadas pagam o teste**: 6 testes que rodam offline (repositório falso em memória), sem banco e sem chave de IA.

### Autor

**Kelvin Oliveira** — [GitHub](https://github.com/KelvinOliveiraCode) · [LinkedIn](https://www.linkedin.com/in/kelvin-oliveira-0282033b4/)

---

## 🇺🇸 English

A conversational finance assistant in **Java 21 + Spring Boot 3.5 + Spring AI 1.1**: the user speaks (or types) a command, the API transcribes the audio with **local Whisper**, an LLM understands the intent, executes a real application function via **tool calling**, and answers back — as text or as spoken MP3 via **edge-tts**. Layered architecture (`domain` / `application` / `infrastructure`) where the AI never touches the database: tools call use cases, the same path as the REST endpoints.

### What it does

- **Register a transaction by voice or text** — "gastei 45,90 no mercado" creates the entry with AI-inferred category;
- **Query by voice or text** — monthly summary with expenses, income and per-category breakdown;
- **Tool audit** — every function call the AI made is logged with timestamp and arguments;
- **Plain REST CRUD** — the same use cases exposed as REST endpoints, no AI involved.

### Voice, 100% local, zero cost

STT via **faster-whisper** and TTS via **edge-tts** (`pt-BR-AntonioNeural`) — no API keys, no cost. The `SpeechToTextBridge` / `TextToSpeechBridge` interfaces isolate these choices: swapping in a paid provider later means implementing an interface, not touching the flow. The chat model accepts **any OpenAI-compatible endpoint** through `OPENAI_BASE_URL`.

### Run it

Requirements: Java 21 and Maven (wrapper included); for voice mode, Python 3 with `faster-whisper` and `edge-tts` plus `ffmpeg` on PATH.

```bash
./mvnw spring-boot:run     # Windows: mvnw.cmd spring-boot:run
```

API starts at `http://localhost:9091` (Swagger at `/docs`). Set `OPENAI_API_KEY` (required in production); `OPENAI_BASE_URL` and `OPENAI_CHAT_MODEL` are configurable.

### What I learned

- **Tool calling in practice**: the model doesn't guess data — it invokes real functions with structured arguments, and the final answer comes from use cases, not LLM imagination;
- **Stable interfaces pay off**: keeping voice behind bridges allowed running fully local with no credentials;
- **Layers earn their tests**: 6 tests that run offline (fake in-memory repository), no database, no AI key.

### Author

**Kelvin Oliveira** — [GitHub](https://github.com/KelvinOliveiraCode) · [LinkedIn](https://www.linkedin.com/in/kelvin-oliveira-0282033b4/)
