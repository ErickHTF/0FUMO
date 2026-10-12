# 0FUMO — Backend

Spring Boot REST API for the smoking cessation application.

## Prerequisites

- Java 21
- Docker Desktop

## Getting Started

**1. Start the database** (from the repository root)

```bash
docker compose up -d
```

Starts PostgreSQL 13 on port 5433.

**2. Run the API** (from this folder)

```bash
./mvnw spring-boot:run
```

The API is available at `http://localhost:8080`.

## Tests

The integration tests use the PostgreSQL from `docker compose`, so the database needs to be up (step 1).

```bash
./mvnw test
```

## Endpoints

| Method | Route | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | No | Sign up |
| POST | `/api/auth/login` | No | Log in |
| POST | `/api/users/{id}/assessment` | Yes | Initial assessment |
| POST | `/api/events` | Yes | Log an event |
| GET  | `/api/events` | Yes | List events |

Authenticated routes require an `Authorization: Bearer <token>` header.
