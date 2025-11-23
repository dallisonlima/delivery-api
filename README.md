# Delivery Tech API

![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)
![Security](https://img.shields.io/badge/Security-JWT-blueviolet)
![Documentation](https://img.shields.io/badge/Docs-Swagger-orange)

API RESTful para um sistema de delivery, desenvolvida com as tecnologias mais recentes do ecossistema Java e Spring.

## 🚀 Funcionalidades

- **Autenticação e Autorização:** Sistema de segurança completo com JWT e controle de acesso baseado em perfis (`ADMIN`, `RESTAURANTE`, `CLIENTE`).
- **Gerenciamento de Restaurantes:** CRUD completo para restaurantes.
- **Gerenciamento de Produtos:** CRUD completo para produtos, associados a restaurantes.
- **Gerenciamento de Pedidos:** Fluxo completo para criação e acompanhamento de pedidos.
- **Gerenciamento de Clientes:** Operações básicas de CRUD para clientes.
- **Documentação Interativa:** Interface Swagger para explorar e testar todos os endpoints da API.

## 🛠️ Tecnologias Utilizadas

- **Java 21 LTS**
- **Spring Boot 3.x**
  - Spring Web
  - Spring Data JPA
  - Spring Security (com JWT)
- **Springdoc-OpenAPI (Swagger 3)** para documentação da API.
- **Hibernate & JPA** para persistência de dados.
- **H2 Database** como banco de dados em memória.
- **Maven** para gerenciamento de dependências.

## 🏃‍♂️ Como Executar o Projeto

### Pré-requisitos

- **JDK 21** (ou superior) instalado e configurado.
- **Maven** instalado e configurado (ou use o Maven Wrapper incluído).

### Passos para Execução

1.  **Clone o repositório:**
    ```bash
    git clone <url-do-repositorio>
    cd delivery-api
    ```

2.  **Execute o projeto com o Maven Wrapper:**
    ```bash
    ./mvnw spring-boot:run
    ```
    O servidor iniciará na porta `8080`.

## 📚 Documentação da API (Swagger)

A API possui uma documentação interativa completa gerada com Swagger (Springdoc). Após iniciar a aplicação, você pode acessá-la para ver todos os endpoints, modelos e testá-los diretamente no navegador.

- **URL do Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Testando Endpoints Protegidos no Swagger

1.  Primeiro, obtenha um token JWT através do endpoint `POST /api/auth/login`.
2.  Copie o token recebido na resposta.
3.  Clique no botão **"Authorize"** no canto superior direito da página do Swagger.
4.  Na janela que abrir, cole o token no campo "Value" e clique em "Authorize".
5.  Pronto! Agora você pode executar os endpoints que exigem autenticação.

## 🔐 Autenticação e Autorização

A API utiliza **JSON Web Tokens (JWT)** para autenticação. Todas as requisições para endpoints protegidos devem conter o cabeçalho:

`Authorization: Bearer {seu_token_jwt}`

### Perfis de Usuário (Roles)

Existem 3 perfis de usuário com diferentes níveis de permissão:

-   `ROLE_ADMIN`: Acesso total ao sistema.
-   `ROLE_RESTAURANTE`: Gerencia apenas os recursos do seu próprio restaurante.
-   `ROLE_CLIENTE`: Pode criar pedidos e visualizar seu próprio histórico.

## 📋 Endpoints da API

Abaixo está a lista completa de endpoints disponíveis.

---

### Autenticação (`/api/auth`)

| Método | Endpoint               | Descrição                        | Permissão      |
| :----- | :--------------------- | :------------------------------- | :------------- |
| `POST` | `/api/auth/register`   | Registra um novo usuário.        | **Público**    |
| `POST` | `/api/auth/login`      | Autentica um usuário e gera um token. | **Público**    |
| `GET`  | `/api/auth/me`         | Retorna os dados do usuário logado. | **Autenticado** |

---

### Restaurantes (`/api/restaurantes`)

| Método    | Endpoint                          | Descrição                               | Permissão                               |
| :-------- | :-------------------------------- | :-------------------------------------- | :-------------------------------------- |
| `GET`     | `/api/restaurantes`               | Lista todos os restaurantes.            | **Público**                             |
| `GET`     | `/api/restaurantes/{id}`          | Busca um restaurante por ID.            | **Autenticado**                         |
| `POST`    | `/api/restaurantes`               | Cadastra um novo restaurante.           | `ADMIN`                                 |
| `PUT`     | `/api/restaurantes/{id}`          | Atualiza um restaurante.                | `ADMIN` ou `RESTAURANTE` (dono)         |
| `DELETE`  | `/api/restaurantes/{id}`          | Deleta um restaurante.                  | `ADMIN`                                 |
| `PATCH`   | `/api/restaurantes/{id}/status`   | Ativa ou desativa um restaurante.       | `ADMIN`                                 |
| `GET`     | `/{restauranteId}/pedidos`        | Busca os pedidos de um restaurante.     | `ADMIN` ou `RESTAURANTE` (dono)         |

---

### Produtos (`/api/produtos`)

| Método    | Endpoint                          | Descrição                           | Permissão                               |
| :-------- | :-------------------------------- | :---------------------------------- | :-------------------------------------- |
| `GET`     | `/api/produtos`                   | Lista todos os produtos.            | **Público**                             |
| `GET`     | `/api/produtos/{id}`              | Busca um produto por ID.            | **Autenticado**                         |
| `POST`    | `/api/produtos`                   | Cadastra um novo produto.           | `ADMIN` ou `RESTAURANTE`                |
| `PUT`     | `/api/produtos/{id}`              | Atualiza um produto.                | `ADMIN` ou `RESTAURANTE` (dono)         |
| `DELETE`  | `/api/produtos/{id}`              | Deleta um produto.                  | `ADMIN` ou `RESTAURANTE` (dono)         |
| `PATCH`   | `/api/produtos/{id}/disponibilidade` | Altera a disponibilidade de um produto. | `ADMIN` ou `RESTAURANTE` (dono)         |

---

### Pedidos (`/api/pedidos`)

| Método    | Endpoint                          | Descrição                           | Permissão                               |
| :-------- | :-------------------------------- | :---------------------------------- | :-------------------------------------- |
| `POST`    | `/api/pedidos`                    | Cria um novo pedido.                | `CLIENTE`                               |
| `GET`     | `/api/pedidos`                    | Lista todos os pedidos do sistema.  | `ADMIN`                                 |
| `GET`     | `/api/pedidos/meus`               | Lista os pedidos do cliente logado. | `CLIENTE`                               |
| `GET`     | `/api/pedidos/restaurante`        | Lista os pedidos do restaurante logado. | `RESTAURANTE`                           |
| `GET`     | `/api/pedidos/{id}`               | Busca um pedido por ID.             | `ADMIN` ou Dono (Cliente/Restaurante)   |
| `PATCH`   | `/api/pedidos/{id}/status`        | Atualiza o status de um pedido.     | `ADMIN` ou `RESTAURANTE`                |

---

### Clientes (`/api/clientes`)

| Método    | Endpoint                          | Descrição                           | Permissão                               |
| :-------- | :-------------------------------- | :---------------------------------- | :-------------------------------------- |
| `GET`     | `/api/clientes`                   | Lista todos os clientes.            | **Autenticado**                         |
| `GET`     | `/api/clientes/{id}`              | Busca um cliente por ID.            | **Autenticado**                         |
| `POST`    | `/api/clientes`                   | Cadastra um novo cliente.           | **Autenticado**                         |
| `PUT`     | `/api/clientes/{id}`              | Atualiza um cliente.                | **Autenticado**                         |
| `PATCH`   | `/api/clientes/{id}/toggle-status`| Ativa ou desativa um cliente.       | **Autenticado**                         |

## 🧪 Testando com Insomnia

Uma coleção do Insomnia está disponível em `Collections Insomnia/new-collection-insomnia.yaml` para facilitar os testes. Siga as instruções na documentação do Swagger para obter um token e configure-o na variável de ambiente `jwt_token` do Insomnia.

---
*Desenvolvido por Dállison Silveira Lima*
