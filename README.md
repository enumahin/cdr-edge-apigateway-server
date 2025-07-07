# CDR Edge Gateway Server

A Spring Cloud Gateway based API Gateway service that acts as the single entry point for all client requests in the CDR (Call Detail Records) system. It handles request routing, security, and load balancing for microservices.

## Features

- API Gateway with dynamic routing
- OAuth2 JWT-based authentication and authorization
- Service discovery with Eureka
- Circuit breaking with Resilience4j
- Health monitoring and metrics
- Request/Response logging

## Prerequisites

- Java 17 or higher
- Maven 3.6.3 or higher
- Redis (for rate limiting and caching)
- OAuth2 Authorization Server (running on port 7080 by default)
- Eureka Discovery Server (running on port 8761 by default)
- Spring Cloud Config Server (running on port 8071 by default, optional)

## Environment Variables

### Required Configuration

```properties
# Server Configuration
SERVER_PORT=9191

# Spring Application Name
SPRING_APPLICATION_NAME=gatewayserver

# Eureka Client Configuration
EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8761/eureka

# OAuth2 Configuration
SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI=http://localhost:7080/oauth2/check_token

# Redis Configuration
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379
```

### Optional Configuration

```properties
# Timeout Configuration
SPRING_CLOUD_GATEWAY_HTTPCLIENT_CONNECT_TIMEOUT=5000
SPRING_CLOUD_GATEWAY_HTTPCLIENT_RESPONSE_TIMEOUT=20s

# Redis Timeout Configuration
SPRING_DATA_REDIS_CONNECT_TIMEOUT=2s
SPRING_DATA_REDIS_TIMEOUT=1s

# Resilience4j Circuit Breaker Configuration
RESILIENCE4J_CIRCUITBREAKER_CONFIGS_DEFAULT_SLIDINGWINDOWSIZE=10
RESILIENCE4J_CIRCUITBREAKER_CONFIGS_DEFAULT_PERMITTEDNUMBEROFCALLSINHALFOPENSTATE=2
RESILIENCE4J_CIRCUITBREAKER_CONFIGS_DEFAULT_FAILURERATETHRESHOLD=50
RESILIENCE4J_CIRCUITBREAKER_CONFIGS_DEFAULT_WAITDURATIONINOPENSTATE=10s
```

## Security

The gateway implements OAuth2 JWT-based authentication with the following security configuration:

- Public endpoints (no authentication required):
  - `/auth/**` - Authentication endpoints
  - `/cdr/metadata/**` - Metadata endpoints

- Protected endpoints (require valid JWT token):
  - `/cdr/**` - All other CDR endpoints

## Getting Started

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd cdr-edge-gateway-server
   ```

2. **Build the application**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   java -jar target/cdr-edge-gateway-server-1.0.0.jar
   ```
   Or using Maven:
   ```bash
   mvn spring-boot:run
   ```

## API Documentation

Once the application is running, you can access:

- **Swagger UI**: `http://localhost:9191/swagger-ui.html`
- **Actuator Endpoints**: `http://localhost:9191/actuator`

## Monitoring

The service exposes the following monitoring endpoints:

- Health: `GET /actuator/health`
- Info: `GET /actuator/info`
- Metrics: `GET /actuator/metrics`
- Prometheus: `GET /actuator/prometheus`

## Dependencies

- Spring Boot Cloud Gateway
- Spring Security OAuth2 Resource Server
- Spring Cloud Netflix Eureka Client
- Spring Boot Actuator
- Resilience4j
- Spring Data Redis

## License

[Specify your license here]

## Contact

[Your contact information]
