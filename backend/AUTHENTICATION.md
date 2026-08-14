# JWT authentication

CampusCare uses stateless bearer tokens signed with HS256. Passwords are stored as adaptive one-way hashes through Spring Security's delegating password encoder (BCrypt for new accounts).

## Required configuration

Set a secret of at least 32 characters. Generate one for local development in PowerShell:

```powershell
$bytes = New-Object byte[] 48
[Security.Cryptography.RandomNumberGenerator]::Fill($bytes)
$env:CAMPUSCARE_JWT_SECRET = [Convert]::ToBase64String($bytes)
```

Tokens expire after 3600 seconds by default. Override this with `CAMPUSCARE_JWT_EXPIRATION_SECONDS`.

An initial admin account can be created on startup by setting all required values:

```powershell
$env:CAMPUSCARE_ADMIN_NAME="CampusCare Admin"
$env:CAMPUSCARE_ADMIN_EMAIL="admin@example.com"
$env:CAMPUSCARE_ADMIN_PASSWORD="replace-with-a-strong-password"
```

Public registration can only create `STUDENT` accounts; a client cannot register itself as an administrator.

## Register and log in

`POST /api/auth/register`

```json
{
  "name": "Asha Rao",
  "email": "asha.rao@example.com",
  "password": "replace-with-a-strong-password"
}
```

`POST /api/auth/login`

```json
{
  "email": "asha.rao@example.com",
  "password": "replace-with-a-strong-password"
}
```

Both endpoints return an access token, its lifetime, and the authenticated user. Send the token on protected calls:

```text
Authorization: Bearer <accessToken>
```

## Authorization rules

- Anyone may call registration, login, welcome, health, and info endpoints.
- Students may create tickets and read or edit tickets associated with their account email.
- Students cannot select another student's identity in a ticket request; the server uses the authenticated account.
- Administrators may list and edit all tickets.
- Only administrators may update ticket status or delete tickets.
