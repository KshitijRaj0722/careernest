# CareerNest — Job Seeker & Employer Platform

CareerNest connects job seekers with employers. Users register as either a
**Job Seeker** or an **Employer** and get access to role-specific features:
job seekers browse listings and apply, employers post and manage jobs and
review applications. SMS notifications (via Twilio) keep users updated on
application status changes.

## Tech Stack

| Layer      | Technology                                  |
|------------|----------------------------------------------|
| Backend    | Spring Boot, Spring Data JPA/MongoDB, REST    |
| Database   | MongoDB                                       |
| Frontend   | React, Redux Toolkit, TypeScript              |
| Styling    | TailwindCSS                                   |
| Security   | Spring Security + JWT (role-based access)     |
| SMS        | Twilio                                        |

## Project Structure

```
Careernest/
├── backend/                # Spring Boot + Maven API
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/com/careernest/backend/
│       │   │   ├── config/         # Security, CORS, Twilio config
│       │   │   ├── controller/     # REST controllers
│       │   │   ├── dto/
│       │   │   │   ├── request/
│       │   │   │   └── response/
│       │   │   ├── exception/      # Global exception handling
│       │   │   ├── model/          # MongoDB documents (User, Job, JobApplication)
│       │   │   ├── repository/     # Spring Data repositories
│       │   │   ├── security/       # JWT filter/util, UserDetailsService
│       │   │   ├── service/        # Service interfaces
│       │   │   │   └── impl/       # Service implementations
│       │   │   └── BackendApplication.java
│       │   └── resources/
│       │       └── application.properties
│       └── test/java/com/careernest/backend/
│
└── frontend/                # React + Redux Toolkit + TypeScript
    ├── index.html
    ├── package.json
    ├── tailwind.config.js
    ├── vite.config.ts
    └── src/
        ├── api/             # Axios client
        ├── app/             # Redux store + typed hooks
        ├── components/      # Reusable UI (common, layout)
        ├── features/        # Redux slices (auth, jobs, applications)
        ├── pages/           # Route-level pages
        ├── routes/          # React Router config
        ├── types/           # Shared TS types
        ├── App.tsx
        └── main.tsx
```

## Getting Started

### Backend
```bash
cd backend
mvn spring-boot:run
```
Requires a running MongoDB instance (see `MONGODB_URI` in `application.properties`)
and `JWT_SECRET` / `TWILIO_*` environment variables for full functionality.

### Frontend
```bash
cd frontend
npm install
npm run dev
```
