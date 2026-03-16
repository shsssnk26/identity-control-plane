# Identity Control Plane

A multi-service security platform for authentication, authorization, audit logging, and secure access control for internal services.

## Project Goal

This project is being built as a portfolio-grade security engineering system to demonstrate end-to-end software development skills across:

- backend service design
- authentication and authorization
- database schema management
- secure token handling
- audit logging
- service-to-service security
- Dockerized local development
- CI/CD and deployment foundations

## Planned Architecture

The system is organized as a multi-module monorepo with the following services:

- **auth-service**  
  Handles user authentication, token issuance, refresh token management, and later MFA and SSO.

- **authz-service**  
  Handles centralized authorization decisions using RBAC and simple ABAC policies.

- **document-service**  
  A sample protected business service that consumes the identity platform.

- **audit-service**  
  Stores and exposes audit/security events across the platform.

- **shared**  
  Common DTOs, utilities, errors, and shared helpers used by multiple services.

## Current Status

Completed so far:

- repository scaffold created
- Gradle multi-module build configured
- Spring Boot service skeletons created
- PostgreSQL running locally via Docker Compose
- `auth-service` successfully connected to Postgres
- Flyway configured and working
- initial auth schema created
- `users` and `roles` tables created

## Repository Structure

```text
identity-control-plane/
├── settings.gradle
├── build.gradle
├── gradle.properties
├── README.md
├── docs/
├── infra/
├── shared/
├── auth-service/
├── authz-service/
├── document-service/
├── audit-service/
└── .github/

## Tech Stack

- **Language:** Java 21
- **Framework:** Spring Boot
- **Build Tool:** Gradle
- **Database:** PostgreSQL
- **Database Migrations:** Flyway
- **Container Runtime:** Docker / Docker Compose
- **Future CI/CD:** GitHub Actions

## Design Decisions

### Monorepo
A monorepo was chosen to simplify development, shared code, documentation, and CI setup for a solo-built platform project.

### Service Split
The project separates:
- authentication
- authorization
- business resource access
- audit logging

This keeps security concerns distinct and closer to a real platform architecture.

### Database Strategy
The project uses PostgreSQL with a schema-per-service approach inside a single Postgres instance.

### Token Strategy
The initial version uses:
- JWT access tokens
- opaque refresh tokens

Later versions may add opaque access tokens and introspection.

### Deployment Strategy
Development starts with Docker Compose for local orchestration. Kubernetes will be added later as a production-style deployment phase.

## Local Development

### Prerequisites

- Java 21
- Docker Desktop
- Gradle wrapper support

### Start PostgreSQL

```bash
docker compose -f infra/docker-compose.yml up -d
### Run auth-service

```bash
./gradlew :auth-service:bootRun
```

### Health Check

```bash
curl http://localhost:8081/actuator/health
```

Expected response:

```json
{"status":"UP"}
```

## Database Migrations

Flyway migrations for `auth-service` are located under:

```text
auth-service/src/main/resources/db/migration/
```

Current migrations include:

- `V1__init_auth_schema.sql`
- `V2__create_users_table.sql`
- `V3__create_roles_table.sql`

## Near-Term Roadmap

Next steps:

1. create `user_roles` table
2. create `refresh_tokens` table
3. add JPA entities and repositories
4. implement user registration
5. implement login and JWT issuance
6. add protected `/me` endpoint
7. begin authorization service design

## Long-Term Features

Planned future features include:

- MFA
- Microsoft Entra SSO
- service-to-service authentication
- audit event ingestion APIs
- RBAC + ABAC authorization engine
- opaque token introspection
- key rotation
- CI/CD workflows
- Kubernetes deployment
- observability improvements

## Purpose

This project is intended to serve as:

- a deep learning exercise in backend and platform security engineering
- a portfolio project for software/security engineering roles
- a practical demonstration of design-to-deployment system building

## License

TBD

