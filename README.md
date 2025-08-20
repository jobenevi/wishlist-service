# Wishlist Service

A RESTful API for managing user wishlists, built with Spring Boot, MongoDB, and documented with OpenAPI/Swagger.

## Features
- Add a product to the customer's wishlist
- Remove a product from the customer's wishlist
- List all products in the customer's wishlist
- Check if a specific product is in the customer's wishlist
- Enforces a maximum of 20 products per wishlist
- MongoDB persistence
- JWT authentication (configurable)
- Interactive API documentation via Swagger UI

## Tech Stack
- Java 21 (Java 11+ compatible)
- Spring Boot 3
- MongoDB (NoSQL)
- Gradle
- OpenAPI/Swagger (springdoc-openapi)
- Docker & Docker Compose

## Clean Architecture
- Organized by domain, application, adapters, and configuration packages
- Follows best practices for maintainability and scalability

## Getting Started

### Prerequisites
- Java 21+ (Java 11+ supported)
- Docker & Docker Compose
- (Optional) MongoDB locally if not using Docker

### Running with Docker Compose

1. Build and start the services:
   ```sh
   docker-compose up --build
   ```
2. The API will be available at `http://localhost:8080`.
3. MongoDB will be available at `localhost:27017` (internal to Docker network).

### Running Locally (without Docker)

1. Start MongoDB locally (default port 27017).
2. Build and run the application:
   ```sh
   ./gradlew bootRun
   ```
3. The API will be available at `http://localhost:8080`.

### API Documentation
- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### Example Requests

#### Add Product to Wishlist
```bash
curl -X POST "http://localhost:8080/wishlists/1/items" \
     -H "Content-Type: application/json" \
     -d '{"productId": 123}'
```

#### Remove Product from Wishlist
```bash
curl -X DELETE "http://localhost:8080/wishlists/1/items/123"
```

#### List All Products in Wishlist
```bash
curl "http://localhost:8080/wishlists/1/items"
```

#### Check if Product is in Wishlist
```bash
curl "http://localhost:8080/wishlists/1/items/123"
```

### Configuration

Configuration is managed via `application.properties`. Example:

```
spring.application.name=wishlist-service
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
spring.data.mongodb.uri=mongodb://localhost:27017/wishlist

# JWT secret (must be 256 bits, e.g., 32 chars for HS256)
spring.security.oauth2.resourceserver.jwt.secret=12345678901234567890123456789012
```

#### JWT Authentication

JWT authentication is enabled by default. You must set the property `spring.security.oauth2.resourceserver.jwt.secret` in your `application.properties` or as an environment variable.
- The secret must be 32 characters (256 bits) for HS256.
- Example:
  `spring.security.oauth2.resourceserver.jwt.secret=12345678901234567890123456789012`

#### Environment Variables
You can override any property using environment variables, e.g.:
- `SPRING_DATA_MONGODB_URI`

### Running Tests

```sh
./gradlew test
```
---

### Luizalabs Interview Exercise
This project was developed as a solution for the Luizalabs backend interview exercise. It demonstrates:
- RESTful API design
- Clean architecture
- BDD-style and unit tests
- Containerization with Docker
- API documentation with Swagger
- Enforced business rules (max 20 products per wishlist)
