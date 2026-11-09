import { SKILLS, students } from '../data';

const avg = (i) =>
  Math.round(
    students.reduce((a, s) => a + s.m[i], 0) / students.length
  );

export const skillAvg = SKILLS.map((s, i) => [s, avg(i)]);
