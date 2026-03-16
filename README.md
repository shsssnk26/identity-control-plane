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
