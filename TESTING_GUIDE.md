# ENSIAS CTF — Complete Setup & Testing Guide

---

## 1. Architecture Overview

```
Frontend (HTML/JS)
      │  HTTP + Bearer JWT
      ▼
Spring Boot API  (port 8080)
      │  JDBC / PostgreSQL protocol
      ▼
Supabase PostgreSQL  (managed cloud DB)
```

**Security stack:**
- Spring Security (stateless, no sessions)
- JWT via `jjwt` (Bearer token in `Authorization` header)
- BCrypt for passwords
- SHA-256 for flag hashing
- `@PreAuthorize("hasRole('ADMIN')")` on every admin endpoint

---

## 2. Supabase Setup (shared database for your team)

### Step 1 — Create a Supabase project
1. Go to https://supabase.com → New Project
2. Name: `ensias-ctf` | Region: closest to Morocco (EU West)
3. Set a strong database password — **save it**

### Step 2 — Run the schema
1. Supabase Dashboard → **SQL Editor** → **New Query**
2. Paste the full contents of `supabase-schema.sql`
3. Click **Run**

### Step 3 — Get your connection string
1. Dashboard → **Settings** → **Database**
2. Copy the **JDBC** URI, it looks like:
   ```
   jdbc:postgresql://db.xxxxxxxxxxxx.supabase.co:5432/postgres
   ```

### Step 4 — Configure the backend
Open `src/main/resources/application.properties` and replace:
```properties
spring.datasource.url=jdbc:postgresql://db.<YOUR-PROJECT-REF>.supabase.co:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=<YOUR-SUPABASE-DB-PASSWORD>
```

### Step 5 — Share with your team
Every teammate just needs to use the same `application.properties`.
No local database setup needed — everyone connects to Supabase.

---

## 3. Running the Backend

```bash
# Clone / open the project in IntelliJ or VS Code
cd ctfdensias

# Build (skip tests on first run)
./mvnw clean package -DskipTests

# Run
./mvnw spring-boot:run
```

The API starts at: **http://localhost:8080**

On first startup you'll see:
```
✅ Default admin created: admin / admin1234  — CHANGE THIS PASSWORD!
```

---

## 4. Swagger UI — Interactive API Testing

Open your browser: **http://localhost:8080/swagger-ui.html**

This gives you a full interactive UI to test every endpoint without Postman.

**To use protected endpoints in Swagger:**
1. Call `POST /api/auth/login` first
2. Copy the `token` from the response
3. Click the **Authorize** 🔒 button (top right)
4. Enter: `Bearer <your-token>`
5. All subsequent calls send the JWT automatically

---

## 5. Testing with curl / Postman

### Register a user
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"player1","email":"player1@ensias.ma","password":"pass1234"}'
```

### Login (get JWT token)
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@ensias.ma","password":"admin1234"}'

# Response:
# {"token":"eyJhbGc...","username":"admin","role":"ADMIN"}
```
Save the token:
```bash
TOKEN="eyJhbGc..."
```

### Create a challenge (Admin)
```bash
curl -X POST http://localhost:8080/api/challenges \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "SQL Injection 101",
    "description": "Bypass the login form",
    "category": "web",
    "difficulty": "EASY",
    "initialPoints": 100,
    "minimumPoints": 25
  }'

# Save the returned challenge ID:
CHALLENGE_ID="<uuid-from-response>"
```

### Add a flag to the challenge (stored as SHA-256 hash)
```bash
curl -X POST "http://localhost:8080/api/challenges/$CHALLENGE_ID/flags?flag=ENSIAS{sql_1nj3ct10n_byp4ss}" \
  -H "Authorization: Bearer $TOKEN"

# Response: {"message":"Flag added and hashed successfully"}
```

### Add a hint (Admin)
```bash
curl -X POST "http://localhost:8080/api/challenges/$CHALLENGE_ID/hints?content=Try+single+quote+injection&cost=0" \
  -H "Authorization: Bearer $TOKEN"
```

