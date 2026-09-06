# ShopSphere

[![Java](https://img.shields.io/badge/Java-26%2B-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-Latest-DC382D?logo=redis&logoColor=white)](https://redis.io/)
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-4--management-FF6600?logo=rabbitmq&logoColor=white)](https://www.rabbitmq.com/)

**ShopSphere** is an enterprise-ready, modular e-commerce RESTful API backend engineered with **Spring Boot**, **Spring Security (Asymmetric RSA JWT)**, **PostgreSQL**, **Redis**, and **RabbitMQ**. It implements modern architectural best practices including the **Transactional Outbox Pattern** for reliable asynchronous messaging, **Virtual Threads** for high-throughput I/O operations, automated database migrations with **Flyway**, and integrated third-party services like **Stripe** (payments) and **Cloudinary** (media management).

---

## Table of Contents

- [Features](#features)
- [Architecture & Design Highlights](#architecture--design-highlights)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Environment Variables](#environment-variables)
- [Setup & Running](#setup--running)
  - [1. Clone Repository](#1-clone-the-repository)
  - [2. Configure Environment](#2-configure-environment-variables)
  - [3. Start Infrastructure (Docker Compose)](#3-start-infrastructure-services)
  - [4. Build & Run Application](#4-build-and-run-the-application)
- [Database Migrations & Seeding](#database-migrations--seeding)
- [API Endpoints & Documentation](#api-endpoints--documentation)
  - [OpenAPI / Swagger UI](#openapi--swagger-ui)
  - [Key API Routes](#key-api-routes)
- [Testing](#testing)
- [Common Scripts & Commands](#common-scripts--commands)
- [TODOs & Roadmap](#todos--roadmap)

---

## Features

- **Authentication & Authorization**:
  - Asymmetric RSA (PKCS#8 / X.509) signed JSON Web Tokens (Access + Refresh tokens).
  - Role-Based Access Control (RBAC) with granular permissions (`ROLE_ADMIN`, `ROLE_CUSTOMER`, etc.).
  - Secure registration, email verification codes, disposable email domain blocking, and password encryption (BCrypt).
- **Product & Category Catalog**:
  - Hierarchical categories with full CRUD.
  - Multi-part product creation with asynchronous Cloudinary image uploading.
  - Paginated filtering and searching.
- **Shopping Cart**:
  - Persistent user cart management (add, update quantities, delete, clear).
- **Checkout & Inventory**:
  - Atomic stock validation and deduction.
  - Order generation from cart with snapshot of product pricing and shipping addresses.
- **Payment Processing**:
  - Stripe Checkout Session creation and customer redirection.
  - Secure webhook endpoint (`/webhooks/stripe`) with cryptographic signature validation to handle payment fulfillment.
- **Asynchronous & Event-Driven Architecture**:
  - **Transactional Outbox Pattern**: Outbox events persisted within PostgreSQL transactions to guarantee at-least-once delivery.
  - **RabbitMQ Integration**: Poller dispatching verification email events to RabbitMQ queues consumed by mail listeners.
  - **Java Virtual Threads**: High-concurrency background execution for resource-heavy operations like image uploads.
- **Auditing & Database Versioning**:
  - Spring Data JPA entity auditing (`created_by`, `created_date`, `last_modified_date`).
  - Flyway versioned migrations (`V1` to `V9`).
  - Automatic seed data on startup (Default Admin user and sample product catalog with Cloudinary uploads).

---

## Architecture & Design Highlights

```
                          ┌────────────────────────┐
                          │   Client / Frontend    │
                          └───────────┬────────────┘
                                      │ HTTP / REST
                                      ▼
               ┌──────────────────────────────────────────────┐
               │          Spring Boot REST Controllers        │
               │   (Spring Security / Asymmetric RSA JWT)     │
               └───────┬──────────────┬──────────────┬────────┘
                       │              │              │
                       ▼              ▼              ▼
                ┌────────────┐ ┌────────────┐ ┌─────────────┐
                │ PostgreSQL │ │   Redis    │ │ Cloudinary  │
                │ (Flyway)   │ │  (Cache /  │ │  (Product   │
                │  - Data    │ │   State)   │ │   Images)   │
                │  - Outbox  │ └────────────┘ └─────────────┘
                └──────┬─────┘
                       │ Polled by OutboxPoller (3s)
                       ▼
                ┌────────────┐
                │  RabbitMQ  │ ──► VerificationEmailConsumer ──► SMTP Mailer
                └────────────┘
```

---

## Tech Stack

| Category | Technology |
| :--- | :--- |
| **Language** | Java 26 / 21+ (with Virtual Threads enabled) |
| **Framework** | Spring Boot 4.0.5 |
| **Security** | Spring Security 6, JJWT 0.13.0 (RSA 2048-bit Asymmetric Keys) |
| **Database & ORM** | PostgreSQL 17, Spring Data JPA, Hibernate, HikariCP |
| **Database Migrations** | Flyway (`flyway-database-postgresql`, `flyway-core`) |
| **Caching & In-Memory** | Redis (`spring-boot-starter-data-redis`) |
| **Messaging & Events** | RabbitMQ 4 (AMQP), Transactional Outbox Pattern |
| **Payments** | Stripe Java SDK (v33.0.0) |
| **Media Storage** | Cloudinary Java SDK (`cloudinary-http5` v2.3.2) |
| **Email & Templating** | Spring Mail (JavaMailSender), Thymeleaf |
| **API Documentation** | Springdoc OpenAPI 3.0.2 (Swagger UI) |
| **Build & Utilities** | Apache Maven, Lombok |

---

## Project Structure

```
ShopSphere/
├── pom.xml                               # Maven project definition and dependencies
├── mvnw / mvnw.cmd                       # Maven Wrapper scripts
├── docker-compose.yml                    # Docker services (PostgreSQL, Redis, RabbitMQ)
├── src/
│   ├── main/
│   │   ├── java/com/khaled/shopsphere/
│   │   │   ├── ShopSphereApplication.java # Application entry point & Admin Seeder
│   │   │   ├── address/                  # User address management
│   │   │   ├── auth/                     # Authentication, Registration & Verification
│   │   │   │   ├── consumer/             # RabbitMQ email message consumers
│   │   │   │   └── message/              # AMQP message payloads
│   │   │   ├── cart/                     # Shopping cart services & controllers
│   │   │   ├── category/                 # Product category management
│   │   │   ├── checkout/                 # Checkout orchestration
│   │   │   ├── common/                   # Shared response wrappers (e.g. PageResponse)
│   │   │   ├── config/                   # Spring configurations (Async, Beans, OpenAPI, RabbitMQ, Redis)
│   │   │   ├── email/                    # SMTP email delivery & Thymeleaf templates
│   │   │   ├── exception/                # Centralized exception hierarchy & handlers
│   │   │   ├── inventory/                # Product stock validation & deduction
│   │   │   ├── order/                    # Order processing, items & order auditing
│   │   │   ├── outbox/                   # Transactional Outbox poller & repository
│   │   │   ├── payment/                  # Stripe integration & webhook handler
│   │   │   ├── product/                  # Product catalog, images & Cloudinary service
│   │   │   ├── role/                     # Roles & permissions
│   │   │   ├── security/                 # JWT filter, Entry point, Key loader & UserDetailsService
│   │   │   ├── seed/                     # Product catalog automated seeder
│   │   │   └── user/                     # User entity, repository & verification tokens
│   │   └── resources/
│   │       ├── application.yaml          # Main application configuration
│   │       ├── db/migration/             # Flyway SQL migrations (V1 to V9)
│   │       ├── keys/local-only/          # Local RSA public and private keys
│   │       ├── seed/products/            # Sample seed product images
│   │       └── templates/email/          # Thymeleaf HTML email templates
│   └── test/
│       └── java/com/khaled/shopsphere/   # Unit & Integration test suite
└── target/                               # Compiled artifacts
```

---

## Prerequisites

Ensure the following tools are installed on your workstation:

- **JDK 21+** or **JDK 26**
- **Docker** and **Docker Compose**
- **Apache Maven 3.9+** (or use `./mvnw`)
- **Stripe Account** (for API testing)
- **Cloudinary Account** (for image storage)
- **SMTP Provider** (e.g., Gmail App Password)

---

## Environment Variables

The application is configured to load environment variables directly from the OS environment or from a `.env` file at the project root (`spring.config.import: optional:file:.env[.properties]`).

Create a `.env` file in the repository root:

```env
# ==========================================
# Database Configuration (PostgreSQL)
# ==========================================
DB_URL=localhost
DB_PORT=5432
DB_NAME=shopsphere_db
DB_USERNAME=postgres
DB_PASSWORD=postgres

# ==========================================
# Initial Super Admin Account (Auto-seeded)
# ==========================================
ADMIN_EMAIL=admin@shopsphere.com
ADMIN_PASSWORD=AdminSecurePassword123!

# ==========================================
# SMTP Email Configuration (Gmail / Mailtrap)
# ==========================================
EMAIL_USERNAME=your_email@gmail.com
EMAIL_PASSWORD=your_gmail_app_password

# ==========================================
# Cloudinary Configuration (Image Uploads)
# ==========================================
CLOUDINARY_CLOUD_NAME=your_cloudinary_cloud_name
CLOUDINARY_API_KEY=your_cloudinary_api_key
CLOUDINARY_API_SECRET=your_cloudinary_api_secret

# ==========================================
# Stripe Payment Configuration
# ==========================================
STRIPE_SECRET_KEY=sk_test_51...
STRIPE_WEBHOOK_SECRET=whsec_...
STRIPE_SUCCESS_URL=http://localhost:3000/checkout/success?session_id={CHECKOUT_SESSION_ID}
STRIPE_CANCEL_URL=http://localhost:3000/checkout/cancel
```

### Configuration Property Reference

| Variable | Description | Default / Example |
| :--- | :--- | :--- |
| `DB_URL` | PostgreSQL Hostname | `localhost` |
| `DB_PORT` | PostgreSQL Port | `5432` |
| `DB_NAME` | Database Name | `shopsphere_db` |
| `DB_USERNAME` | Database User | `postgres` |
| `DB_PASSWORD` | Database Password | `postgres` |
| `ADMIN_EMAIL` | Seed Admin email address | `admin@shopsphere.com` |
| `ADMIN_PASSWORD`| Seed Admin password | Strong password |
| `EMAIL_USERNAME`| SMTP Sender username / email | `your_email@gmail.com` |
| `EMAIL_PASSWORD`| SMTP Sender password / app token | Gmail App Password |
| `CLOUDINARY_CLOUD_NAME` | Cloudinary account name | — |
| `CLOUDINARY_API_KEY`    | Cloudinary API Key | — |
| `CLOUDINARY_API_SECRET` | Cloudinary API Secret | — |
| `STRIPE_SECRET_KEY`     | Stripe Secret Key | `sk_test_...` |
| `STRIPE_WEBHOOK_SECRET` | Stripe Webhook Signing Secret | `whsec_...` |
| `STRIPE_SUCCESS_URL`    | Redirect URL upon successful payment | Frontend success page |
| `STRIPE_CANCEL_URL`     | Redirect URL upon cancelled payment | Frontend cancel page |

---

## Setup & Running

### 1. Clone the Repository

```bash
git clone https://github.com/KhaledMahmoud13/ShopSphere.git
cd ShopSphere
```

### 2. Configure Environment Variables

Create and populate `.env`:

```bash
cp .env.example .env   # Or create .env with variables listed above
```

### 3. Start Infrastructure Services

Use Docker Compose to launch PostgreSQL, Redis, and RabbitMQ:

```bash
docker compose up -d
```

Verify that containers are healthy:

```bash
docker compose ps
```

| Service | Host Port | Management UI / Notes |
| :--- | :--- | :--- |
| **PostgreSQL** | `5432` | Stores core business entities & outbox events |
| **Redis** | `6379` | Session / Caching storage |
| **RabbitMQ** | `5672` (AMQP) / `15672` (HTTP) | Management Web UI: `http://localhost:15672` (`guest`/`guest`) |

### 4. Build and Run the Application

Run directly using Maven:

```bash
./mvnw spring-boot:run
```

Or package the JAR and execute:

```bash
./mvnw clean package -DskipTests
java -jar target/ShopSphere-0.0.1-SNAPSHOT.jar
```

The application starts on port `8080` by default.

---

## Database Migrations & Seeding

### Flyway Migrations
Flyway runs automatically on startup (`spring.flyway.enabled: true`), executing scripts located in `src/main/resources/db/migration/`:
- `V1`: Create users, roles, and permissions tables.
- `V2`: Seed predefined roles (`ROLE_ADMIN`, `ROLE_CUSTOMER`, etc.) and permissions.
- `V3`: Product module schema (products, product images).
- `V4`: Order module schema (orders, order items, order addresses).
- `V5`: Cart module schema (carts, cart items).
- `V6`: Payment module schema.
- `V7`: User address module schema.
- `V8`: Transactional outbox events schema (`outbox_events`).
- `V9`: Category module schema.

### Automated Seeders
1. **Admin Seeder** (`ShopSphereApplication.java`): If no user exists with `ADMIN_EMAIL`, an admin account is created with `ROLE_ADMIN` and verified credentials.
2. **Product Seeder** (`ProductSeeder.java`): If product count is `0`, initial categories (Electronics, Accessories, Gaming, Laptops) and products are seeded with images uploaded to Cloudinary.

---

## API Endpoints & Documentation

### OpenAPI / Swagger UI

Interactive API documentation and schema explorer are available once the application is running:

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) or [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **OpenAPI JSON Docs**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### Key API Routes

#### 🔐 Authentication (`/api/v1/auth`)
- `POST /api/v1/auth/register` — Register a new customer account.
- `POST /api/v1/auth/login` — Authenticate and receive Access + Refresh JWT tokens.
- `POST /api/v1/auth/verify-email` — Verify email via 6-digit verification code.
- `POST /api/v1/auth/send-code` — Resend verification code.
- `POST /api/v1/auth/refresh` — Issue a new access token using a valid refresh token.

#### 🏷️ Categories (`/api/v1/categories`)
- `GET /api/v1/categories` — List all categories (Public).
- `GET /api/v1/categories/{categoryId}` — Get category by ID (Public).
- `POST /api/v1/categories` — Create category (`ROLE_ADMIN` / `admin:access`).
- `PUT /api/v1/categories/{categoryId}` — Update category (`ROLE_ADMIN` / `admin:access`).
- `DELETE /api/v1/categories/{categoryId}` — Delete category (`ROLE_ADMIN` / `admin:access`).

#### 📦 Products (`/api/v1/products`)
- `GET /api/v1/products` — Filter & list products with pagination (Public).
- `POST /api/v1/products` — Create product with multipart image uploads (`ROLE_ADMIN` / `admin:access`).

#### 🛒 Shopping Cart (`/api/v1/cart`)
- `GET /api/v1/cart` — View current authenticated user's cart (`cart:read`).
- `POST /api/v1/cart/items` — Add item to cart (`cart:write`).
- `PATCH /api/v1/cart/items/{productId}` — Update item quantity in cart (`cart:write`).
- `DELETE /api/v1/cart/items/{productId}` — Remove item from cart (`cart:write`).
- `DELETE /api/v1/cart` — Clear cart (`cart:write`).

#### 📍 User Addresses (`/api/v1/address`)
- `GET /api/v1/address` — List user addresses (`address:read`).
- `GET /api/v1/address/{addressId}` — Get address by ID (`address:read`).
- `POST /api/v1/address` — Add new address (`address:write`).
- `PUT /api/v1/address/{addressId}` — Update address (`address:write`).
- `PATCH /api/v1/address/{addressId}/default` — Set default address (`address:write`).
- `DELETE /api/v1/address/{addressId}` — Delete address (`address:write`).

#### 💳 Checkout & Payments (`/api/v1/checkout`, `/api/v1/payments`)
- `POST /api/v1/checkout` — Place order from current cart (`checkout:create`).
- `GET /api/v1/payments/session/{orderId}` — Get or create Stripe Checkout Session URL (`payment:create`).
- `POST /webhooks/stripe` — Stripe Webhook receiver for event fulfillment (`checkout.session.completed`, etc.).

#### 📋 Orders (`/api/v1/orders`)
- `GET /api/v1/orders` — List authenticated user's orders (`order:read`).
- `GET /api/v1/orders/{orderId}` — Get order details (Order owner only).
- `PATCH /api/v1/orders/{orderId}/cancel` — Cancel an order (Order owner only).
- `PATCH /api/v1/orders/{orderId}/status` — Update order status (`admin:access`).
- `GET /api/v1/orders/admin` — List all orders across system with filtering (`admin:access`).

---

## Common Scripts & Commands

| Task | Command |
| :--- | :--- |
| **Start Docker Infrastructure** | `docker compose up -d` |
| **Stop Docker Infrastructure** | `docker compose down` |
| **Wipe Database Volumes** | `docker compose down -v` |
| **Compile & Verify Project** | `./mvnw clean compile` |
| **Run Application Locally** | `./mvnw spring-boot:run` |
| **Run Test Suite** | `./mvnw test` |
| **Forward Stripe Webhooks (CLI)** | `stripe listen --forward-to localhost:8080/webhooks/stripe` |
| **Tail Application Logs** | `./mvnw spring-boot:run` |

---

## TODOs & Roadmap

- [ ] **Token Revocation Blacklist**: Store revoked JWTs / logged-out tokens in Redis with TTL.
- [ ] **Elasticsearch / OpenSearch Integration**: Advanced full-text search and faceted filtering for large product catalogs.
- [ ] **Order Tracking & Notifications**: Push notification/SMS updates for order lifecycle events (Dispatched, Delivered).
- [ ] **Multi-Currency & Tax Engine**: Dynamic currency conversion and automated VAT/sales tax calculations.
- [ ] **CI/CD Pipeline**: GitHub Actions workflow for automated testing, linting, Docker image build, and deployment.
