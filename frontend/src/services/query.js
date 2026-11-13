// Builds "?a=1&b=2" from an object, skipping empty values.
export const qs = (params = {}) => {
  const text = Object.entries(params)
    .filter(([, v]) => v !== undefined && v !== null && v !== '')
    .map(([k, v]) => `${k}=${encodeURIComponent(v)}`)
    .join('&');
  return text ? `?${text}` : '';
};
