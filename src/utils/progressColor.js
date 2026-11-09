export const getProgressColor = (v) =>
  v >= 80
    ? '#2e9e6b'
    : v >= 65
      ? '#e0a020'
      : '#d9534f';
