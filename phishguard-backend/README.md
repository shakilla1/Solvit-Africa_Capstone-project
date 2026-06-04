# PhishGuard Backend

This folder contains the Java Spring Boot backend API for PhishGuard Pro.

## What it includes

- Spring Boot REST API
- Spring Data JPA with MySQL
- JWT authentication and role-based security
- Entities for admins, target users, campaigns, notifications, training progress, and events
- MySQL schema file: `schema.sql`

## Prerequisites

- Java 21
- Maven
- MySQL server

## How to run

1. Open a terminal in this folder:
   ```bash
   cd phishguard-backend
   ```
2. Update `src/main/resources/application.properties` with your MySQL credentials:
   - `spring.datasource.username`
   - `spring.datasource.password`
3. Use the schema file to initialize the database or let Spring auto-create the schema:
   ```bash
   mysql -u root -p < schema.sql
   ```
4. Start the backend:
   ```bash
   mvn clean spring-boot:run
   ```

The backend runs on `http://localhost:8080`.

## Notes

- The backend is SQL-based and uses MySQL, not NoSQL.
- `schema.sql` contains the database tables and indexes used by the application.
