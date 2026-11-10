import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Button, FormInput } from '../../components';
import { login, logout, register } from '../../services/authService';

export function Auth({ mode, onAuth }) {
  const nav = useNavigate();

  const [f, setF] = useState({
    name: '',
    email: '',
    password: '',
    confirm: '',
    role: 'Teacher',
  });

  const [err, setErr] = useState('');
  const [busy, setBusy] = useState(false);

  const set = (k) => (e) => setF({ ...f, [k]: e.target.value });

  const go = async (e) => {
    e.preventDefault();
    setErr('');

    if (!f.email || !f.password) {
      return setErr('Email and password are required.');
    }

    if (mode === 'register') {
      if (!f.name.trim()) return setErr('Full name is required.');
      if (f.password.length < 6) return setErr('Password must be at least 6 characters.');
      if (f.password !== f.confirm) return setErr('Passwords do not match.');
    }

    setBusy(true);
    try {
      const user =
        mode === 'register'
          ? await register({ name: f.name, email: f.email, password: f.password, role: f.role })
          : await login(f.email, f.password);

      if (user.role !== 'Teacher' && user.role !== 'Parent') {
        logout();
        setErr('Student accounts are not available in the web app yet.');
        return;
      }

      onAuth(user);
      nav(user.role === 'Parent' ? '/parent/dashboard' : '/teacher/dashboard');
    } catch (ex) {
      setErr(ex.message);
    } finally {
      setBusy(false);
    }
  };

  return (
    <div className="auth">
      <form className="card" onSubmit={go}>
        <h2>{mode === 'login' ? 'Welcome back' : 'Create account'}</h2>

        {err && <p className="err">{err}</p>}

        {mode === 'register' && (
          <FormInput label="Full Name" value={f.name} onChange={set('name')} />
        )}

        <FormInput label="Email" type="email" value={f.email} onChange={set('email')} />

        <FormInput
          label="Password"
          type="password"
          value={f.password}
          onChange={set('password')}
        />

        {mode === 'register' && (
          <>
            <FormInput
              label="Confirm Password"
              type="password"
              value={f.confirm}
              onChange={set('confirm')}
            />

            <FormInput label="Role">
              <select value={f.role} onChange={set('role')}>
                <option>Teacher</option>
                <option>Parent</option>
              </select>
            </FormInput>
          </>
        )}

        <Button disabled={busy}>
          {busy ? 'Please wait...' : mode === 'login' ? 'Login' : 'Register'}
        </Button>

        <small>
          {mode === 'login' ? (
            <>
              No account? <Link to="/register">Register</Link>. Demo accounts (password
              password123): teacher@edureach.com or parent@edureach.com.
            </>
          ) : (
            <>
              Have an account? <Link to="/login">Login</Link>
            </>
          )}
        </small>
      </form>
    </div>
  );
}
