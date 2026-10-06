# Guia de implantação local — 0FUMO

## Pré-requisitos

- [Java 21](https://adoptium.net)
- [Docker Desktop](https://www.docker.com/products/docker-desktop)
- [Node.js](https://nodejs.org) (para servir o frontend)
- Git

---

## 1. Clonar o repositório

```bash
git clone https://github.com/ErickHTF/0FUMO.git
cd 0FUMO
```

> Atalho: `docker compose --profile app up -d --build` sobe banco, backend e frontend em containers, sem precisar de Java nem Node. Os passos abaixo são para rodar em modo de desenvolvimento.

---

## 2. Subir o banco de dados

Na raiz do repositório:

```bash
docker compose up -d
```

Isso sobe um PostgreSQL 13 na porta **5433** com as configurações:

| Parâmetro | Valor |
|---|---|
| Host | `localhost:5433` |
| Banco | `zerofumo_DB` |
| Usuário | `zerofumo_user` |
| Senha | `zerofumo_pass` |

Para verificar se subiu:

```bash
docker ps
```

---

## 3. Rodar o backend

Na pasta do backend:

```bash
cd backend
./mvnw spring-boot:run
```

Aguarda aparecer `Started ZerofumoApplication` no terminal. A API fica em `http://localhost:8080`.

---

## 4. Rodar o frontend

Em outro terminal, na pasta do frontend:

```bash
cd frontend
npx serve .
```

Abre o navegador em `http://localhost:3000`.

---

## Ordem de inicialização

```
Docker (banco) → Backend (porta 8080) → Frontend (porta 3000)
```

O frontend não funciona sem o backend rodando.

---

## Parar tudo

```bash
# Para o backend: Ctrl+C no terminal dele

# Para o banco (na raiz):
docker compose down

# Se subiu tudo via Docker:
docker compose --profile app down
```