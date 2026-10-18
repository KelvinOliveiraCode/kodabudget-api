# kodabudget-api

<div align="center">

[English](README.en.md) · **Português (Brasil)**

</div>

[![Java 21](https://img.shields.io/badge/Java-21-%23007396?style=flat-square)](https://adoptium.net/) [![Spring Boot 3](https://img.shields.io/badge/Spring%20Boot-3-%236DB33F?style=flat-square)](https://spring.io/projects/spring-boot) [![Maven](https://img.shields.io/badge/Maven-3.9-C71A36?style=flat-square&logo=apache-maven)](https://maven.apache.org/) [![JUnit 5](https://img.shields.io/badge/JUnit-5-25A162?style=flat-square&logo=junit5)](https://junit.org/junit5/) [![MIT License](https://img.shields.io/badge/License-MIT-f4c430?style=flat-square)](LICENSE)

### Assistente financeiro com IA por voz: Whisper + tool calling Spring AI + edge-tts

---

## O que é

Assistente financeiro com IA por voz: Whisper + tool calling Spring AI + edge-tts

---

## Início Rápido

```bash
# Clone e entre
git clone https://github.com/KelvinOliveiraCode/<repo>.git
cd <repo>

# Build e execute os testes
./mvnw verify

# Execute a aplicação
./mvnw spring-boot:run
# API disponível em http://localhost:8080
# Swagger UI em http://localhost:8080/docs
```

## Como funciona

Estrutura limpa e modular com separação de responsabilidades.

---

## Capabilities

| Capability | Description |
|------------|-------------|
| REST API | Spring Boot 3, versioned endpoints, RFC 7807 errors |
| Fee Calculation | Strategy pattern ,  PIX R$0.49, Boleto R$2.49, Credit 3.99%, Debit 1.89% |
| Risk Validation | Chain of Responsibility ,  HighValue -> Blocklist -> Velocity |
| Transaction Registry | Singleton in-memory registry with full audit trail |
| Event System | Observer pattern ,  decoupled messaging & metrics listeners |
| Observability | /api/v1/observability endpoint exposing listener effects |
| API Docs | springdoc-openapi ,  Swagger UI at /docs, OpenAPI 3 at /api-docs |
| Testing | JUnit 5, Mockito, MockMvc ,  integration + unit tests |


---



---

## FAQ

<details>
<summary><strong>What is this project?</strong></summary>

A Java/Spring Boot study/project demonstrating core concepts.

</details>

<details>
<summary><strong>How do I run it?</strong></summary>

See [Quick Start](#quick-start) for copy-paste commands.

</details>

<details>
<summary><strong>Are there tests?</strong></summary>

No automated tests in this repository.

</details>

<details>
<summary><strong>Can I use this in production?</strong></summary>

This is a **study/portfolio project**. Review and harden (auth, persistence, monitoring, etc.) before production use.

</details>



---

## Documentation

- **Source Code** ,  Browse the `src/` directory for implementation details
- **API Reference** ,  Swagger UI at `/docs` when running
- **Architecture Diagram** ,  See [architecture.mermaid](docs/architecture.mermaid) (if present)
- **Changelog** ,  [CHANGELOG.md](CHANGELOG.md) (if present)
- **Contributing** ,  [CONTRIBUTING.md](CONTRIBUTING.md) (if present)

## License

[MIT](LICENSE) ,  Kelvin Oliveira

---

<div align="center">

**Built with** · Java 21 + Spring Boot 3 + Maven

**Author** · [Kelvin Oliveira](https://github.com/KelvinOliveiraCode) · [Portfolio](https://kelvinoliveiracode.github.io/portfolio/)

</div>