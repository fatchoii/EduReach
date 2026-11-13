import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Button, FormInput } from '../../components';
import { homePathFor, login, register } from '../../services/authService';

const ROLES = ['Learner', 'Instructor', 'Admin', 'Parent'];

export function Auth({ mode, onAuth }) {
  const nav = useNavigate();

  const [f, setF] = useState({
    name: '',
    email: '',
    password: '',
    confirm: '',
    role: 'Learner',
    adminCode: '',
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
      if (f.role === 'Admin' && !f.adminCode.trim()) return setErr('Enter the admin code.');
    }

    setBusy(true);
    try {
      const user =
        mode === 'register'
          ? await register({ name: f.name, email: f.email, password: f.password, role: f.role, adminCode: f.adminCode })
          : await login(f.email, f.password);

      onAuth(user);
      nav(homePathFor(user.role));
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
          <>
            <FormInput label="Choose your role">
              <select value={f.role} onChange={set('role')}>
                {ROLES.map((r) => (
                  <option key={r}>{r}</option>
                ))}
              </select>
            </FormInput>
            <FormInput label="Full Name" value={f.name} onChange={set('name')} />
          </>
        )}

        <FormInput label="Email" type="email" value={f.email} onChange={set('email')} />

        <FormInput label="Password" type="password" value={f.password} onChange={set('password')} />

        {mode === 'register' && (
          <>
            <FormInput
              label="Confirm Password"
              type="password"
              value={f.confirm}
              onChange={set('confirm')}
            />
            {f.role === 'Admin' && (
              <FormInput label="Admin code" type="password" value={f.adminCode} onChange={set('adminCode')} />
            )}
          </>
        )}

        <Button disabled={busy}>
          {busy ? 'Please wait...' : mode === 'login' ? 'Login' : 'Register'}
        </Button>

        <small>
          {mode === 'login' ? (
            <>
              No account? <Link to="/register">Register</Link>. Demo accounts (password password123):
              learner ana@edureach.com, instructor teacher@edureach.com, admin admin@edureach.com,
              parent parent@edureach.com.
            </>
          ) : (
            <>
              Have an account? <Link to="/login">Login</Link>. One account works on web and mobile.
            </>
          )}
        </small>
      </form>
    </div>
  );
}
