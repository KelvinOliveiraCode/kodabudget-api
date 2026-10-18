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

## Capacidades

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

## Perguntas Frequentes

<details>
<summary><strong>O que é este projeto?</strong></summary>

Um estudo/projeto em Java/Spring Boot demonstrando conceitos fundamentais.

</details>

<details>
<summary><strong>Como executar?</strong></summary>

Veja [Início Rápido](#início-rápido) para comandos copiáveis.

</details>

<details>
<summary><strong>Há testes?</strong></summary>

Nenhum teste automatizado neste repositório.

</details>

<details>
<summary><strong>Posso usar em produção?</strong></summary>

Este é um **projeto de estudo/portfólio**. Revise e endureça (auth, persistência, monitoramento, etc.) antes de usar em produção.

</details>



---

## Documentação

- **Código-Fonte** ,  Navegue pelo diretório `src/` para detalhes de implementação
- **Referência da API** ,  Swagger UI em `/docs` ao executar
- **Diagrama de Arquitetura** ,  Veja [architecture.mermaid](docs/architecture.mermaid) (se presente)
- **Changelog** ,  [CHANGELOG.md](CHANGELOG.md) (se presente)
- **Contribuindo** ,  [CONTRIBUTING.md](CONTRIBUTING.md) (se presente)

## Licença

[MIT](LICENSE) ,  Kelvin Oliveira

---

<div align="center">

**Construído com** · Java 21 + Spring Boot 3 + Maven

**Autor** · [Kelvin Oliveira](https://github.com/KelvinOliveiraCode) · [Portfolio](https://kelvinoliveiracode.github.io/portfolio/)

</div>