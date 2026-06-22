# BREADS MINDS – Backend API

Backend REST API for the **BREADS MINDS** mobile application, built as part of the **TCS Tech4Hope CSR Initiative (BWS)**.  
BREADS MINDS supports district coordinators in managing mental health programs, school visits, training sessions, and monthly reporting across Karnataka.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.3 |
| Core Framework | Spring Framework 6.2.x |
| Security | Spring Security 6.5.x + JWT (JJWT 0.12.6) |
| Persistence | Spring Data JPA + Hibernate 6.6.x |
| Database | MySQL 8.x |
| Build Tool | Maven 3.8+ |
| API Docs | SpringDoc OpenAPI 2.8.6 (Swagger UI) |
| Utilities | Lombok 1.18.36, MapStruct 1.6.3 |
| Code Coverage | JaCoCo 0.8.13 |

---

## Prerequisites

- Java 21+
- MySQL 8.x running on `localhost:3306`
- Maven 3.8+ (or use the Maven bundled with IntelliJ IDEA)

---

## Getting Started

### 1. Clone the repository

```bash
git clone https://github.com/10612566/BREADS_Backend.git
cd BREADS_Backend
```

### 2. Configure the database

The `dev` profile connects to MySQL with these defaults (override via env vars):

| Property | Default |
|---|---|
| URL | `jdbc:mysql://localhost:3306/breads_minds` |
| Username | `test` |
| Password | `mySecuritY25%` |

Set environment variables to override:
```bash
DB_USERNAME=your_username
DB_PASSWORD=your_password
```

The database `breads_minds` is created automatically on first startup (`createDatabaseIfNotExist=true`).

### 3. Run the application

```bash
mvn spring-boot:run
```

The server starts on **`http://localhost:8080/api`**.

On first startup, `DataInitializer` automatically seeds:
- 2 Areas, 4 Districts, 5 Schools
- 4 Health Programs
- 6 default users (see credentials below)
- 1 welcome notice

---

## Default Credentials

| Username | Password | Role |
|---|---|---|
| `superadmin` | `breads_master` | SUPER_ADMIN |
| `admin` | `breads_hq` | BREADS_COORDINATOR |
| `dc_bangalore` | `minds_blr` | DISTRICT_COORDINATOR |
| `dc_bidar` | `minds_bdr` | DISTRICT_COORDINATOR |
| `dc_chitradurga` | `minds_ctg` | DISTRICT_COORDINATOR |
| `dc_yadgir` | `minds_ydg` | DISTRICT_COORDINATOR |

---

## API Documentation (Swagger UI)

Once the app is running, open:

```
http://localhost:8080/api/swagger-ui.html
```

### How to authenticate in Swagger UI

1. Call **POST /auth/login** with your credentials
2. Copy the `token` from the response
3. Click the **Authorize** button (lock icon, top right)
4. Paste the token and click **Authorize**

All secured endpoints will now include the Bearer token automatically.

---

## API Endpoints Overview

| Tag | Base Path | Description |
|---|---|---|
| Authentication | `/api/auth` | Login, JWT token |
| Users | `/api/users` | User management, profile, approval |
| Areas | `/api/areas` | Geographic area CRUD |
| Districts | `/api/districts` | District CRUD (linked to areas) |
| Schools | `/api/schools` | School CRUD (linked to districts) |
| Notices | `/api/notices` | Notice board by user role |
| Service Requests | `/api/service-requests` | Request submission and review |
| Dashboard | `/api/dashboard` | Aggregated statistics |
| Training Sessions | `/api/training-sessions` | Training records per report |
| Additional Sessions | `/api/additional-sessions` | Extra activity sessions |
| Reports | `/api/reports` | Monthly beneficiary reports |
| System Logs | `/api/system-logs` | Paginated audit log |

---

## Role-Based Access

| Role | Permissions |
|---|---|
| `SUPER_ADMIN` | Full access to all endpoints |
| `BREADS_COORDINATOR` | Manage users, schools, districts, notices, review requests, lock reports |
| `DISTRICT_COORDINATOR` | Submit and update reports, manage sessions |

---

## Project Structure

```
src/main/java/com/breads/minds/
├── config/          # Security, CORS, OpenAPI, DataInitializer
├── controller/      # REST controllers (12 controllers)
├── dto/             # Request and response DTOs
│   ├── request/
│   └── response/
├── entity/          # JPA entities
│   └── enums/       # Role, Status, Category enums
├── exception/       # Global exception handler
├── repository/      # Spring Data JPA repositories
├── security/        # JWT filter, JwtUtil, UserDetailsService
└── service/         # Business logic services

src/main/resources/
├── application.yaml          # Base config (active profile: dev)
├── application-dev.yaml      # Dev DB config
├── application-prod.yaml     # Prod DB config (uses env vars)
└── db/
    ├── schema.sql            # DDL script
    └── seed_data.sql         # Reference data seed
```

---

## Configuration

Key properties in `application.yaml`:

```yaml
server:
  port: 8080
  servlet:
    context-path: /api

app:
  jwt:
    secret: ${JWT_SECRET:<default-secret>}
    expiration-ms: 86400000   # 24 hours
  cors:
    allowed-origins: ${CORS_ORIGINS:http://localhost:3000,http://localhost:5173}
  monthly-deadline: 5         # Reports due by 5th of each month
```

---

## Health Check

```
GET http://localhost:8080/api/actuator/health
```

---

## Dependency Versions

| Dependency | Version |
|---|---|
| Spring Boot | 3.5.3 |
| Spring Framework | 6.2.x (managed by Boot) |
| Spring Security | 6.5.x (managed by Boot) |
| Hibernate | 6.6.x (managed by Boot) |
| JJWT | 0.12.6 |
| SpringDoc OpenAPI | 2.8.6 |
| Lombok | 1.18.36 |
| MapStruct | 1.6.3 |
| JaCoCo | 0.8.13 |
| MySQL Connector/J | managed by Spring Boot BOM |

---

## License

Internal project — TCS Tech4Hope CSR Initiative. Not for public distribution.
