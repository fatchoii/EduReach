import { get } from './api';

const average = (numbers) =>
  numbers.length ? numbers.reduce((sum, n) => sum + n, 0) / numbers.length : 0;
const round = (n) => Math.round(n);

/**
 * Loads everything the teacher dashboard needs from the API:
 * the teacher's courses, student mastery, assignments and submissions.
 */
export async function loadTeacherDashboard(instructorId) {
  const courses = await get(`/api/courses?instructorId=${instructorId}`);

  const perCourse = await Promise.all(
    courses.map(async (course) => {
      const [mastery, modules] = await Promise.all([
        get(`/api/courses/${course.id}/mastery`),
        get(`/api/modules?courseId=${course.id}`),
      ]);
      return { mastery, modules };
    })
  );

  const moduleIds = new Set(perCourse.flatMap((c) => c.modules.map((m) => m.id)));
  const [allAssessments, allSubmissions] = await Promise.all([
    get('/api/assessments'),
    get('/api/submissions'),
  ]);
  const assessments = allAssessments.filter((a) => moduleIds.has(a.moduleId));
  const assessmentIds = new Set(assessments.map((a) => a.id));
  const submissions = allSubmissions.filter((s) => assessmentIds.has(s.assessmentId));

  // One entry per student, combining their scores from every course they are enrolled in.
  const students = new Map();
  const skillOrder = [];
  perCourse.forEach(({ mastery }) => {
    mastery.forEach((m) => {
      if (!students.has(m.studentId)) {
        students.set(m.studentId, { id: m.studentId, name: m.studentName, scores: new Map() });
      }
      m.skills.forEach((skill) => {
        if (!skillOrder.includes(skill.moduleTitle)) skillOrder.push(skill.moduleTitle);
        if (skill.attempts > 0) {
          const list = students.get(m.studentId).scores.get(skill.moduleTitle) || [];
          list.push(skill.score);
          students.get(m.studentId).scores.set(skill.moduleTitle, list);
        }
      });
    });
  });

  const studentList = [...students.values()].map((s) => {
    const skills = [...s.scores.entries()].map(([title, list]) => [title, average(list)]);
    return { id: s.id, name: s.name, skills, overall: average(skills.map(([, v]) => v)) };
  });

  const skills = skillOrder
    .map((title) => {
      const values = studentList.flatMap((s) => s.skills.filter(([t]) => t === title).map(([, v]) => v));
      return [title, round(average(values)), values.length];
    })
    .filter(([, , count]) => count > 0)
    .map(([title, value]) => [title, value]);

  const needsAttention = studentList
    .filter((s) => s.skills.length > 0 && s.overall < 70)
    .map((s) => {
      const [title, score] = s.skills.reduce((low, cur) => (cur[1] < low[1] ? cur : low));
      return { id: s.id, name: s.name, skill: title, score: round(score) };
    });

  const activity = [...submissions]
    .sort((a, b) => new Date(b.submittedAt) - new Date(a.submittedAt))
    .slice(0, 5)
    .map(
      (s) =>
        `${s.userName} submitted ${s.assessmentTitle} (${round(s.score)}%) on ${new Date(
          s.submittedAt
        ).toLocaleDateString()}`
    );

  return {
    studentCount: studentList.length,
    activeAssignments: assessments.filter((a) => a.status === 'ACTIVE').length,
    submissionCount: submissions.length,
    skills,
    averageMastery: skills.length ? round(average(skills.map(([, v]) => v))) : 0,
    needsAttention,
    activity,
  };
}
