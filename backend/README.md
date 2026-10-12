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
| POST | `/api/auth/register` | No | Sign up (`RegisterRequestDTO`: `name`, `email`, `password` min. 8 chars). Returns `201` with token and user |
| POST | `/api/auth/login` | No | Log in (`LoginRequestDTO`: `email`, `password`). Returns token and user |
| GET | `/api/users/me` | Yes | Current user |
| GET | `/api/users/{id}` | Yes | Get user by id |
| PUT | `/api/users/{id}` | Yes | Update user (`UpdateUserDTO`: `name`, `email`) |
| DELETE | `/api/users/{id}` | Yes | Delete user. Returns `204` |
| POST | `/api/users/{id}/assessment` | Yes | Initial assessment (`AssessmentRequestDTO`: `cigsPerDay`, `smokingYears`, `motivation`, `dependencyLevel`) |
| POST | `/api/events` | Yes* | Log an event (`EventRequestDTO`: `type`, `intensity`, `trigger`, `notes` optional, `occurredAt`). Returns `201` |
| GET | `/api/events` | Yes* | List the current user's events |
| GET | `/api/progress` | Yes* | Progress stats (`ProgressStatsDTO`: totals by type, intensity and trigger, last event, days tracking) |
| GET | `/api/relaxation-resources` | Yes* | List relaxation resources |
| GET | `/api/relaxation-resources?trigger={trigger}` | Yes* | Relaxation resources for a trigger |
| GET | `/api/features` | Yes* | Checks whether the app features are unlocked |

Authenticated routes require an `Authorization: Bearer <token>` header.

\* Also requires the initial assessment to be completed; otherwise returns `403`.

Common error responses: `400` validation error (field → message), `401` missing/invalid token or wrong credentials, `403` assessment not completed, `404` user not found, `409` e-mail already registered.
