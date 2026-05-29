# Inventory Management API

A Spring Boot RESTful service for CRUD operations on inventory items, using PostgreSQL for persistent storage.

## Tech Stack
- Java 21
- Spring Boot 3.2.5
- Spring Data JPA (Hibernate)
- PostgreSQL 16
- Docker / Docker Compose

## Endpoints

| Method | URL                | Description          |
|--------|--------------------|----------------------|
| POST   | /api/inventory     | Create an item       |
| GET    | /api/inventory     | List all items       |
| GET    | /api/inventory/{id}| Get item by ID      |
| PUT    | /api/inventory/{id}| Update an item      |
| DELETE | /api/inventory/{id}| Delete an item      |

## Request Body (POST/PUT)
```json
{
  "name": "Widget",
  "quantity": 10,
  "price": 29.99,
  "description": "A useful widget"
}
```

## Running Locally (without Docker)
1. Ensure PostgreSQL is running and create a database named `inventorydb` with user `inventory_user` and password `inventory_pass`.
2. Build and run:
   ```bash
   ./mvnw spring-boot:run
   ```

## Running with Docker Compose
```bash
docker compose up --build
```
The API will be available at http://localhost:8080.

## Testing
```bash
./mvnw test
```
