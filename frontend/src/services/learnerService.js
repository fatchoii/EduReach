import { get, post } from './api';
import { qs } from './query';
import { assessments, courses, liveSessions, lessons, modules } from './contentService';

export const getMyProgress = () => get('/api/me/progress');
export const getMySubmissions = () => get('/api/me/submissions');
export const getCompletedLessons = (courseId) => get(`/api/me/completed-lessons${qs({ courseId })}`);
export const completeLesson = (lessonId) => post(`/api/lessons/${lessonId}/complete`);
export const enroll = (courseId) => post(`/api/me/enroll/${courseId}`);
export const getQuiz = (assessmentId) => get(`/api/assessments/${assessmentId}/take`);
export const submitQuiz = (assessmentId, answers) => post(`/api/assessments/${assessmentId}/submit`, { answers });

const byOrder = (a, b) => (a.orderIndex || 0) - (b.orderIndex || 0);

/** Everything the learner dashboard shows: progress, open courses, upcoming quizzes, live sessions. */
export async function loadLearnerDashboard() {
  const [progress, published, submissions] = await Promise.all([
    getMyProgress(),
    courses.list({ status: 'PUBLISHED' }),
    getMySubmissions(),
  ]);
  const enrolledIds = new Set(progress.map((p) => p.courseId));
  const submitted = new Set(submissions.map((s) => s.assessmentId));

  const perCourse = await Promise.all(
    progress.map(async (p) => {
      const [mods, sessions] = await Promise.all([
        modules.list({ courseId: p.courseId }),
        liveSessions.list({ courseId: p.courseId }),
      ]);
      const open = (
        await Promise.all(mods.map((m) => assessments.list({ moduleId: m.id, status: 'ACTIVE' })))
      ).flat();
      return { courseTitle: p.courseTitle, open, sessions };
    })
  );

  const upcoming = perCourse
    .flatMap((c) =>
      c.open.filter((a) => !submitted.has(a.id)).map((a) => ({ ...a, courseTitle: c.courseTitle }))
    )
    .sort((a, b) => (a.dueDate || '9999').localeCompare(b.dueDate || '9999'));

  const now = new Date();
  const sessions = perCourse
    .flatMap((c) => c.sessions.map((s) => ({ ...s, courseTitle: c.courseTitle })))
    .filter((s) => !s.scheduledAt || new Date(s.scheduledAt) >= now)
    .sort((a, b) => String(a.scheduledAt).localeCompare(String(b.scheduledAt)));

  return { progress, available: published.filter((c) => !enrolledIds.has(c.id)), upcoming, sessions };
}

/** A course as the learner sees it: published lessons, open quizzes, completed lessons, past scores. */
export async function loadCourseContent(courseId) {
  const [course, mods, completed, submissions] = await Promise.all([
    courses.get(courseId),
    modules.list({ courseId }),
    getCompletedLessons(courseId),
    getMySubmissions(),
  ]);
  const content = await Promise.all(
    [...mods].sort(byOrder).map(async (m) => {
      const [moduleLessons, moduleAssessments] = await Promise.all([
        lessons.list({ moduleId: m.id, status: 'PUBLISHED' }),
        assessments.list({ moduleId: m.id, status: 'ACTIVE' }),
      ]);
      return { id: m.id, title: m.title, orderIndex: m.orderIndex, lessons: moduleLessons, assessments: moduleAssessments };
    })
  );
  const scores = {};
  [...submissions]
    .sort((a, b) => String(a.submittedAt).localeCompare(String(b.submittedAt)))
    .forEach((s) => {
      scores[s.assessmentId] = { score: s.score, status: s.status };
    });
  return { course, modules: content, completed, scores };
}
