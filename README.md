# KodaBudget API

Assistente financeiro com IA construído para o desafio **"Desenvolvendo sua API Inteligente com Reconhecimento de Fala e Spring Boot"** (DIO). A pessoa fala (ou digita) um comando, a API transcreve o áudio, um LLM entende a intenção, executa uma função real da aplicação via tool calling e devolve a resposta — em texto ou falada em MP3.

![CI](https://github.com/KelvinOliveiraCode/kodabudget-api/actions/workflows/ci.yml/badge.svg)

## O que o projeto faz

Fluxo principal (o mesmo do desafio):

```
áudio (multipart) ─▶ transcrição ─▶ LLM entende a intenção ─▶ tool calling
                    (Whisper)        (ChatClient)              (use cases)
                                                                  │
resposta falada ◀─ text-to-speech ◀──────── resposta em texto ◀──┘
  (edge-tts)                          (JSON com transcrição + resposta)
```

Funcionalidades:

- **Registrar transação por voz ou texto** — "gastei 45,90 no mercado" cria o lançamento com categoria inferida pela IA;
- **Consultar por voz ou texto** — "quanto gastei em setembro?" devolve o sumário mensal;
- **Sumário mensal** — despesas, receitas e quebra por categoria (evolução implementada);
- **Auditoria de tools** — cada chamada de função que a IA executou fica registrada com horário e argumentos (evolução implementada);
- **CRUD REST tradicional** — os mesmos use cases expostos como endpoints REST, sem IA.

## Como executar

Requisitos: Java 21 e Maven (ou o wrapper `./mvnw` incluído).

```bash
./mvnw spring-boot:run     # Windows: mvnw.cmd spring-boot:run
```

A API sobe em `http://localhost:9091` (Swagger em `/docs`).

### Variáveis de ambiente

| Variável | Para quê | Padrão |
|---|---|---|
| `OPENAI_API_KEY` | Chat + tool calling | obrigatória em produção |
| `OPENAI_BASE_URL` | Endpoint OpenAI-compatível | `https://api.openai.com` |
| `OPENAI_CHAT_MODEL` | Modelo de chat | `gpt-4o-mini` |

A API aceita **qualquer endpoint compatível com a API da OpenAI** — basta apontar `OPENAI_BASE_URL` (o Spring AI cuida do resto). A transcrição usa Whisper local (`faster-whisper`) e a resposta falada usa `edge-tts` (voz `pt-BR-AntonioNeural`) — voz sem custo, sem chave. As pontes `SpeechToTextBridge`/`TextToSpeechBridge` isolam essas escolhas: trocar por um provedor de voz pago depois é implementar a interface, sem mexer no fluxo.

### Pré-requisitos do modo voz local

- Python 3 com `faster-whisper` e `edge-tts` (`pip install faster-whisper edge-tts`);
- `python` e `ffmpeg` acessíveis no PATH.

## Testar o fluxo principal

Comando em texto:

```bash
curl -X POST http://localhost:9091/api/v1/assistant/chat \
  -H "Content-Type: application/json" \
  -d '{"text":"Registra ai: gastei 45,90 no mercado"}'
```

Comando por voz (qualquer mp3/wav falado):

```bash
curl -X POST http://localhost:9091/api/v1/assistant/voice \
  -F "file=@comando.mp3"
```

Resposta falada em MP3:

```bash
curl -X POST http://localhost:9091/api/v1/assistant/voice/audio \
  -F "file=@comando.mp3" -o resposta.mp3
```

### Execuções reais (desta máquina)

Comando falado (pt-BR, Whisper small local + modelo de chat compatível):

> áudio: "Olá, registre por favor: gastei doze reais com o ônibus de hoje"

```json
{"transcript":"Olá, registre por favor, gastei 12 reais com ônibus de hoje.",
 "reply":"Prontinho! Registrei sua despesa de 12 reais com ônibus na categoria transporte."}
```

Sumário mensal consultado por texto:

> "Me da o resumo do mes de setembro de 2026"

```json
{"reply":"Em setembro de 2026 você gastou R$ 45,90, todos com alimentação, e não teve nenhuma receita registrada."}
```

## Tecnologias

- Java 21, Spring Boot 3.5 (Web, Data JPA, Validation)
- Spring AI 1.1 (ChatClient, Tool Calling com `@Tool`)
- H2 em arquivo (troca por PostgreSQL sem tocar no domínio)
- faster-whisper (STT local) e edge-tts (TTS local, voz pt-BR)
- springdoc-openapi, JUnit 5 + AssertJ
- Arquitetura em camadas: `domain` / `application` / `infrastructure`

## Arquitetura

```
domain/          Transaction, Category, TransactionId, contrato do repositório
application/     use cases (criar, listar, sumário mensal) — sem framework
infrastructure/  web (controllers), ai (ChatClient + @Tools + auditoria),
                 voice (STT/TTS locais), persistence (JPA)
```

A IA nunca toca no banco: as ferramentas (`@Tool`) chamam use cases da aplicação, o mesmo caminho dos endpoints REST. Isso mantém as regras de negócio num lugar só — o princípio central da trilha.

## O que aprendi

- **Tool calling na prática**: o modelo não adivinha dados — ele invoca funções reais com argumentos estruturados e a resposta final sai dos use cases, não da imaginação do LLM;
- **Interfaces estáveis valem ouro**: manter a API de voz atrás de pontes (`SpeechToTextBridge`/`TextToSpeechBridge`) permitiu rodar tudo local sem credencial e trocar para OpenAI depois sem mexer no fluxo;
- **Camadas pagam o teste**: os use cases foram testados com repositório falso em memória — 6 testes que rodam offline, sem banco e sem chave de IA.

## Referências

- [Trilha oficial do desafio (DIO)](https://github.com/digitalinnovationone/dio-spring-boot-learning-track)
- [Spring AI Reference](https://docs.spring.io/spring-ai/reference/index.html)
- [Tool Calling API](https://docs.spring.io/spring-ai/reference/api/tools.html)
