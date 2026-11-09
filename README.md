# EduReach

EduReach is a web and mobile LMS for teachers, parents, and students that focuses on addition and subtraction for Grade 3 learners.

## Tech Stack

- React 18 (Create React App)
- React Router 6

## Getting Started

```bash
npm install
npm start
```

The app runs at http://localhost:3000. To create a production build, run `npm run build`.

Demo login: use any email and password. An email containing the word `parent` opens the parent view; any other email opens the teacher view.

## Project Structure

```
src/
├── components/        Reusable UI, one component per file
│   ├── common/        Button, Card, Modal, Badge, ProgressBar, Chart, DataTable, FormInput, PageHeader
│   ├── cards/         DashboardCard, SkillCard, AssignmentCard, LessonCard, NotificationItem
│   ├── layout/        Navbar, Sidebar
│   └── index.js       Barrel file: import { Button } from './components'
├── pages/             One file per screen, grouped by user role
│   ├── public/        Landing, Auth
│   ├── teacher/       TeacherDashboard, Classes, Lessons, QuestionBank, Assignments,
│   │                  Submissions, Analytics, Worksheets
│   ├── parent/        ParentDashboard
│   ├── shared/        Notifications, Profile
│   └── index.js
├── data/              Mock data (one file per entity), to be replaced by the backend API
├── utils/             Shared helpers (progress colors, skill statistics)
├── styles/            global.css
├── App.js             Routes and layout shell
└── index.js           Entry point
```

## Features

- Landing page, login, and registration
- Teacher: dashboard, classes, lessons, question bank, assignments, submissions and grading, skill analytics, notifications, worksheet generator, profile
- Parent: dashboard with progress and suggested practice, notifications

## Roadmap

- Spring Boot REST API backend and database
- Replace mock data in `src/data` with API calls
- Real authentication
