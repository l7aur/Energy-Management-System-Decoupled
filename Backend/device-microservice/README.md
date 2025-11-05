# Device Microservice — Spring Boot

The Device microservice is part of the **Energy Management application**.  
Its responsibility is to store and manage **device entities**, and associate devices with users through **data duplication strategy** to optimize authorization and reduce cross-service calls.

This service is **not publicly exposed** and is **accessible only through the API Gateway**.  
It runs as part of the **Docker Compose microservices environment**.

## Features

- Spring Security-based role identification
- Role-based access control (RBAC)
- PostgreSQL user and device storage (data duplication)
- User-Device relationships using data duplication
- CRUD operations for device entities
- Springdoc / OpenAPI documentation: `http://localhost/device/swagger-ui/index.html`
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

- **Device Database** - separate docker network: `device-network`
- **API Gateway (Traefik)** — all requests routed & authenticated externally: `traefik-network`

No direct public communication is allowed — internal network only.

This microservice does not call other services directly — it stores duplicated user IDs and necessary user fields to avoid cross-service lookups.

Endpoints are prefixed with `/device`.

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
    springdoc.swagger-ui.config-url=/device/v3/api-docs/swagger-config
    springdoc.swagger-ui.path=/swagger-ui.html
    springdoc.swagger-ui.url=/device/v3/api-docs

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