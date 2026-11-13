# EduReach

EduReach is a web and mobile LMS for Grade 3 addition and subtraction. One account works on web and mobile, with three roles: Learner, Instructor and Admin (plus Parent).

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
2. Create `backend/.env` (Git ignores it). See `backend/.env.example` for the Supabase Session pooler values and the optional email settings.
3. Start the API from the `backend` folder:
   ```bash
   ./mvnw spring-boot:run        # Windows PowerShell: .\mvnw spring-boot:run
   ```
4. The API runs at http://localhost:8080. Tables are created automatically in the `edureach` schema and demo data is inserted on the first run.

Demo accounts (password `password123`): `admin@edureach.com`, `teacher@edureach.com` (instructor), `ana@edureach.com` (learner), `parent@edureach.com`. To register an Admin on the sign-up page, enter the admin code (`edureach-admin` unless `ADMIN_SIGNUP_CODE` is set).

If the roles or tables change between versions, reset the schema in the Supabase SQL Editor and restart the backend:
```sql
drop schema if exists edureach cascade;
create schema edureach;
```

## Running the Frontend

```bash
cd frontend
npm install
npm start
```

The app runs at http://localhost:3000 and talks to the backend at `http://localhost:8080` (change it with `REACT_APP_API_URL`, see `frontend/.env.example`). Start the backend first.

## Features (by requirement)

1. **Accounts and roles:** register and log in with Learner, Instructor, Admin or Parent roles. Menus and permissions follow the role, and admins manage roles under Users and roles.
2. **Course authoring:** instructors create courses with modules and lessons (text, video, audio, slides), preview them, and publish them. Publishing alerts the enrolled learners.
3. **Offline and sync:** learners download a course, study and take quizzes without a connection, and the work syncs automatically when the connection returns. Admins set the sync policy and watch the sync monitor.
4. **Quizzes, progress and grading:** learners take quizzes and assignments, and their progress updates immediately. Quizzes are auto-graded. Assignments go to the instructor's review queue in the gradebook, with class progress charts.
5. **Notifications:** in-app alerts for deadlines, new content, live session invites and grades, with reminder settings. Email alerts work when SMTP is configured.

## Frontend Structure

```
frontend/src/
├── components/    Reusable UI (common, cards, layout, builder)
├── pages/         Screens grouped by role (public, learner, instructor, admin, parent, shared)
├── services/      API client, session, offline storage and sync, one service per feature
├── hooks/         Shared React hooks (useAsync, online status, offline queue)
├── data/          Old mock data (no longer used by the pages)
├── utils/         Shared helpers
└── styles/        global.css
```

## Backend Structure

```
backend/src/main/java/com/it342g02/vibecoder/edureach/
├── config/        SecurityConfig (JWT, CORS, role rules), DataSeeder, ReminderScheduler
├── controller/    REST controllers
├── dto/           Request and response records
├── entity/        JPA entities (the ERD tables plus ParentStudent, SubmissionAnswer, Notification, LessonProgress, LiveSession, NotificationSetting, SyncLog, SyncPolicy)
├── exception/     Error handling (JSON error responses)
├── repository/    Spring Data JPA repositories
├── security/      JWT service, request filter, user details
├── service/       Business logic (grading, progress, alerts, offline package, sync)
└── EduReachApplication.java
```

## API Overview

All endpoints are under `/api`. Send the token from login as `Authorization: Bearer <token>`.

| Area | Endpoints |
|---|---|
| Auth | `POST /auth/register`, `POST /auth/login`, `GET /auth/me` |
| Users (admin) | `GET /users`, `PUT /users/{id}/role`, `DELETE /users/{id}` |
| Content (instructor) | `/courses`, `/modules`, `/lessons`, `/assessments`, `/questions`, `/live-sessions`, plus `POST /courses/{id}/publish`, `/lessons/{id}/publish`, `/assessments/{id}/publish` |
| Learner | `GET /me/progress`, `POST /lessons/{id}/complete`, `POST /me/enroll/{courseId}`, `GET /assessments/{id}/take`, `POST /assessments/{id}/submit`, `GET /me/submissions` |
| Grading (instructor) | `GET /submissions`, `GET /submission-answers?submissionId=`, `POST /submissions/{id}/grade` |
| Notifications | `GET /me/notifications`, `POST /me/notifications/{id}/read`, `GET/PUT /me/notification-settings`, `POST /notifications/run-reminders` |
| Offline and sync | `GET /offline/courses/{id}/package`, `POST /sync`, `GET/PUT /sync-policy`, `GET /admin/sync-logs` |
| Analytics | `GET /courses/{id}/mastery`, `GET /students/{id}/mastery` |

## Roadmap

- Google and Microsoft sign-in
- A mobile app using the same API (push notifications, native offline storage)
- Group workspaces, points and leaderboards, badges, multi-language support
- Deploy the backend so the Vercel site can use it
