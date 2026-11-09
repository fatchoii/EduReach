import { students } from './students';

// Class Data
export const classes = [
  {
    id: 1,
    name: 'Grade 3 - Sampaguita',
    grade: 3,
    students: students
  },
  {
    id: 2,
    name: 'Grade 3 - Orchid',
    grade: 3,
    students: students.slice(0, 4)
  }
];

// Calculate Class Average
export const classAvg = (c) =>
  Math.round(
    c.students.reduce((a, s) => a + s.overall, 0) /
    c.students.length
  );
