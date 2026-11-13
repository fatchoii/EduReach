// Stores the logged-in session (JWT + user info) in the browser's localStorage.
const KEY = 'edureach.session';

export const getSession = () => {
  try {
    const raw = localStorage.getItem(KEY);
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
};

export const saveSession = (session) => {
  try {
    localStorage.setItem(KEY, JSON.stringify(session));
  } catch {
    // Storage can be unavailable (private mode); the user will simply have to log in again.
  }
};

export const clearSession = () => {
  try {
    localStorage.removeItem(KEY);
  } catch {
    // Nothing to clear.
  }
};

export const getToken = () => getSession()?.token ?? null;
export const getUser = () => getSession()?.user ?? null;
