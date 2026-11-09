import { SKILLS } from './skills';

// Question Bank
export const questions0 = [
  ['23 + 15 = ?', 38, 0, 'Easy'],
  ['45 + 32 = ?', 77, 0, 'Easy'],
  ['36 + 28 = ?', 64, 1, 'Medium'],
  ['57 + 39 = ?', 96, 1, 'Hard'],
  ['68 - 25 = ?', 43, 2, 'Easy'],
  ['45 - 17 = ?', 28, 3, 'Medium'],
  ['72 - 38 = ?', 34, 3, 'Medium'],
  ['60 - 27 = ?', 33, 4, 'Medium'],
  ['300 - 148 = ?', 152, 4, 'Hard'],
  ['405 - 167 = ?', 238, 4, 'Hard']
].map(([q, a, s, d], i) => ({
  id: i + 1,
  q,
  a,
  skill: SKILLS[s],
  diff: d
}));