### Submit a flag (as a player)
```bash
PLAYER_TOKEN="<player-jwt-from-login>"

curl -X POST http://localhost:8080/api/submissions \
  -H "Authorization: Bearer $PLAYER_TOKEN" \
  -H "Content-Type: application/json" \
  -d "{\"challengeId\":\"$CHALLENGE_ID\",\"flag\":\"ENSIAS{sql_1nj3ct10n_byp4ss}\"}"

# Correct response:
# {"correct":true,"message":"🚩 Correct flag! Well done!","pointsAwarded":100}

# Wrong flag response:
# {"correct":false,"message":"❌ Wrong flag. Try again!"}
```

### View scoreboard
```bash
curl http://localhost:8080/api/scoreboard
```

### Hide scoreboard (Admin)
```bash
COMP_ID="<competition-uuid>"
curl -X PATCH "http://localhost:8080/api/competitions/$COMP_ID/scoreboard?visible=false" \
  -H "Authorization: Bearer $TOKEN"
```

### Change a user's role (Admin)
```bash
USER_ID="<user-uuid>"
curl -X PATCH "http://localhost:8080/api/admin/users/$USER_ID/role?role=ADMIN" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 6. Endpoint Reference

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/api/auth/register` | Public | Create account |
| POST | `/api/auth/login` | Public | Login → get JWT |
| POST | `/api/auth/logout` | User | Logout |
| GET | `/api/challenges` | Public | Active challenges |
| GET | `/api/challenges/all` | Admin | All challenges |
| POST | `/api/challenges` | Admin | Create challenge |
| PUT | `/api/challenges/{id}` | Admin | Update challenge |
| PATCH | `/api/challenges/{id}/toggle` | Admin | Enable/disable |
| DELETE | `/api/challenges/{id}` | Admin | Delete challenge |
| POST | `/api/challenges/{id}/flags?flag=` | Admin | Add flag (SHA-256 hashed) |
| DELETE | `/api/challenges/{id}/flags/{flagId}` | Admin | Remove flag |
| POST | `/api/challenges/{id}/hints?content=&cost=` | Admin | Add hint |
| GET | `/api/challenges/hints/{hintId}/reveal` | User | Reveal hint |
| POST | `/api/submissions` | User | Submit flag |
| GET | `/api/scoreboard` | Public* | Scoreboard |
| GET | `/api/scoreboard/admin` | Admin | Always-visible scoreboard |
| PATCH | `/api/competitions/{id}/scoreboard?visible=` | Admin | Show/hide scoreboard |
| GET | `/api/teams` | Admin | All teams |
| POST | `/api/teams` | User | Create team |
| POST | `/api/teams/{teamId}/members/{userId}` | Admin | Add member |

---

## 7. Flag Hashing Explained

Flags are **never stored in plaintext**. When you call `POST /api/challenges/{id}/flags?flag=ENSIAS{...}`:

```
"ENSIAS{my_flag}"  →  SHA-256  →  "a3f1c7d9..."  (stored in DB)
```

When a player submits a flag:
```
submitted "ENSIAS{my_flag}"  →  SHA-256  →  compare to stored hash
```

This means even if your database is compromised, attackers can't read the flags.

---

## 8. Switching Between Local H2 and Supabase

In `application.properties`:

**For local dev (H2 in-memory):** Uncomment the H2 block, comment Supabase lines.
- DB resets on every restart
- Great for rapid iteration

**For Supabase (shared):** Use the PostgreSQL connection string.
- Persistent, shared with teammates
- Run `supabase-schema.sql` once to create tables

---

## 9. Common Issues

| Problem | Solution |
|---------|----------|
| `401 Unauthorized` | Token expired or missing `Bearer ` prefix |
| `403 Forbidden` | Endpoint requires `ADMIN` role |
| `409 Conflict` | Username/email/team name already taken |
| `404 Not Found` | Wrong UUID |
| DB connection refused | Check Supabase URL and password in `application.properties` |
| Flag always wrong | Make sure you're submitting the exact string you passed to `/flags` |
