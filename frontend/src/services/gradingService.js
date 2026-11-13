import { get, post } from './api';
import { assessments, enrollments, modules } from './contentService';

export const getAnswers = (submissionId) => get(`/api/submission-answers?submissionId=${submissionId}`);
export const gradeSubmission = (id, body) => post(`/api/submissions/${id}/grade`, body);
export const runReminders = () => post('/api/notifications/run-reminders');

const average = (list) => (list.length ? list.reduce((s, n) => s + n, 0) / list.length : 0);

/** Gradebook, class progress and the review queue for one course. */
export async function loadGradebook(courseId) {
  const [mods, enrolled, allSubmissions] = await Promise.all([
    modules.list({ courseId }),
    enrollments.list({ courseId }),
    get('/api/submissions'),
  ]);
  const list = (await Promise.all(mods.map((m) => assessments.list({ moduleId: m.id })))).flat();
  const ids = new Set(list.map((a) => a.id));
  const submissions = allSubmissions
    .filter((s) => ids.has(s.assessmentId))
    .sort((a, b) => String(b.submittedAt).localeCompare(String(a.submittedAt)));

  const learners = enrolled.map((e) => {
    const mine = submissions.filter((s) => s.userId === e.userId);
    return {
      id: e.userId,
      name: e.userName,
      progress: e.progressPercent || 0,
      average: Math.round(average(mine.map((s) => s.score || 0))),
      submissions: mine.length,
    };
  });

  return {
    assessments: list,
    learners,
    submissions,
    pending: submissions.filter((s) => s.status === 'PENDING_REVIEW'),
  };
}
