# Resource Booking System

Production-oriented REST API built with Java 17, Spring Boot 3, Spring Security 6, JWT, JPA/Hibernate, PostgreSQL/MySQL, Bean Validation, Springdoc OpenAPI, JUnit 5 and Mockito.

## Architecture
`Controller -> Service -> Repository -> Database`. Controllers handle HTTP concerns only. DTOs isolate the persistence model. Reservation authorization is enforced in the service layer using the authenticated JWT principal.

## Roles
- **USER**: read resources; create/read/update/delete only their own reservations.
- **ADMIN**: full resource CRUD and reservation access across all users.

## Security decisions
- Stateless `SecurityFilterChain`; no server sessions.
- BCrypt password hashing.
- JWT signed with an environment/configured secret.
- Reservation creation never accepts `userId`; ownership comes from `Authentication`.
- User reservation queries are filtered by authenticated username at the repository specification level.
- Ownership is checked again for get/update/delete to prevent IDOR/BOLA.
- Cancelled reservations cannot be modified.
- Users may only cancel a reservation; they cannot promote its status.

## Prerequisites
Java 17+, Maven 3.9+, PostgreSQL 14+/MySQL 8+.

## Configuration
Copy `.env.example` values into your environment. `application.properties` supports PostgreSQL by default. For MySQL set `DB_URL` to a MySQL JDBC URL and ensure the MySQL driver is selected by the URL.

Development uses `ddl-auto=update`; production should use Flyway or Liquibase migrations and managed secrets.

## Run
```bash
mvn clean test
mvn spring-boot:run
```

## Test credentials
Development seed data (not production credentials):
- ADMIN: `admin / admin123`
- USER: `user / user123`

Passwords are persisted as BCrypt hashes.

## Swagger
Open `http://localhost:8080/swagger-ui.html` and use **Authorize** with `Bearer <JWT>` after login.

## Authentication flow
`POST /auth/login` -> receive JWT -> send `Authorization: Bearer <JWT>` -> access protected endpoints.

## API
| Method | Endpoint | Role | Description |
|---|---|---|---|
| POST | `/auth/login` | Public | Authenticate |
| GET | `/resources` | USER/ADMIN | List resources |
| GET | `/resources/{id}` | USER/ADMIN | Get resource |
| POST | `/resources` | ADMIN | Create resource |
| PUT | `/resources/{id}` | ADMIN | Update resource |
| DELETE | `/resources/{id}` | ADMIN | Delete resource |
| GET | `/reservations` | USER/ADMIN | List; USER sees only own |
| GET | `/reservations/{id}` | USER/ADMIN | Ownership checked for USER |
| POST | `/reservations` | USER/ADMIN | Create for authenticated user |
| PUT | `/reservations/{id}` | USER/ADMIN | Ownership checked for USER |
| DELETE | `/reservations/{id}` | USER/ADMIN | Ownership checked for USER |

Reservation listing supports `status`, `minPrice`, `maxPrice`, `page`, `size`, and Spring Data `sort`, e.g. `/reservations?status=CONFIRMED&minPrice=100&maxPrice=1000&page=0&size=10&sort=price,desc`.

## Example login
```json
{"username":"user","password":"user123"}
```

## Example reservation
```json
{"resourceId":1,"startTime":"2026-09-01T10:00:00","endTime":"2026-09-01T12:00:00"}
```

## Reservation rules
- `startTime < endTime` and start must not be in the past.
- Resource must exist and be available.
- Overlap rule: `existing.startTime < requested.endTime && existing.endTime > requested.startTime`.
- Cancelled reservations do not block a new booking.
- Price is copied from the resource using `BigDecimal`; clients cannot set the price.
- New reservations are `CONFIRMED`.

## Error format
```json
{"timestamp":"2026-08-27T10:30:00","status":409,"error":"Conflict","message":"Resource is already reserved for the requested time period","path":"/reservations"}
```

## Tests
Run `mvn clean test`. The suite should cover authentication, RBAC, reservation ownership, validation, filtering, pagination, sorting and conflict detection as the implementation evolves.

## Concurrency limitation
The overlap check is transactional but a database-level exclusion/locking strategy is still recommended for strict high-concurrency booking guarantees. PostgreSQL can enforce non-overlap with an exclusion constraint using a range type; otherwise use appropriate pessimistic locking/serializable transactions for the booking path.

## Docker
A Docker Compose setup is intentionally omitted from this baseline because database vendor selection is configurable; the application can run directly against PostgreSQL or MySQL using the documented environment variables.

## Docker Compose
With Docker installed:
```bash
docker compose up --build
```
This starts PostgreSQL and the API. The compose credentials are development-only and should be changed for any shared environment.
