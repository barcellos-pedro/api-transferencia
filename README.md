# API Transferências

Esta API permite a gestão de transferências bancárias entre contas.
Através dela, é possível criar, listar e consultar transferências realizadas.

## Tarefa

Desenvolva um projeto que exponha APIs no padrão RESTful e atenda às seguintes
funcionalidades:

### Cadastro de Clientes

Um endpoint para cadastrar um cliente com as seguintes informações: ID (único), nome, número da conta (único) e saldo em
conta.

### Listagem de Clientes

Um endpoint para listar todos os clientes cadastrados.

### Busca de Cliente por Número da Conta

Um endpoint para buscar um cliente pelo número da conta.

### Transferência entre Contas

Um endpoint para realizar transferências entre duas contas.
A conta de origem precisa ter saldo suficiente para a realização da transferência.
O valor da transferência deve ser de no máximo R$ 10.000,00.

### Histórico de Transferências

Um endpoint para buscar as transferências relacionadas a uma conta, ordenadas por data decrescente.
Lembre-se de que transferências sem sucesso também devem ser armazenadas.

## Endpoints

- Cadastrar Clientes
- Listar Clientes
- Buscar Cliente por Número da Conta
- Transferência entre Contas
- Histórico de Transferências de uma Conta

## Entidades

Entidades/Tabelas do domínio da solução

### Cliente

- id (único)
- nome
- número da conta (único)
- saldo em conta

### Transferência

- id
- id_cliente_origem
- id_cliente_destino
- valor
- data
