# CareerNest — Employers and Job Seeker Platform

CareerNest is a job portal that connects **job seekers** with **employers**. Users register
as either role and get access to features specific to it: seekers browse and search listings
and apply to them, while employers post and manage listings and review the applications they
receive. Application status changes trigger SMS notifications via Twilio.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot 3.3, Spring Data MongoDB, REST |
| Database | MongoDB (Atlas) |
| Frontend | React 18, Redux Toolkit, TypeScript, Vite |
| Styling | TailwindCSS |
| Security | Spring Security + JWT, role-based access |
| SMS | Twilio |

---

## Features

### Authentication & roles
- Registration and login with JWT (24-hour expiry), passwords hashed with BCrypt
- Two roles chosen at signup — `JOB_SEEKER` and `EMPLOYER` — enforced at the endpoint level
- Login failures return 401 without revealing whether the email or the password was wrong

### Employer
- Create, **edit**, and delete job postings
- View applicants for their own postings, with name, email, and phone
- Move an applicant through `APPLIED → REVIEWED → SHORTLISTED → REJECTED → HIRED`
- Ownership is enforced: employers cannot modify or view applicants for postings they did not create

### Job seeker
- Browse and search jobs by keyword and/or location (both filters optional)
- View job details and apply
- Track their own applications and current status
- Duplicate applications to the same job are rejected

