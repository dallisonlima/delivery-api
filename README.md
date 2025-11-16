# Delivery Tech API

Sistema de delivery desenvolvido com Spring Boot e Java 21.

## 🚀 Tecnologias

- **Java 21 LTS** (versão mais recente)
- Spring Boot 3.2.x
- Spring Web
- Spring Data JPA
- H2 Database
- Maven

## ⚡ Recursos Modernos Utilizados

- Records (Java 14+)
- Text Blocks (Java 15+)
- Pattern Matching (Java 17+)
- Virtual Threads (Java 21)

## 🏃‍♂️ Como executar

1.  **Pré-requisitos:** JDK 21 instalado
2.  Clone o repositório
3.  Execute: `./mvnw spring-boot:run`
4.  Acesse: http://localhost:8080/health

## 📋 Endpoints Principais

- `GET /health` - Status da aplicação (inclui versão Java)
- `GET /info` - Informações da aplicação
- `GET /h2-console` - Console do banco H2
- `POST /clientes` - Cadastra um novo cliente.
- `GET /clientes/{id}` - Busca um cliente por ID.
- `POST /restaurantes` - Cadastra um novo restaurante.
- `GET /restaurantes` - Lista os restaurantes.
- `POST /pedidos` - Cria um novo pedido.
- `GET /clientes/{id}/pedidos` - Lista os pedidos de um cliente específico.
- `PATCH /clientes/{clienteId}/pedidos/{pedidoId}/status` - Atualiza o status de um pedido.

## 🧪 Testando a API com Insomnia

Para facilitar os testes dos endpoints, uma collection do Insomnia está disponível no projeto.

1.  **Importar a Collection**:
    *   Abra o Insomnia.
    *   Vá em `Import/Export`.
    *   Clique em `Import Data` e selecione `From File`.
    *   Escolha o arquivo `Collections Insomnia/delivery-api-collection.json` na raiz do projeto.

2.  **Usar a Collection**:
    *   Após importar, uma nova coleção chamada "Delivery API" aparecerá.
    *   As requisições estão organizadas por recurso (Clientes, Restaurantes, etc.).
    *   A variável de ambiente `baseUrl` já está configurada para `http://localhost:8080`.

## 💡 Exemplos de Uso da API

### 1. Cadastrar um novo Cliente

**Requisição:** `POST /clientes`

```json
{
    "nome": "João da Silva",
    "email": "joao.silva@example.com",
    "telefone": "11987654321",
    "endereco": "Rua das Flores, 123"
}
```

### 2. Cadastrar um novo Restaurante

**Requisição:** `POST /restaurantes`

```json
{
    "nome": "Pizzaria Forno a Lenha",
    "taxaEntrega": 7.50,
    "categoria": "Pizza",
    "ativo": true,
    "endereco": "Avenida Principal, 456",
    "avaliacao": 4.7
}
```

### 3. Criar um novo Pedido

**Requisição:** `POST /pedidos`

**Importante:** Certifique-se de que o cliente (ID 1) e o produto (ID 1) já existem no banco.

```json
{
    "cliente": { "id": 1 },
    "restaurante": { "id": 1 },
    "enderecoEntrega": "Rua das Flores, 123",
    "itens": [
        {
            "produto": { "id": 1 },
            "quantidade": 2
        }
    ]
}
```

### 4. Buscar os Pedidos de um Cliente

**Requisição:** `GET /clientes/1/pedidos`

### 5. Atualizar o Status de um Pedido

**Requisição:** `PATCH /clientes/1/pedidos/1/status?status=CONFIRMADO`

## 🔧 Configuração

- Porta: 8080
- Banco: H2 em memória
- Profile: development

## 👨‍💻 Desenvolvedor

[Dállison Silveira Lima] - [Sistemas da Informação - USJT - Vila Leopoldina]

Desenvolvido com JDK 21 e Spring Boot 3.2.x
