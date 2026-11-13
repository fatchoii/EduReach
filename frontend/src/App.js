import React, { useEffect, useState } from 'react';
import { Routes, Route, Navigate, Outlet, useNavigate } from 'react-router-dom';

import { Navbar, OfflineBanner, Sidebar } from './components';
import { useAutoSync } from './hooks/useOfflineQueue';
import { useUnread } from './hooks/useUnread';
import * as P from './pages';
import { UNAUTHORIZED_EVENT } from './services/api';
import { getCurrentUser, homePathFor, logout } from './services/authService';

// Sidebar menu for each role
const NAV = {
  Learner: [
    ['/learner/dashboard', 'My learning', '🏠'],
    ['/learner/offline', 'Offline and sync', '⬇️'],
    ['/learner/notifications', 'Notifications', '🔔'],
    ['/learner/profile', 'Profile', '👤'],
  ],
  Instructor: [
    ['/instructor/dashboard', 'Dashboard', '🏠'],
    ['/instructor/courses', 'Courses', '📚'],
    ['/instructor/gradebook', 'Gradebook', '📝'],
    ['/instructor/notifications', 'Notifications', '🔔'],
    ['/instructor/profile', 'Profile', '👤'],
  ],
  Admin: [
    ['/admin/dashboard', 'Dashboard', '🏠'],
    ['/admin/users', 'Users and roles', '👥'],
    ['/admin/sync', 'Sync monitor', '🔄'],
    ['/instructor/courses', 'Courses', '📚'],
    ['/instructor/gradebook', 'Gradebook', '📝'],
    ['/admin/notifications', 'Notifications', '🔔'],
    ['/admin/profile', 'Profile', '👤'],
  ],
  Parent: [
    ['/parent/dashboard', 'My children', '🏠'],
    ['/parent/notifications', 'Notifications', '🔔'],
    ['/parent/profile', 'Profile', '👤'],
  ],
};

const notificationsPath = (role) => NAV[role].find(([, label]) => label === 'Notifications')[0];

// Main layout: top bar, role menu, page content
function Shell({ user, roles, onLogout }) {
  const [open, setOpen] = useState(false);
  const unread = useUnread(!!user);
  useAutoSync(!!user && user.role === 'Learner');

  if (!user) {
    return <Navigate to="/login" />;
  }

  // Send users to their own area if they open another role's pages.
  if (!roles.includes(user.role)) {
    return <Navigate to={homePathFor(user.role)} />;
  }

  return (
    <div className="shell">
      <Navbar
        user={user}
        onMenu={() => setOpen(!open)}
        onLogout={onLogout}
        unread={unread}
        notificationsPath={notificationsPath(user.role)}
      />

      <div className="body">
        <Sidebar items={NAV[user.role]} open={open} close={() => setOpen(false)} />

        <main>
          {user.role === 'Learner' && <OfflineBanner />}
          <Outlet />
        </main>
      </div>
    </div>
  );
}

export default function App() {
  const [user, setUser] = useState(getCurrentUser);
  const nav = useNavigate();

  const out = () => {
    logout();
    setUser(null);
    nav('/');
  };

  // The server rejected our token (expired or invalid): return to the login page.
  useEffect(() => {
    const onUnauthorized = () => {
      setUser(null);
      nav('/login');
    };
    window.addEventListener(UNAUTHORIZED_EVENT, onUnauthorized);
    return () => window.removeEventListener(UNAUTHORIZED_EVENT, onUnauthorized);
  }, [nav]);

  const shell = (roles) => <Shell user={user} roles={roles} onLogout={out} />;

  return (
    <Routes>
      {/* Public */}
      <Route path="/" element={<P.Landing />} />
      <Route path="/login" element={<P.Auth mode="login" onAuth={setUser} />} />
      <Route path="/register" element={<P.Auth mode="register" onAuth={setUser} />} />

      {/* Learner */}
      <Route path="/learner" element={shell(['Learner'])}>
        <Route path="dashboard" element={<P.LearnerDashboard />} />
        <Route path="courses/:courseId" element={<P.LearnerCourse />} />
        <Route path="quiz/:assessmentId" element={<P.LearnerQuiz />} />
        <Route path="offline" element={<P.LearnerOffline />} />
        <Route path="offline/:courseId" element={<P.OfflineCourse />} />
        <Route path="offline/:courseId/quiz/:assessmentId" element={<P.OfflineQuiz />} />
        <Route path="notifications" element={<P.Notifications />} />
        <Route path="profile" element={<P.Profile user={user} />} />
      </Route>

      {/* Instructor (admins can use these pages too) */}
      <Route path="/instructor" element={shell(['Instructor', 'Admin'])}>
        <Route path="dashboard" element={<P.InstructorDashboard />} />
        <Route path="courses" element={<P.Courses />} />
        <Route path="courses/:courseId" element={<P.CourseBuilder />} />
        <Route path="gradebook" element={<P.Gradebook />} />
        <Route path="notifications" element={<P.Notifications />} />
        <Route path="profile" element={<P.Profile user={user} />} />
      </Route>

      {/* Admin */}
      <Route path="/admin" element={shell(['Admin'])}>
        <Route path="dashboard" element={<P.AdminDashboard />} />
        <Route path="users" element={<P.AdminUsers />} />
        <Route path="sync" element={<P.AdminSync />} />
        <Route path="notifications" element={<P.Notifications />} />
        <Route path="profile" element={<P.Profile user={user} />} />
      </Route>

      {/* Parent */}
      <Route path="/parent" element={shell(['Parent'])}>
        <Route path="dashboard" element={<P.ParentDashboard />} />
        <Route path="notifications" element={<P.Notifications />} />
        <Route path="profile" element={<P.Profile user={user} />} />
      </Route>

      <Route path="*" element={<Navigate to="/" />} />
    </Routes>
  );
}
