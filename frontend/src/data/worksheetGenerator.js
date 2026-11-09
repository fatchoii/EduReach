import { SKILLS } from './skills';

// Random Number Generator
const r = (n) => Math.floor(Math.random() * n);

// Generate Math Questions
export function gen(skill, diff, n) {
  const k = SKILLS.indexOf(skill);
  const m = diff === 'Easy' ? 1 : diff === 'Medium' ? 1.6 : 2;

  return Array.from({ length: n }, () => {
    let a, b, op = '+';

    if (k < 2) {
      a = 10 + r(40 * m);
      b = 10 + r(40 * m);

      if (k === 1) {
        while ((a % 10) + (b % 10) < 10) {
          a = 10 + r(40 * m);
          b = 10 + r(40 * m);
        }
      }
    } else {
      op = '-';

      if (k === 4) {
        a = (1 + r(4)) * 100;
        b = 11 + r(a - 20);
      } else {
        a = 30 + r(60 * m);
        b = 10 + r(a - 15);

        if (k === 3 && a % 10 >= b % 10) {
          b = b + (a % 10 - b % 10) + 1;
        }

        if (b >= a) {
          b = a - 1;
        }
      }
    }

    return {
      q: `${a} ${op} ${b} = ?`,
      a: op === '+' ? a + b : a - b
    };
  });
}
