# 0FUMO — Frontend

Interface web em HTML/CSS/JS puro para a aplicação de cessação do tabagismo.

## Pré-requisitos

- Backend rodando em `http://localhost:8080` (ver repo do backend)
- Node.js (opcional, para servir via `npx serve`)

## Como rodar

**Opção 1 — npx serve**

```bash
npx serve .
```

Abre no endereço indicado no terminal (geralmente `http://localhost:3000`).


## Páginas

| Arquivo | Descrição |
|---|---|
| `index.html` | Landing page |
| `register.html` | Cadastro |
| `login.html` | Login |
| `assessment.html` | Avaliação inicial |
| `dashboard.html` | Painel do usuário |
| `eventos.html` | Registrar e listar eventos |

## Observação

A URL da API está definida na primeira linha de `js/api.js`:

```js
const API_BASE = 'http://localhost:8080/api';
```

Altere se o backend estiver em outro endereço.