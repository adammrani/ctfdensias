# ENSIAS CTF — React Frontend

Same design as the single-file HTML, restructured as a proper React + Vite project.

---

## Project Structure

```
src/
├── api.js                  ← All HTTP calls to Spring Boot (single source of truth)
├── main.jsx                ← Entry point
├── App.jsx                 ← Router + global providers
├── styles/
│   └── global.css          ← CSS variables, buttons, forms (shared everywhere)
├── context/
│   ├── AuthContext.jsx     ← Token, user, solved IDs — global auth state
│   └── NotifContext.jsx    ← Toast notification system
├── components/
│   ├── Navbar.jsx          ← Top nav (dynamic based on auth state)
│   └── ChallengeModal.jsx  ← Challenge detail + flag submission modal
└── pages/
    ├── Home.jsx            ← Landing page (redirects to /challenges if logged in)
    ├── Challenges.jsx      ← Challenge grid with category filter
    ├── Scoreboard.jsx      ← Team rankings table
    ├── Rules.jsx           ← Rules & scoring formula
    ├── About.jsx           ← About ENSIAS + category cards
    ├── Tickets.jsx         ← Open/view support tickets
    ├── Login.jsx           ← Login form
    ├── Register.jsx        ← 2-step: account → team create/join
    └── Admin.jsx           ← Admin panel (4 tabs)
```

---

## How to Connect to Spring Boot

### Development (recommended)

Vite's dev server proxies all `/api/*` requests to `http://localhost:8080`.
This means **zero CORS configuration needed** in development.

The proxy is configured in `vite.config.js`:
```js
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
    }
  }
}
```

### Running both together

**Terminal 1 — Spring Boot:**
```powershell
cd C:\Users\bachu\Desktop\Projects\ctfdensias
mvn clean spring-boot:run
```

**Terminal 2 — React frontend:**
```powershell
cd path\to\ensias-ctf-react
npm install
npm run dev
```

Then open: **http://localhost:5173**

The React app hits `/api/auth/login` which Vite forwards to `http://localhost:8080/api/auth/login`.
Spring Boot handles it and returns JSON. No CORS errors.

---

## Production Build

1. Build the React app:
   ```bash
   npm run build
   ```
   This outputs static files to `dist/`.

2. **Option A — Serve from Spring Boot directly:**
   Copy the `dist/` folder contents into `src/main/resources/static/` in your Spring Boot project.
   Spring Boot will serve the React app at `http://localhost:8080/`.
   No separate frontend server needed. No CORS issues.

3. **Option B — Deploy separately (e.g. Vercel/Netlify):**
   Create `.env.production`:
   ```
   VITE_API_BASE=https://your-backend-url.com
   ```
   Then build. The app will call your backend directly.
   You must configure CORS in Spring Boot `SecurityConfig.java`:
   ```java
   corsConfig.setAllowedOriginPatterns(List.of("https://your-frontend.vercel.app"));
   ```

---

## Environment Variables

| Variable         | Purpose                          | Default              |
|------------------|----------------------------------|----------------------|
| `VITE_API_BASE`  | Backend base URL (production)    | `""` (uses proxy)    |

Create `.env.local` for local overrides (gitignored by default).

---

## Auth Flow

1. User logs in → JWT token saved to `sessionStorage`
2. Every `apiCall()` in `api.js` reads the token and adds `Authorization: Bearer <token>`
3. On logout → `sessionStorage.clear()` + redirect to home
4. React Router guards:
   - `/challenges`, `/tickets` → require login → redirect to `/login`
   - `/admin` → require ADMIN role → redirect to `/`
   - `/login`, `/register` → redirect to `/challenges` if already logged in
