# CampusCare Kubernetes deployment

These manifests provide a local Kubernetes foundation for the three-container stack. They use `IfNotPresent` images named `campuscare/backend:local` and `campuscare/frontend:local`, so they work with Docker Desktop Kubernetes or a local cluster where the images are loaded.

## Prerequisites

- Docker Desktop Kubernetes, Minikube, or another Kubernetes cluster
- `kubectl` configured for that cluster
- Docker images built and available to the cluster

## Build local images

Build the frontend with an empty API URL so Nginx proxies `/api` to the in-cluster `backend` Service:

```powershell
docker build -t campuscare/backend:local .\backend
docker build --build-arg VITE_API_URL= -t campuscare/frontend:local .\frontend
```

For Minikube, run `minikube image load campuscare/backend:local` and `minikube image load campuscare/frontend:local` after building.

## Create the Kubernetes secret

The secret is intentionally created from the command line rather than committed as YAML:

```powershell
kubectl create namespace campuscare
kubectl -n campuscare create secret generic campuscare-secrets `
  --from-literal=MYSQL_ROOT_PASSWORD="replace-with-a-strong-password" `
  --from-literal=CAMPUSCARE_JWT_SECRET="replace-with-at-least-32-random-characters" `
  --from-literal=CAMPUSCARE_ADMIN_NAME="CampusCare Admin" `
  --from-literal=CAMPUSCARE_ADMIN_EMAIL="admin@example.com" `
  --from-literal=CAMPUSCARE_ADMIN_PASSWORD="replace-with-a-strong-admin-password"
```

Use a secret manager for shared or production clusters. Do not commit those values.

## Apply and inspect

```powershell
kubectl apply -k .\kubernetes
kubectl -n campuscare get pods,services,persistentvolumeclaims
kubectl -n campuscare rollout status deployment/mysql
kubectl -n campuscare rollout status deployment/backend
kubectl -n campuscare rollout status deployment/frontend
```

For a local cluster, access the frontend with:

```powershell
kubectl -n campuscare port-forward service/frontend 5173:80
```

Then open `http://localhost:5173`. Backend traffic stays internal through `backend:8080`; MySQL stays internal through `mysql:3306`.

## Database initialization

The backend runs with `ddl-auto=update`, so JPA creates/updates the `users` and `tickets` tables. For repeatable seed data, apply the repository SQL scripts against the cluster database using a temporary MySQL client pod or your migration process. The MySQL PVC preserves data across pod restarts.

## Remove the local deployment

```powershell
kubectl delete -k .\kubernetes
```

Deleting the workload does not necessarily delete the persistent volume data. Remove the PVC explicitly only when a fresh database is intended.
