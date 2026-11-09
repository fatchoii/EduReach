// Student Data
export const students = [
  ['Ana Reyes', [95, 80, 90, 70, 62]],
  ['Ben Lopez', [88, 70, 84, 60, 50]],
  ['Carla Tan', [98, 92, 95, 85, 78]],
  ['Dan Cruz', [80, 58, 72, 48, 35]],
  ['Ella Go', [92, 85, 88, 75, 66]],
  ['Finn Uy', [85, 66, 80, 55, 44]]
].map(([name, m], i) => ({
  id: i + 1,
  name,
  m,
  overall: Math.round(m.reduce((a, b) => a + b) / 5)
}));
