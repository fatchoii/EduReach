
import React, { useState } from 'react';
import {
  Routes,
  Route,
  Navigate,
  Outlet,
  useNavigate
} from 'react-router-dom';

import { Navbar, Sidebar } from './components';
import * as P from './pages';

// Teacher Navigation Items
const T = [
  ['dashboard', 'Dashboard', '🏠'],
  ['classes', 'Classes', '🏫'],
  ['lessons', 'Lessons', '📖'],
  ['question-bank', 'Question Bank', '❓'],
  ['assignments', 'Assignments', '📝'],
  ['submissions', 'Submissions', '📥'],
  ['analytics', 'Skill Analytics', '📊'],
  ['notifications', 'Notifications', '🔔'],
  ['worksheets', 'Worksheets', '🖨️'],
  ['profile', 'Profile', '👤'],
  ['settings', 'Settings', '⚙️']
].map(([p, l, i]) => ['/teacher/' + p, l, i]);

// Parent Navigation Items
const PA = [
  ['/parent/dashboard', 'Dashboard', '🏠'],
  ['/parent/notifications', 'Notifications', '🔔']
];

// Main Layout Component
function Shell({ user, role, onLogout }) {
  const [o, setO] = useState(false);

  if (!user) {
    return <Navigate to="/login" />;
  }

  return (
    <div className="shell">
      <Navbar
        user={user}
        onMenu={() => setO(!o)}
        onLogout={onLogout}
      />

      <div className="body">
        <Sidebar
          items={role === 'Teacher' ? T : PA}
          open={o}
          close={() => setO(false)}
        />

        <main>
          <Outlet />
        </main>
      </div>
    </div>
  );
}

// Main App Component
export default function App() {
  const [user, setUser] = useState(null);
  const nav = useNavigate();

  const out = () => {
    setUser(null);
    nav('/');
  };

  return (
    <Routes>
      {/* Public Routes */}
      <Route path="/" element={<P.Landing />} />

      <Route
        path="/login"
        element={<P.Auth mode="login" onAuth={setUser} />}
      />

      <Route
        path="/register"
        element={<P.Auth mode="register" onAuth={setUser} />}
      />

      {/* Teacher Routes */}
      <Route
        path="/teacher"
        element={
          <Shell
            user={user}
            role="Teacher"
            onLogout={out}
          />
        }
      >
        <Route path="dashboard" element={<P.TDash />} />
        <Route path="classes" element={<P.Classes />} />
        <Route path="lessons" element={<P.Lessons />} />
        <Route path="question-bank" element={<P.QBank />} />
        <Route path="assignments" element={<P.Assignments />} />
        <Route path="submissions" element={<P.Submissions />} />
        <Route path="analytics" element={<P.Analytics />} />

        <Route
          path="notifications"
          element={<P.Notifications role="teacher" />}
        />

        <Route path="worksheets" element={<P.Worksheets />} />
        <Route path="profile" element={<P.Profile user={user} />} />
        <Route path="settings" element={<P.Profile user={user} />} />
      </Route>

      {/* Parent Routes */}
      <Route
        path="/parent"
        element={
          <Shell
            user={user}
            role="Parent"
            onLogout={out}
          />
        }
      >
        <Route path="dashboard" element={<P.PDash />} />

        <Route
          path="notifications"
          element={<P.Notifications role="parent" />}
        />
      </Route>

      {/* Fallback Route */}
      <Route path="*" element={<Navigate to="/" />} />
    </Routes>
  );
}
