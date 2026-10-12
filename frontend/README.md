# 0FUMO — Frontend

Plain HTML/CSS/JS web interface for the smoking cessation application.

## Prerequisites

- Backend running at `http://localhost:8080` (see [`backend/`](../backend))
- Node.js (optional, to serve via `npx serve`) or Docker

## Getting Started

**Option 1 — npx serve**

```bash
npx serve .
```

Open the address shown in the terminal (usually `http://localhost:3000`).

**Option 2 — Docker** (from the repository root)

```bash
docker compose --profile app up -d --build
```

Serves this folder with nginx at `http://localhost:3000`, together with the database and the backend.

## Pages

| File | Description |
|---|---|
| `index.html` | Landing page |
| `register.html` | Sign up |
| `login.html` | Log in |
| `assessment.html` | Initial assessment |
| `dashboard.html` | User dashboard |
| `eventos.html` | Log and list events |

## Note

The API URL is defined on the first line of `js/api.js`:

```js
const API_BASE = 'http://localhost:8080/api';
```

Change it if the backend runs at a different address.
