import { post } from './api';
import { clearSession, getUser, saveSession } from './session';

const ROLE_LABELS = { LEARNER: 'Learner', INSTRUCTOR: 'Instructor', ADMIN: 'Admin', PARENT: 'Parent' };

// Where each role lands after logging in.
export const homePathFor = (role) =>
  ({
    Learner: '/learner/dashboard',
    Instructor: '/instructor/dashboard',
    Admin: '/admin/dashboard',
    Parent: '/parent/dashboard',
  }[role] || '/');

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

export async function register({ name, email, password, role, adminCode }) {
  const body = { fullName: name, email, password, role: role.toUpperCase() };
  if (role === 'Admin') body.adminCode = adminCode;
  const response = await post('/api/auth/register', body, { auth: false });
  return startSession(response);
}

export { clearSession as logout, getUser as getCurrentUser };