### Notifications
- SMS on application submission and on every status change (see [Twilio](#twilio-sms) below)
- Notifications are best-effort: a failed send is logged but never fails the underlying action

---

## Project Structure

```
Careernest/
├── backend/                     # Spring Boot + Maven API
│   ├── config/                  # Local runtime config (gitignored) — real secrets live here
│   │   └── application.properties.example
│   ├── pom.xml
│   └── src/main/java/com/careernest/backend/
│       ├── config/              # Security, CORS, Twilio
│       ├── controller/          # REST endpoints
│       ├── dto/request|response # Request/response payloads
│       ├── exception/           # Custom exceptions + global handler
│       ├── model/               # Mongo documents: User, Job, JobApplication
│       ├── repository/          # Spring Data repositories
│       ├── security/            # JWT filter/util, UserDetailsService
│       └── service/impl/        # Business logic
│
└── frontend/                    # React + Redux Toolkit + TypeScript
    └── src/
        ├── api/                 # Axios client + endpoint wrappers
        ├── app/                 # Redux store + typed hooks
        ├── components/layout/   # NavBar
        ├── features/            # Redux slices
        ├── pages/               # Route-level pages
        ├── routes/              # Router + route guards
        └── types/               # Shared TypeScript types
```

---

## API Reference

Base URL: `http://localhost:8080`

Protected routes require a header: `Authorization: Bearer <token>`

### Public

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/` | Service info and endpoint directory |
| `GET` | `/actuator/health` | Health check, including MongoDB connectivity |
| `POST` | `/api/auth/register` | Create an account, returns a JWT |
| `POST` | `/api/auth/login` | Log in, returns a JWT |

### Authenticated (any role)

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/jobs?keyword=&location=` | Search jobs; both params optional |
| `GET` | `/api/jobs/{id}` | Job details |

### Employer only

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/jobs/mine` | Your postings |
| `POST` | `/api/jobs` | Create a posting |
| `PUT` | `/api/jobs/{id}` | Update your posting |
| `DELETE` | `/api/jobs/{id}` | Delete your posting |
| `GET` | `/api/applications/job/{jobId}` | Applicants for your posting |
| `PATCH` | `/api/applications/{id}/status` | Update an applicant's status |

### Job seeker only

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/applications/{jobId}` | Apply to a job |
| `GET` | `/api/applications/my` | Your applications |

### Status codes

| Code | Meaning |
|---|---|
| `400` | Validation failed, or an invalid enum/malformed body |
| `401` | Missing, invalid, or expired token; bad login credentials |
| `403` | Authenticated but wrong role, or not the owner of the resource |
| `404` | Resource does not exist |
| `409` | Duplicate — email already registered, or already applied to this job |

### Example

```bash
# Register an employer
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Acme HR","email":"hr@acme.com","password":"secret123","phoneNumber":"+911234567890","role":"EMPLOYER"}'

# Create a job with the returned token
curl -X POST http://localhost:8080/api/jobs \
  -H "Authorization: Bearer <token>" -H "Content-Type: application/json" \
  -d '{"title":"Java Developer","description":"Spring Boot","location":"Bangalore","salary":1800000,"deadline":"2026-12-31"}'
```

---

## Running Locally

### Prerequisites
- JDK 17+
- Maven 3.9+
- Node.js 18+
- A MongoDB database (MongoDB Atlas free tier, or a local `mongod`)

### 1. Backend

Copy the config template and fill in your own values:

```bash
cd backend
cp config/application.properties.example config/application.properties
```

Edit `config/application.properties`:

```properties
spring.data.mongodb.uri=mongodb+srv://<user>:<password>@<cluster>/careernest?retryWrites=true&w=majority
jwt.secret=<any random string, at least 32 characters>
jwt.expiration-ms=86400000

twilio.account-sid=
twilio.auth-token=
twilio.phone-number=
```

> This file is **gitignored** — real credentials never reach the repository.
> If your MongoDB password contains `@ : / ? # [ ]` or `%`, percent-encode it.

Start it:

```bash
mvn spring-boot:run
```

Runs on `http://localhost:8080`. Confirm with `curl http://localhost:8080/actuator/health` —
`"mongo":{"status":"UP"}` means the database is connected.

### 2. Frontend

```bash
cd frontend
npm install
npm run dev
```

Runs on `http://localhost:5173`. It defaults to the backend at `http://localhost:8080/api`;
override by creating `frontend/.env`:

```
VITE_API_BASE_URL=http://localhost:8080/api
```

---

## Configuration

Every setting resolves as `${ENV_VAR:default}`, so the same build works locally and in
production. Precedence, highest first:

1. **Environment variables** — used in deployment
2. **`backend/config/application.properties`** — used locally, gitignored
3. **`backend/src/main/resources/application.properties`** — defaults only, no secrets

| Variable | Purpose |
|---|---|
| `MONGODB_URI` | MongoDB connection string |
| `JWT_SECRET` | Token signing key, minimum 32 characters |
| `JWT_EXPIRATION_MS` | Token lifetime (default `86400000`, 24h) |
| `TWILIO_ACCOUNT_SID` | Twilio Account SID |
| `TWILIO_AUTH_TOKEN` | Twilio Auth Token |
| `TWILIO_PHONE_NUMBER` | Twilio sender number, E.164 format |
| `APP_CORS_ALLOWED_ORIGINS` | Comma-separated allowed origins (default `http://localhost:5173`) |

---

## Twilio SMS

The integration is complete and active whenever all three Twilio values are set; when they
are blank the app logs messages instead of sending them, so the rest of the system works
unchanged.

**Trial-account limitations to be aware of:**

- The sender must be a number **provisioned through Twilio** — a personal number will not work.
  Get one under Console → Phone Numbers → Buy a number (trial includes one).
- Trial accounts can only send to **verified** recipient numbers (Console → Verified Caller IDs).
- **Indian (+91) destinations additionally require DLT registration.** Indian carriers mandate
  registered sender IDs and pre-approved templates for A2P SMS, so free-form messages to +91
  numbers are rejected on a trial account with
  *"Trial accounts can only use predefined SMS templates."* Lifting this is a regulatory
  process requiring business verification, not a code change.

Because sends are best-effort, none of the above affects application behaviour — applying and
status changes still succeed and return `200`, with the failure logged.

---

## Deployment

**Backend** (Render / Railway / EC2) — set `MONGODB_URI`, `JWT_SECRET`, the `TWILIO_*`
variables, and `APP_CORS_ALLOWED_ORIGINS` (your deployed frontend URL) as environment
variables in the platform dashboard. Do not deploy `config/application.properties`.
Point the platform's health check at `/actuator/health`.

MongoDB Atlas → Network Access must allow `0.0.0.0/0`, as these platforms have no fixed
egress IP on their free tiers.

**Frontend** (Vercel / Netlify) — set `VITE_API_BASE_URL` to your deployed backend's
`/api` URL. Build command `npm run build`, output directory `dist`.

---

## Security Notes

- Passwords are hashed with BCrypt and never returned by any endpoint
- JWTs are stateless; the API holds no server-side session
- Applicant contact details are only exposed to the employer who owns that posting
- Search input is regex-escaped before reaching MongoDB
- Secrets are supplied by environment variables or a gitignored file — never committed
