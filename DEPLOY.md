# Local Deployment Guide — 0FUMO

## Prerequisites

- [Java 21](https://adoptium.net)
- [Docker Desktop](https://www.docker.com/products/docker-desktop)
- [Node.js](https://nodejs.org) (to serve the frontend)
- Git

---

## 1. Clone the repository

```bash
git clone https://github.com/ErickHTF/0FUMO.git
cd 0FUMO
```

> Shortcut: `docker compose --profile app up -d --build` brings up the database, backend and frontend in containers, with no need for Java or Node. The steps below are for running in development mode.

---

## 2. Start the database

From the repository root:

```bash
docker compose up -d
```

This starts PostgreSQL 13 on port **5433** with the following settings:

| Parameter | Value |
|---|---|
| Host | `localhost:5433` |
| Database | `zerofumo_DB` |
| User | `zerofumo_user` |
| Password | `zerofumo_pass` |

To check that it's up:

```bash
docker ps
```

---

## 3. Run the backend

From the backend folder:

```bash
cd backend
./mvnw spring-boot:run
```

Wait for `Started ZeroFumoApplication` to show up in the terminal. The API is available at `http://localhost:8080`.

---

## 4. Run the frontend

In another terminal, from the frontend folder:

```bash
cd frontend
npx serve .
```

Open your browser at `http://localhost:3000`.

---

## Startup order

```
Docker (database) → Backend (port 8080) → Frontend (port 3000)
```

The frontend doesn't work without the backend running.

---

## Stopping everything

```bash
# Backend: Ctrl+C in its terminal

# Database (from the root):
docker compose down

# If you started everything via Docker:
docker compose --profile app down
```
