# 🏦 Transfer Service API

Este projeto consiste em uma API RESTful para gerenciamento de clientes e realização de transferências financeiras,
desenvolvida como parte do processo seletivo para Engenheiro de Software. A solução foca em **consistência de dados**, *
*auditabilidade** e **escalabilidade**.

## 🛠️ Tecnologias e Requisitos

* **Java 21** (LTS)
* **Spring Boot 3**
* **Spring Data JPA** com Banco de Dados **H2** (In-memory)
* **Maven** (Gerenciador de dependências)
* **SpringDoc OpenAPI (Swagger)** (Documentação)
* **Bean Validation** (Validação de entradas)

## 🚀 Como Executar a Aplicação

1. **Pré-requisitos**: Certifique-se de ter o **JDK 21** e o **Maven** instalados.
2. **Clone o repositório**:

```bash
git clone https://github.com/barcellos-pedro/api-transferencia.git
cd api-transferencia
```

3. **Compile e execute**:

```bash
mvn spring-boot:run
```

4. **Acesse a API**: A aplicação estará disponível em `http://localhost:8080`.
5. **Documentação Interativa (Swagger)**: Acesse `http://localhost:8080/swagger-ui/index.html` para testar os endpoints
   no navegador

## 📖 Endpoints Principais

A API segue o padrão RESTful e versionamento via URL (`/v1/...`):

**Clientes**:

* `GET /v1/customers`: Listagem geral de clientes.
* `POST /v1/customers`: Cadastro de novo cliente.
* `GET /v1/customers/{account}`: Busca Cliente por número de conta.
* `GET /v1/customers/{account}/transfers`: Histórico ordenado por data decrescente, incluindo falhas.
* `POST /v1/customers/{account}/transfers`: Realiza transferência entre contas (Limite de R$ 10.000,00).

## Decisões de Engenharia & Arquitetura

### 1. Resiliência no Histórico (Auditoria)

Conforme solicitado, transferências sem sucesso também são armazenadas. Para garantir que o registro de falha seja
persistido mesmo quando a transação financeira sofrer rollback, utilizei a propagação **`REQUIRES_NEW`** no serviço de
auditoria. Isso garante a integridade do histórico para conformidade bancária.

### 2. Controle de Concorrência

Para atender ao requisito de controle de concorrência na operação de transferência, foi implementado:

* **Pessimistic Locking** (ou **Optimistic Locking** com `@Version`): Para evitar o problema de "Lost Update" quando
  dois processos tentam debitar da mesma conta simultaneamente.

### 3. Validação de Regras de Negócio

As regras de saldo suficiente e limite máximo de R$ 10.000,00 por operação foram centralizadas na camada de serviço,
garantindo que o estado do banco de dados permaneça consistente.

## 🧪 Testes

A cobertura de testes foi priorizada para garantir a confiabilidade das transferências:

* **Testes Unitários**: Validação de lógica de negócio e cálculos de saldo.
* **Testes de Integração**: Fluxo completo de transferência simulando concorrência e rollback de banco de dados.

Execute os testes com:

```bash
mvn test
```
