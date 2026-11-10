# EduReach

EduReach is a web and mobile LMS for teachers, parents, and students that focuses on addition and subtraction for Grade 3 learners.

## Repository Layout

```
EduReach/
├── frontend/    React 18 app (Create React App)
├── backend/     Spring Boot 3 REST API (Java 21, Maven, PostgreSQL on Supabase, JWT)
└── README.md
```

- Backend base package: `com.it342g02.vibecoder.edureach`
- Maven: groupId `com.it342g02.vibecoder`, artifactId `edureach`

## Running the Backend

Requirements: Java 21 and a Supabase project (hosted PostgreSQL).

1. In Supabase, open the SQL Editor and run `create schema if not exists edureach;` once.
2. Create `backend/.env` (Git ignores it) with the Session pooler details from Supabase, under Connect:
   ```
   DB_URL=jdbc:postgresql://<pooler-host>:5432/postgres?sslmode=require
   DB_USERNAME=postgres.<project-ref>
   DB_PASSWORD=<your database password>
   ```
   See `backend/.env.example`. Share these values with teammates privately, never in the repository.
3. Start the API from the `backend` folder:
   ```bash
   ./mvnw spring-boot:run        # Windows PowerShell: .\mvnw spring-boot:run
   ```
4. The API runs at http://localhost:8080. Tables are created automatically in the `edureach` schema, and demo data is inserted on the first run.

Demo accounts (password `password123`): `teacher@edureach.com`, `parent@edureach.com`, `ana@edureach.com` (student).

## Running the Frontend

```bash
cd frontend
npm install
npm start
```

The app runs at http://localhost:3000 and talks to the backend at `http://localhost:8080`. To use another address, copy `frontend/.env.example` to `frontend/.env` and set `REACT_APP_API_URL`. Start the backend first.

Pages already connected to the API: login, registration, and the teacher dashboard. The other pages still use the mock data in `frontend/src/data` and are being connected one by one.

## Frontend Structure

```
frontend/src/
├── components/    Reusable UI (common, cards, layout)
├── pages/         Screens grouped by role (public, teacher, parent, shared)
├── services/      API client (api.js), login session, one service per feature
├── hooks/         Shared React hooks (useAsync)
├── data/          Mock data, being replaced by the API
├── utils/         Shared helpers
└── styles/        global.css
```

## Backend Structure

```
backend/src/main/java/com/it342g02/vibecoder/edureach/
├── config/        SecurityConfig (JWT, CORS, role rules), DataSeeder (demo data)
├── controller/    REST controllers
├── dto/           Request and response records
├── entity/        JPA entities (the ERD tables)
├── exception/     Error handling (JSON error responses)
├── repository/    Spring Data JPA repositories
├── security/      JWT service, request filter, user details
├── service/       Business logic
└── EduReachApplication.java
```

## API Overview

All endpoints are under `/api`. Send the token from login as `Authorization: Bearer <token>`.

| Area | Endpoints |
|---|---|
| Auth | `POST /auth/register`, `POST /auth/login`, `GET /auth/me` |
| Users | `GET /users?role=STUDENT`, `GET/PUT/DELETE /users/{id}` |
| Courses | `/courses?instructorId=`, `/enrollments?userId=&courseId=` |
| Content | `/modules?courseId=`, `/lessons?moduleId=`, `/assessments?moduleId=` |
| Questions | `/questions?assessmentId=&moduleId=` |
| Submissions | `/submissions?assessmentId=&userId=`, `/submission-answers?submissionId=` |
| Auto-grading | `POST /assessments/{id}/submit` with `{"answers":[{"questionId":1,"answer":"38"}]}` |
| Analytics | `GET /courses/{id}/mastery`, `GET /students/{id}/mastery` |
| Other | `/badges?userId=&courseId=`, `/parent-students?parentId=&studentId=`, `/notifications?userId=` |

Every resource above supports `GET` (list and by id), `POST`, `PUT /{id}` and `DELETE /{id}`. Creating, editing and deleting courses, modules, lessons, assessments, questions, enrollments, submissions, badges and parent links is restricted to teachers.

## Roadmap

- Connect the remaining pages (classes, lessons, question bank, assignments, submissions, analytics, notifications, parent dashboard)
- Hide correct answers from students
- Deploy the backend so the Vercel site can use it
