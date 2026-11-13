// "PENDING_REVIEW" -> "Pending review"
export const statusLabel = (s) => (s ? s.charAt(0) + s.slice(1).toLowerCase().replace(/_/g, ' ') : '');

export const formatDate = (v) => (v ? new Date(v).toLocaleDateString() : '-');
export const formatDateTime = (v) => (v ? new Date(v).toLocaleString() : '-');

const pad = (n) => String(n).padStart(2, '0');

// Local time without a time zone ("2026-11-10T20:15:00"), the format the backend expects.
export const toLocalIso = (d = new Date()) =>
  `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`;

export const formatBytes = (n) => {
  if (n < 1024) return `${n} B`;
  if (n < 1048576) return `${(n / 1024).toFixed(1)} KB`;
  return `${(n / 1048576).toFixed(2)} MB`;
};

export const uid = () =>
  window.crypto && window.crypto.randomUUID
    ? window.crypto.randomUUID()
    : `${Date.now()}-${Math.random().toString(16).slice(2)}`;

// YouTube links cannot play in a <video> tag; turn them into an embeddable address.
export const youtubeEmbed = (url) => {
  if (!url) return null;
  const match = url.match(/(?:youtube\.com\/watch\?v=|youtu\.be\/|youtube\.com\/embed\/)([\w-]{6,})/);
  return match ? `https://www.youtube.com/embed/${match[1]}` : null;
};
