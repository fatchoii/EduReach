# EduReach

EduReach is a web and mobile LMS for teachers, parents, and students that focuses on addition and subtraction for Grade 3 learners.

## Repository Layout

```
EduReach/
├── frontend/    React 18 app (Create React App)
├── backend/     Spring Boot 3 REST API (Java 21, Maven, MySQL, JWT)
└── README.md
```

- Backend base package: `com.it342g02.vibecoder.edureach`
- Maven: groupId `com.it342g02.vibecoder`, artifactId `edureach`

## Running the Backend

Requirements: Java 21 and MySQL (running on port 3306).

1. Copy `backend/.env.example` to `backend/.env` and put your MySQL password in it. This file is ignored by Git.
2. Start the API from the `backend` folder:
   ```bash
   ./mvnw spring-boot:run        # Windows PowerShell: .\mvnw spring-boot:run
   ```
3. The API runs at http://localhost:8080. The `edureach_db` database and all tables are created automatically, and demo data is inserted on the first run.

Demo accounts (password `password123`): `teacher@edureach.com`, `parent@edureach.com`, `ana@edureach.com` (student).

## Running the Frontend

```bash
cd frontend
npm install
npm start
```

The app runs at http://localhost:3000.

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

- Connect the React pages to the API (replace the mock data in `frontend/src/data`)
- Real authentication in the frontend
- Deploy the backend so the Vercel site can use it
