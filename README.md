## 0FUMO – Plataforma de Apoio à Cessação do Tabagismo
> Este projeto é parte da disciplina de **Linguagem de Programação 1**, com foco em aplicar conceitos de análise de requisitos, modelagem e desenvolvimento de sistemas voltados para problemas reais.


## Problemas que Resolvemos ##

Muitas pessoas enfrentam dificuldades ao tentar parar de fumar, principalmente devido à dependência física e aos fatores emocionais envolvidos, como estresse, ansiedade e hábitos do dia a dia. Além disso, a ausência de acompanhamento contínuo e de ferramentas práticas dificulta a identificação de gatilhos e aumenta a probabilidade de recaídas. Outro problema comum é a baixa adesão a soluções existentes, que muitas vezes são complexas ou pouco acessíveis no cotidiano do usuário.

## Solução ##
Nesse contexto, surge a necessidade de **ferramentas que possam auxiliar indivíduos no processo de cessação do tabagismo, oferecendo acompanhamento, motivação e suporte durante a jornada.**

## Objetivo do Sistema ##
O objetivo do sistema é fornecer uma plataforma digital que auxilie pessoas no processo de parar de fumar, oferecendo ferramentas de acompanhamento, motivação e suporte comportamental.
O sistema permitirá:

- Monitorar o progresso do usuário sem fumar
- Apresentar estatísticas de saúde relacionadas ao tempo sem fumar
- Registrar momentos de desejo de fumar para identificar padrões
- Oferecer recursos de relaxamento para controle da ansiedade
- Auxiliar o usuário na decisão de parar de fumar por meio de avaliação inicial

Dessa forma, o sistema busca aumentar as chances de sucesso no abandono do tabagismo.

## Meta de Beneficios ##

- Aumento das chances de sucesso no abandono do tabagismo  
- Melhora da qualidade de vida e da saúde dos usuários  
- Redução de custos associados ao tratamento de doenças relacionadas ao tabaco  
- Reduzir a frequência de recaídas

## Público-Alvo ##
- Indivíduos que desejam parar de fumar  
- Profissionais de saúde que buscam ferramentas de apoio

## Estrutura do Repositório ##

| Pasta | Conteúdo |
|---|---|
| [`backend/`](backend) | API REST em Spring Boot 4 (Java 21, JWT, PostgreSQL) |
| [`frontend/`](frontend) | Interface web em HTML/CSS/JS puro |
| [`docs/`](docs) | Requisitos, casos de uso, modelos de análise, roteiro de teste, guias e gerenciamento |

## Como Rodar ##

Com Docker, sobe banco, API e frontend de uma vez:

```bash
docker compose --profile app up -d --build
```

- Frontend: http://localhost:3000
- API: http://localhost:8080/api

Para desenvolvimento, sobe só o banco e roda a API e o front localmente. O passo a passo está no [Guia de Implantação](DEPLOY.md).
