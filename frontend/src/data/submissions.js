import { SKILLS } from './skills';

// Student Submissions
export const submissions = [
  {
    id: 1,
    student: 'Dan Cruz',
    assignment: 'Borrowing Basics',
    score: '2/4',
    date: '2026-09-24',
    status: 'Graded',
    items: [
      ['45 - 28 = ?', 27, 17, SKILLS[3]],
      ['72 - 38 = ?', 34, 34, SKILLS[3]],
      ['60 - 27 = ?', 47, 33, SKILLS[3]],
      ['81 - 46 = ?', 35, 35, SKILLS[3]]
    ]
  },
  {
    id: 2,
    student: 'Ben Lopez',
    assignment: 'Across Zero Challenge',
    score: '1/3',
    date: '2026-09-27',
    status: 'Needs Review',
    items: [
      ['300 - 148 = ?', 162, 152, SKILLS[4]],
      ['405 - 167 = ?', 238, 238, SKILLS[4]],
      ['200 - 76 = ?', 134, 124, SKILLS[4]]
    ]
  },
  {
    id: 3,
    student: 'Carla Tan',
    assignment: 'Regrouping Practice 1',
    score: '4/4',
    date: '2026-09-28',
    status: 'Graded',
    items: [
      ['36 + 28 = ?', 64, 64, SKILLS[1]],
      ['47 + 35 = ?', 82, 82, SKILLS[1]],
      ['59 + 24 = ?', 83, 83, SKILLS[1]],
      ['68 + 17 = ?', 85, 85, SKILLS[1]]
    ]
  }
];
