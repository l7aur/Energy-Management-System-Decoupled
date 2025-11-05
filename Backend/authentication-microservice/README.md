# Authentication Microservice — Spring Boot

The authentication microservice is part of the **Energy Management application**.  
Its responsibility is to securely authenticate users and manage system access, including generating JWT tokens, managing roles/authorities, and storing encrypted credentials.

This service is **accessible only through the API Gateway** and is included in the shared **Docker Compose** environment.

## Features

- Spring Security-based authentication
- Auth0 JWT token generation & validation
- Secure password encryption
- Role-based access control (RBAC)
- PostgreSQL user and token storage
- CRUD operations for authentication entities
- Springdoc / OpenAPI documentation: `http://localhost/auth/swagger-ui/index.html`
- Docker container support
- Configurable token expiration policies
- JWT token is validated by traefik API-gateway

## Tech Stack

| Component | Technology |
|----------|-----------|
| Language | Java 25+ |
| Framework | Spring Boot |
| Build Tool | Maven |
| Database | PostgreSQL |
| Security | Spring Security, Auth0 JWT |
| Documentation | Springdoc OpenAPI |
| Container | Docker |
| Deployment | Docker Compose |
| API-gateway | Traefik + JWT & CORS plugins |

## Security Notes

- Passwords stored encrypted using BCrypt
- JWT-based authentication
- Role/authority mapping stored in DB
- No session state (stateless microservice)
- Gateway provides request authentication boundary

## Architecture

This service interacts with:

- **Authentication Database** - separate docker network: `authentication-network`
- **API Gateway (Traefik)** — all requests routed & authenticated externally: `traefik-network`

Direct public communication is allowed for the `/auth/login` and `auth/register` endpoints.

This microservice does not call other services.

Endpoints are prefixed with `/auth`.

## application.properties requirements

    spring.application.name=authentication-microservice
    server.port=your-server-port

    spring.datasource.driver-class-name=org.postgresql.Driver
    spring.datasource.url=docker-database-endpoint
    spring.datasource.username=database-username
    spring.datasource.password=database-password

    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.open-in-view=false

    jwt.expiration=1h
    jwt.secret=your-very-secret

    springdoc.api-docs.enabled=true
    springdoc.api-docs.path=/v3/api-docs

    springdoc.swagger-ui.enabled=true
    springdoc.swagger-ui.config-url=/auth/v3/api-docs/swagger-config
    springdoc.swagger-ui.path=/swagger-ui.html
    springdoc.swagger-ui.url=/auth/v3/api-docs

    springdoc.swagger-ui.disable-swagger-default-url=true

## How to Use

Follow these steps to run the Energy Management locally or with Docker.

### Development

1. Clone the repository
2. Build the microservice
```bash
mvn clean package
```
3. Run locally
```bash
java -jar target/<microservice-name>.jar
```

### Run with docker-compose

The `docker-compose.yml` file is provided in the repository.

```bash
docker-compose up --build
```