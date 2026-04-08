# 🎮 GamerStore - Backend de Microsserviços

## 📌 Visão Geral

Backend de e-commerce construído com **arquitetura de microsserviços orientada a eventos**.

Cada serviço possui responsabilidade bem definida, banco próprio e comunicação **assíncrona via RabbitMQ**, reduzindo acoplamento e aumentando escalabilidade.

---

## 🧠 Arquitetura (visão real do projeto)

### 🔥 Características principais

* Microsserviços desacoplados
* Comunicação assíncrona com eventos
* Banco de dados por serviço
* Integração com serviços externos (Stripe)
* API Gateway centralizando documentação

---

## 🔄 Fluxo real de compra (Event-Driven)

    Client->>CartService: Adiciona produtos ao carrinho

    CartService->>ProductService: Consulta preço real dos produtos
    CartService->>ShippingService: Calcula frete

    Client->>PaymentService: Realiza pagamento

    PaymentService->>RabbitMQ: Publica PaymentConfirmedEvent

    RabbitMQ->>ProductService: Atualiza estoque
    RabbitMQ->>CartService: Limpa carrinho
    RabbitMQ->>OrderService: Cria pedido
    RabbitMQ->>EmailService: Envia email
```

---

## 🧩 Microsserviços

### 🔐 User Service

* Autenticação e gestão de usuários
* Banco: PostgreSQL

---

### 📦 Product Service

* Listagem de produtos
* Atualização de estoque via eventos
* Consome eventos do Payment Service
* Banco: PostgreSQL

---

### 🛒 Cart Service

* Gerenciamento do carrinho
* Banco: Redis
* Consome eventos para limpar carrinho

#### Integrações:

* 🔗 Shipping Service (frete via OpenFeign)
* 🔗 Product Service (validação de preço)

---

### 💳 Payment Service

* Integração com Stripe
* Responsável pelo pagamento
* Publica eventos no RabbitMQ

---

### 📑 Order Service

* Criação de pedidos
* Consome eventos de pagamento
* Banco: MongoDB

---

### 📧 Email Service

* Envio de emails transacionais
* Consome eventos de pagamento confirmado

---

### 🚚 Shipping Service

* Cálculo de frete
* Consumido pelo Cart Service

---

## 📨 Mensageria

* Broker: RabbitMQ
* Comunicação baseada em eventos

### Evento principal

* `PaymentConfirmedEvent`

👉 Esse evento é o coração do sistema:

* Atualiza estoque
* Limpa carrinho
* Cria pedido
* Dispara email

---

## 🌐 API Gateway

* Centraliza documentação dos serviços

---

## 🗄️ Bancos de Dados

| Serviço         | Banco      |
| --------------- | ---------- |
| User Service    | PostgreSQL |
| Product Service | PostgreSQL |
| Cart Service    | Redis      |
| Order Service   | MongoDB    |

---

## 🚀 Deploy

* Deploy via Railway
* Arquitetura preparada para Kubernetes

---

## 🔐 Segurança

* Uso de variáveis de ambiente
* Autenticação com JWT
* Circuit breaker
* Resilience4j

---

## 📈 Próximos Passos

* Observabilidade (logs e métricas)
* Migração para Kubernetes