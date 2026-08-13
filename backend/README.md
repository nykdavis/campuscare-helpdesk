# CampusCare Helpdesk API

The Java 21 backend for the CampusCare Student Helpdesk Portal provides REST endpoints for students to create and manage campus support tickets.

## Requirements

- Java 21
- MySQL 8.0 or newer
- No local Maven installation is required; the Maven Wrapper is included.

## Database setup

The default JDBC URL creates the `campuscare_helpdesk` database when the configured MySQL user has permission. Alternatively, run `CREATE DATABASE campuscare_helpdesk;` first.

| Environment variable | Purpose | Default |
| --- | --- | --- |
| `CAMPUSCARE_DB_URL` | MySQL JDBC URL | Local `campuscare_helpdesk` database |
| `CAMPUSCARE_DB_USERNAME` | MySQL username | `root` |
| `CAMPUSCARE_DB_PASSWORD` | MySQL password | Required; no default |
| `CAMPUSCARE_DDL_AUTO` | Hibernate schema action | `update` |

PowerShell setup and startup:

```powershell
$env:CAMPUSCARE_DB_USERNAME="root"
$env:CAMPUSCARE_DB_PASSWORD="replace-with-local-password"
$env:CAMPUSCARE_DB_URL="jdbc:mysql://localhost:3306/campuscare_helpdesk?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
.\mvnw.cmd spring-boot:run
```

## Tests

The tests mock application dependencies and do not need MySQL or Docker:

```powershell
.\mvnw.cmd clean test
```

## Endpoints

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/api/tickets` | Create a ticket |
| `GET` | `/api/tickets` | List tickets; filter by `status`, `category`, or `studentEmail` |
| `GET` | `/api/tickets/{id}` | Get one ticket |
| `PUT` | `/api/tickets/{id}` | Replace editable ticket details |
| `PATCH` | `/api/tickets/{id}/status` | Change ticket status |
| `DELETE` | `/api/tickets/{id}` | Delete a ticket |
| `GET` | `/api/welcome` | Verify the API is running |
| `GET` | `/actuator/health` | Check application health |
| `GET` | `/actuator/info` | View application information |

Example ticket creation body:

```json
{
  "title": "Unable to access campus Wi-Fi",
  "description": "The Wi-Fi login page does not accept my student account.",
  "category": "IT_SUPPORT",
  "studentName": "Asha Rao",
  "studentEmail": "asha.rao@example.com"
}
```

Example status update body:

```json
{
  "status": "IN_PROGRESS"
}
```

After startup, test health with `Invoke-RestMethod http://localhost:8080/actuator/health`.
