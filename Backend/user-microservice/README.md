# User Microservice — Spring Boot

The User microservice is part of the **Energy Management application**.  
Its responsibility is to store and manage **user-related information such as email, phone number, address, and other profile details**.

This service is **not publicly exposed** and is **accessible only through the API Gateway**.  
It runs as part of the **Docker Compose microservices environment**.

## Features

- Spring Security-based role identification
- Role-based access control (RBAC)
- PostgreSQL user storage
- Springdoc / OpenAPI documentation: `http://localhost/user/swagger-ui/index.html`
- CRUD operations for user entities
- Docker container support
- JWT token is validated by traefik API-gateway

## Tech Stack

| Component | Technology |
|----------|-----------|
| Language | Java 25+ |
| Framework | Spring Boot |
| Build Tool | Maven |
| Database | PostgreSQL |
| Security | Spring Security |
| Documentation | Springdoc OpenAPI |
| Container | Docker |
| Deployment | Docker Compose |
| API-gateway | Traefik + JWT & CORS plugins |

## Security Notes

- JWT-based authentication
- No session state (stateless microservice)
- Gateway provides request authentication boundary

## Architecture

This service interacts with:

- **User Database** - separate docker network: `user-network`
- **API Gateway (Traefik)** — all requests routed & authenticated externally: `traefik-network`

No direct public communication is allowed — internal network only.

This microservice does not call other services.

Endpoints are prefixed with `/user`.

## application.properties requirements

    spring.application.name=user-microservice
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
    springdoc.swagger-ui.config-url=/user/v3/api-docs/swagger-config
    springdoc.swagger-ui.path=/swagger-ui.html
    springdoc.swagger-ui.url=/user/v3/api-docs

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