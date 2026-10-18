# kodabudget-api

<div align="center">

**English** · [Português (Brasil)](README.md)

</div>

[![Java 21](https://img.shields.io/badge/Java-21-%23007396?style=flat-square)](https://adoptium.net/) [![Spring Boot 3](https://img.shields.io/badge/Spring%20Boot-3-%236DB33F?style=flat-square)](https://spring.io/projects/spring-boot) [![Maven](https://img.shields.io/badge/Maven-3.9-C71A36?style=flat-square&logo=apache-maven)](https://maven.apache.org/) [![JUnit 5](https://img.shields.io/badge/JUnit-5-25A162?style=flat-square&logo=junit5)](https://junit.org/junit5/) [![MIT License](https://img.shields.io/badge/License-MIT-f4c430?style=flat-square)](LICENSE)

### Voice-powered finance assistant with Spring AI tool calling

---

## What it is

Voice-powered finance assistant with Spring AI tool calling

---

## Quick Start

```bash
# Clone and enter
git clone https://github.com/KelvinOliveiraCode/<repo>.git
cd <repo>

# Build and run tests
./mvnw verify

# Run the application
./mvnw spring-boot:run
# API available at http://localhost:8080
# Swagger UI at http://localhost:8080/docs
```

## How it works

Estrutura limpa e modular com separação de responsabilidades.

---

## Capabilities

| Capacidade | Descrição |
|------------|-------------|
| API REST | Spring Boot 3, endpoints versionados, erros RFC 7807 |
| Cálculo de Taxa | Strategy pattern ,  PIX R$0,49, Boleto R$2,49, Crédito 3,99%, Débito 1,89% |
| Validação de Risco | Chain of Responsibility ,  HighValue -> Blocklist -> Velocity |
| Registro de Transações | Registry Singleton em memória com trilha de auditoria completa |
| Sistema de Eventos | Observer pattern ,  mensageria e métricas desacopladas |
| Observabilidade | Endpoint /api/v1/observability expondo efeitos dos listeners |
| Documentação da API | springdoc-openapi ,  Swagger UI em /docs, OpenAPI 3 em /api-docs |
| Testes | JUnit 5, Mockito, MockMvc ,  testes de integração + unidade |


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