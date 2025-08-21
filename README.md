# Wishlist Service

A RESTful API for managing user wishlists, built with Spring Boot, MongoDB, and documented with OpenAPI/Swagger.

## Features

* Add a product to the customer's wishlist
* Remove a product from the customer's wishlist
* List all products in the customer's wishlist
* Check if a specific product is in the customer's wishlist
* Enforces a maximum of 20 products per wishlist
* MongoDB persistence
* JWT authentication (configurable)
* Interactive API documentation via Swagger UI

## Tech Stack

* Java 21+
* Spring Boot 3
* MongoDB (NoSQL)
* Gradle
* OpenAPI/Swagger (springdoc-openapi)
* Docker & Docker Compose

## Clean Architecture

* Organized by domain, application, adapters, and configuration packages
* Follows best practices for maintainability and scalability

## Getting Started

### Prerequisites

* Java 21+
* Docker & Docker Compose
* (Optional) MongoDB locally if not using Docker

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

### Building and Running with Docker

To build the Docker image locally (the same way as in CI):

```sh
# Build the Docker image
docker build -t wishlist-service:ci-demo .
```

To run the application in a container:

```sh
docker run -p 8080:8080 wishlist-service:ci-demo
```

### API Documentation

* Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
* OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

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

### JWT Token Generation for Swagger and API Testing (For Local Testing Only)

> **Warning:** The script below is intended for local testing only. The generated token will **not** work in homologation or production environments, and must not be used for real users or production data.

You can generate a valid JWT for testing using the provided shell script:

```sh
./jwt-generate-locally.sh
```

This script will:

* Read the JWT secret from your `src/main/resources/application.properties`
* Generate a valid JWT token for user id `1` with 1 hour expiry
* Print only the token value to the terminal (ready to copy)

Paste the generated token into the "Authorize" field in Swagger UI to authenticate your requests.

---

## ⚠️ Important: Matching `userId` in Path and Token

The `WishlistController` enforces that the `userId` in the URL **must match** the authenticated user in the JWT claims.

* It checks the claim `user_id` (preferred) or falls back to `sub`.
* If they don’t match, the API will return **403 Forbidden**.

### Example

* Token with `user_id = "1"` (or `sub = "1"`):

    * ✅ Works: `POST /v1/wishlists/1/product`
    * ❌ Fails: `POST /v1/wishlists/2/product`

### How to generate tokens for different users

The script `jwt-generate-locally.sh` can be adjusted to accept a userId argument. Example version:

```bash
#!/usr/bin/env bash
set -euo pipefail

USER_ID="${1:-1}"
TTL="${2:-3600}" # seconds

SECRET="2b7e151628aed2a6abf7158809cf4f3c" # must match application-local.properties

header_base64=$(printf '{"alg":"HS256","typ":"JWT"}' | openssl base64 -A | tr '+/' '-_' | tr -d '=')

iat=$(date +%s)
exp=$((iat + TTL))
payload=$(printf '{"sub":"%s","user_id":"%s","scope":"user","iat":%s,"exp":%s}' "$USER_ID" "$USER_ID" "$iat" "$exp")
payload_base64=$(printf '%s' "$payload" | openssl base64 -A | tr '+/' '-_' | tr -d '=')

unsigned="${header_base64}.${payload_base64}"
sig=$(printf '%s' "$unsigned" | openssl dgst -binary -sha256 -hmac "$SECRET" | openssl base64 -A | tr '+/' '-_' | tr -d '=')

echo "Bearer ${unsigned}.${sig}"
```

Usage:

```sh
./jwt-generate-locally.sh 2
```

This generates a token with `user_id=2` that will work for `/wishlists/2/...` endpoints.

---

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

* The secret must be 32 characters (256 bits) for HS256.
* Example:
  `spring.security.oauth2.resourceserver.jwt.secret=12345678901234567890123456789012`

#### Environment Variables

You can override any property using environment variables, e.g.:

* `SPRING_DATA_MONGODB_URI`

### Running Tests

```sh
./gradlew test
```

---

### Luizalabs Interview Exercise

This project was developed as a solution for the Luizalabs backend interview exercise. It demonstrates:

* RESTful API design
* Clean architecture
* BDD-style and unit tests
* Containerization with Docker
* API documentation with Swagger
* Enforced business rules (max 20 products per wishlist)
