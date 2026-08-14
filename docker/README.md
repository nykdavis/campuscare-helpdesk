# CampusCare Docker setup

Docker Compose runs the React frontend, Spring Boot backend, and MySQL database as one local stack.

## Start the stack

Docker Desktop must be running with Linux containers. From the repository root:

```powershell
Copy-Item .env.example .env
```

Replace the example password in `.env`, then run:

```powershell
docker compose up --build -d
docker compose ps
```

Open `http://localhost:5173`. The backend is available at `http://localhost:8080`, with health at `http://localhost:8080/actuator/health`. Containerized MySQL uses host port 3307 by default to avoid conflicting with a locally installed MySQL server.

## Logs and shutdown

```powershell
docker compose logs -f
docker compose down
```

Shutdown preserves MySQL data. To intentionally remove the database and start fresh:

```powershell
docker compose down --volumes
```

The schema and seed scripts run only when MySQL initializes an empty volume.
