import { post } from './api';
import { clearSession, getUser, saveSession } from './session';

const ROLE_LABELS = { TEACHER: 'Teacher', PARENT: 'Parent', STUDENT: 'Student' };

// Saves the session returned by the backend and returns the user shape the UI expects.
function startSession(response) {
  const user = {
    id: response.userId,
    name: response.fullName,
    email: response.email,
    role: ROLE_LABELS[response.role] || response.role,
  };
  saveSession({ token: response.token, user });
  return user;
}

export async function login(email, password) {
  const response = await post('/api/auth/login', { email, password }, { auth: false });
  return startSession(response);
}

export async function register({ name, email, password, role }) {
  const response = await post(
    '/api/auth/register',
    { fullName: name, email, password, role: role.toUpperCase() },
    { auth: false }
  );
  return startSession(response);
}

export { clearSession as logout, getUser as getCurrentUser };
