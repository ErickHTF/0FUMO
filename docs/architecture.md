# Architecture

[← Back to README](../README.md)

## Source layout

| Folder                          | Responsibility                                                       |
| ------------------------------- | -------------------------------------------------------------------- |
| [Backend](../backend/)          | Spring Boot REST API, authentication, business rules and persistence |
| [Frontend](../frontend/)        | HTML pages, shared CSS and a JavaScript API client                   |
| [Academic documents](README.md) | Requirements, analysis, tests and course records                     |

The backend groups controllers, DTOs, services, entities, repositories, security
and exception handling under `br.edu.ifsp.zerofumo`.

The frontend stores the session in browser local storage. Its shared API client
adds the JWT bearer token to requests. After registration, users complete an
assessment before opening the protected tracking features.

## Design diagrams

The [PlantUML source diagrams](../backend/diagrams/) cover the architecture,
progress monitoring, event registration and relaxation resource suggestions.
The academic analysis models are linked from the [documentation index](README.md).
