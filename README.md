# 🔗 URL Compressor API

## Зміст
1. [Вступ](#вступ)
2. [Функції](#функції)
3. [Використані технології](#використані-технології)
4. [Інструкція з встановлення](#інструкція-з-встановлення)
5. [Конфігурація](#конфігурація)
6. [Документація API](#документація-api)
   - [Авторизація користувача](#авторизація-користувача)
   - [Операції з URL](#операції-з-url)
   - [Статистика URL](#статистика-url)
   - [Нотатки](#нотатки)
7. [Безпека](#безпека)
8. [Схема бази даних](#схема-бази-даних)
9. [Ліцензія](#ліцензія)

---

## Вступ
**URL Compressor API** — це RESTful-сервіс для скорочення довгих URL-адрес, перегляду статистики переходів та збереження приватних нотаток. У проєкті реалізовано JWT-авторизацію, ролевий доступ, логування, токен-блекліст та Swagger-документацію.

---

## Функції
- 🔐 JWT-автентифікація з access/refresh токенами
- ✂️ Скорочення та розшифровка URL
- 📈 Перегляд статистики переходів
- ✍️ Приватні нотатки (CRUD лише для власника)
- ⚙️ Розмежування доступу за ролями (USER, MODERATOR, ADMIN)
- 📜 Документація API (Swagger/OpenAPI)
- 🧪 Тестування (MockMvc, Testcontainers)

---

## Використані технології
- Java 21
- Spring Boot 3.4.1
- Spring Security + JWT
- PostgreSQL
- Flyway
- OpenAPI 3 (SpringDoc)
- Maven
- Testcontainers, Mockito, JUnit 5

---

## Інструкція з встановлення

### 1. Клонування репозиторію
```bash
git clone <repository-url>
cd url-compressor
```

### 2. Запуск
```bash
mvn clean install
mvn spring-boot:run
```

---

## Конфігурація

| Змінна                        | Значення                                      |
|------------------------------|-----------------------------------------------|
| `SPRING_DATASOURCE_URL`      | `jdbc:postgresql://localhost:5432/url_db`     |
| `SPRING_DATASOURCE_USERNAME` | `your_db_user`                                |
| `SPRING_DATASOURCE_PASSWORD` | `your_db_pass`                                |
| `JWT_SECRET_KEY`             |  `your_secret_key` (змінна в application.yaml)|

**Порти:**
- Dev: `8080`
- Prod: `9999`

---

## Документація API

Swagger UI:
- Dev: `http://localhost:8080/swagger-ui/index.html`
- Prod: `http://localhost:9999/swagger-ui/index.html`

### Авторизація користувача

**POST /api/v1/user/signin**
```json
{ "login": "user", "password": "12345678" }
```
→ Response:
```json
{ "accessToken": "...", "refreshToken": "..." }
```

**POST /api/v1/auth/refresh**
```json
{ "refreshToken": "..." }
```

**POST /api/v1/user/logout**  
Authorization: Bearer accessToken

---

### Операції з URL

**POST /api/v1/link/shorten**
```json
{ "originalUrl": "https://example.com" }
```

**POST /api/v1/link/expand**
```json
{ "originalUrl": "abc123" }
```

**GET /api/v1/user/links**  
→ список коротких посилань користувача

---

### Статистика URL

**POST /api/v1/statistics/{shortUrl}**
```json
{ "originalUrl": "abc123" }
```
→
```json
{ "shortUrl": "abc123", "clicks": 12 }
```

---

### Нотатки

**POST /api/v1/notes**
```json
{ "title": "Моя нотатка", "content": "Це приватна нотатка" }
```

**GET /api/v1/notes**  
→ Список усіх нотаток поточного користувача

**PUT /api/v1/notes/{id}**
```json
{ "title": "Оновлений заголовок", "content": "Новий зміст" }
```

**DELETE /api/v1/notes/{id}**

---

## Безпека

Усі захищені ендпоінти вимагають JWT:
```
Authorization: Bearer <token>
```

- Access токен — для доступу до ресурсів
- Refresh токен — для оновлення
- Logout — додає токен у blacklist
- Захист доступу реалізовано через ролі

---

## Схема бази даних

### Таблиця `users`
| Поле     | Тип         | Примітка       |
|----------|-------------|----------------|
| id       | BIGSERIAL   | PRIMARY KEY    |
| login    | VARCHAR     | UNIQUE         |
| password | VARCHAR     | зашифровано    |

### Таблиця `short_links`
| Поле        | Тип         | Примітка         |
|-------------|-------------|------------------|
| id          | BIGSERIAL   | PRIMARY KEY      |
| original    | TEXT        |                  |
| alias       | VARCHAR     | унікальний alias |
| created_on  | TIMESTAMP   |                  |
| clicks      | BIGINT      |                  |
| owner_id    | BIGINT      | FK → users.id    |

---

## Ліцензія

Проєкт відкритий і доступний за ліцензією **MIT**.

# 🔗 URL Compressor API

## Table of Contents
1. [Introduction](#introduction)
2. [Features](#features)
3. [Technologies Used](#technologies-used)
4. [Installation Guide](#installation-guide)
5. [Configuration](#configuration)
6. [API Documentation](#api-documentation)
   - [Authentication](#authentication)
   - [URL Operations](#url-operations)
   - [URL Statistics](#url-statistics)
7. [Security](#security)
8. [Database Schema](#database-schema)
9. [License](#license)

---

## Introduction
**URL Compressor API** is a RESTful service designed to shorten long URLs, track click statistics, and manage private notes. The project implements JWT authentication, role-based access control, logging, token blacklisting, and Swagger documentation.

---

## Features
- 🔐 JWT authentication with access and refresh tokens
- ✂️ URL shortening and decoding
- 📈 Click statistics tracking
- ✍️ Private notes (CRUD operations for the owner)
- ⚙️ Role-based access control (USER, MODERATOR, ADMIN)
- 📜 API documentation (Swagger/OpenAPI)
- 🧪 Testing with MockMvc and Testcontainers

---

## Technologies Used
- Java 21
- Spring Boot 3.4.1
- Spring Security + JWT
- PostgreSQL
- Flyway
- OpenAPI 3 (SpringDoc)
- Maven
- Testcontainers, Mockito, JUnit 5

---

## Installation Guide

### 1. Clone the Repository
```bash
git clone <repository-url>
cd url-compressor
```

### 2. Run the Application
```bash
mvn clean install
mvn spring-boot:run
```

---

## Configuration

| Variable         | Value                                      |
|------------------|--------------------------------------------|
| `DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/url_db`  |
| `DB_USERNAME`    | `your_db_user`                             |
| `DB_PASSWORD`    | `your_db_pass`                             |
| `JWT_SECRET_KEY` | `your_secret_key`                          |

**Ports:**
- Development: `8080`
- Production: `9999`

---

## API Documentation

Swagger UI:
- Development: `http://localhost:8080/swagger-ui/index.html`
- Production: `http://localhost:9999/swagger-ui/index.html`

### Authentication

**POST /api/v1/auth/login**
```json
{ "login": "user", "password": "12345678" }
```
→ Response:
```json
{ "accessToken": "...", "refreshToken": "..." }
```

**POST /api/v1/auth/refresh**
```json
{ "refreshToken": "..." }
```

**POST /api/v1/auth/logout**  
Authorization: Bearer accessToken

---

### URL Operations

**POST /api/v1/links**
```json
{ "originalUrl": "https://example.com" }
```

**GET /api/v1/links/decode?shortUrl=abc123**  
→ `{ "originalUrl": "https://example.com" }`

**GET /api/v1/user/links**  
→ List of user's shortened URLs

---

### URL Statistics

**GET /api/v1/statistics/{shortUrl}**
```json
{ "shortUrl": "abc123", "clicks": 12 }
```

---

## 🔄 Request Examples

#### Create a Note
**POST /api/v1/notes**
```json
{ "title": "My Note", "content": "This is a private note" }
```

#### Retrieve User's Notes
**GET /api/v1/notes**  
→ List of all notes for the current user

#### Update a Note
**PUT /api/v1/notes/{id}**
```json
{ "title": "Updated Title", "content": "New content" }
```

#### Delete a Note
**DELETE /api/v1/notes/{id}**

---

## Security

All protected endpoints require JWT:
```
Authorization: Bearer <token>
```

- Access token — for resource access
- Refresh token — for obtaining new tokens
- Logout — adds the token to a blacklist
- Access control is enforced via roles

---

## Database Schema

### Table `users`
| Field    | Type       | Note          |
|----------|------------|---------------|
| id       | BIGSERIAL  | PRIMARY KEY   |
| login    | VARCHAR    | UNIQUE        |
| password | VARCHAR    | Encrypted     |

### Table `short_links`
| Field      | Type       | Note               |
|------------|------------|--------------------|
| id         | BIGSERIAL  | PRIMARY KEY        |
| original   | TEXT       |                    |
| alias      | VARCHAR    | Unique alias       |
| created_on | TIMESTAMP  |                    |
| clicks     | BIGINT     |                    |
| owner_id   | BIGINT     | FK → users.id      |

---

## License

This project is open-source and available under the **MIT License**.