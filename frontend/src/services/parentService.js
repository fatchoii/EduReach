import { get } from './api';

/** The children linked to a parent, with their skill mastery. */
export async function loadParentDashboard(parentId) {
  const links = await get(`/api/parent-students?parentId=${parentId}`);
  return Promise.all(
    links.map(async (link) => ({
      studentId: link.studentId,
      studentName: link.studentName,
      mastery: await get(`/api/students/${link.studentId}/mastery`),
    }))
  );
}
