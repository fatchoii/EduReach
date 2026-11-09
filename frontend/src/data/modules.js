import { SKILLS } from './skills';

// Learning Modules
export const modules = [
  'Basic Addition',
  'Addition with Regrouping',
  'Basic Subtraction',
  'Subtraction with Borrowing',
  'Borrowing Across Zero'
].map((t, i) => ({
  id: i + 1,
  title: t,
  skill: SKILLS[i]
}));
