# APCore Access Control

Spring Boot backend for managing companies, user accounts, and user permissions within a company.

## Technologies

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA / Hibernate
- PostgreSQL
- Maven

## Database

PostgreSQL database: `apcore`

The connection is configured in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/apcore
spring.datasource.username=admin
spring.datasource.password=${APCORE_DB_PASSWORD:change-me}
```

It is recommended to provide the password through an environment variable:

```bash
export APCORE_DB_PASSWORD='your-password'
```

The existing SQL schema has been copied to:

```text
src/main/resources/schema.sql
```

Hibernate does not modify the database schema because `spring.jpa.hibernate.ddl-auto=none`.

## Model

The system uses the existing tables and column names from the PostgreSQL schema:

- `client`
- `user_account`
- `client_user`
- `user_permission`

Permissions are linked to `client_user`, not directly to `user_account`. This allows the same user account to have different roles and permissions in different companies.

## Business Logic

### Company Registration

`POST /api/registration/company`

Flow:

- checks whether `client.tax_id` already exists
- creates a `client`
- creates a `user_account`
- creates a `client_user` with `role_code = OWNER`
- assigns the basic OWNER/admin permissions:
  - `CREATE_USER`
  - `UPDATE_USER`
  - `DEACTIVATE_USER`
  - `ASSIGN_PERMISSIONS`
  - `VIEW_CLIENT`
  - `UPDATE_CLIENT`
  - `ADMIN_PANEL`

The password is not stored as plain text. The service uses BCrypt and writes the result to `password_hash`.

Example request:

```json
{
  "client": {
    "taxId": "123456789",
    "registrationNumber": "98765432",
    "name": "Example Company",
    "address": "Main Street 1",
    "city": "Belgrade",
    "postalCode": "11000",
    "legalForm": "DOO",
    "status": "ACTIVE",
    "email": "office@example.com",
    "phone": "+381111111"
  },
  "owner": {
    "username": "owner",
    "email": "owner@example.com",
    "password": "strong-password",
    "firstName": "Owner",
    "lastName": "User",
    "phone": "+381641111111"
  }
}
```

### Adding a User to a Company

`POST /api/clients/{clientId}/users`

Only a user who has `CREATE_USER` or `ASSIGN_PERMISSIONS` in the same company can add a user.

If a user with the same email already exists, the existing `user_account` is used. Otherwise, a new one is created.

Example request:

```json
{
  "requesterClientUserId": 1,
  "roleCode": "OPERATOR",
  "user": {
    "username": "operator1",
    "email": "operator1@example.com",
    "password": "strong-password",
    "firstName": "Operator",
    "lastName": "One",
    "phone": "+381642222222"
  }
}
```

### Assigning Permissions

`POST /api/client-users/{clientUserId}/permissions`

Only a user who has `ASSIGN_PERMISSIONS` in the same company can assign a permission.

Example request:

```json
{
  "requesterClientUserId": 1,
  "permissionCode": "VIEW_CLIENT",
  "permissionName": "View client",
  "allowed": true
}
```

If a permission for the same `client_user_id` and `permission_code` already exists, the record is updated.

## Endpoints

| Method | Path | Description |
| --- | --- | --- |
| POST | `/api/registration/company` | Registers a new company and the first user as OWNER |
| POST | `/api/clients/{clientId}/users` | Adds a user to an existing company |
| GET | `/api/clients/{clientId}/users` | Lists all users for a company |
| POST | `/api/client-users/{clientUserId}/permissions` | Assigns a permission to a user in a company |
| GET | `/api/client-users/{clientUserId}/permissions` | Lists a user's permissions in a company |

## Running

From the project folder:

```bash
mvn spring-boot:run
```

Compilation:

```bash
mvn test
```

## Documentation

The ER model has been copied to:

```text
docs/client-user-permission-er-model.pdf
```
