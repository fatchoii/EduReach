import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Button, FormInput } from '../../components';

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

  const set = (k) => (e) =>
    setF({ ...f, [k]: e.target.value });

  const go = (e) => {
    e.preventDefault();

    if (!f.email || !f.password) {
      return setErr('Email and password are required.');
    }

    if (mode === 'register' && f.password !== f.confirm) {
      return setErr('Passwords do not match.');
    }

    const role =
      mode === 'register'
        ? f.role
        : f.email.includes('parent')
          ? 'Parent'
          : 'Teacher';

    onAuth({
      name: f.name || (role === 'Parent' ? 'Maria Reyes' : 'Ms. Santos'),
      email: f.email,
      role,
    });

    nav(role === 'Parent' ? '/parent/dashboard' : '/teacher/dashboard');
  };

  return (
    <div className="auth">
      <form className="card" onSubmit={go}>
        <h2>
          {mode === 'login' ? 'Welcome back' : 'Create account'}
        </h2>

        {err && <p className="err">{err}</p>}

        {mode === 'register' && (
          <FormInput
            label="Full Name"
            value={f.name}
            onChange={set('name')}
          />
        )}

        <FormInput
          label="Email"
          type="email"
          value={f.email}
          onChange={set('email')}
        />

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

        <Button>
          {mode === 'login' ? 'Login' : 'Register'}
        </Button>

        <small>
          {mode === 'login' ? (
            <>
              No account? <Link to="/register">Register</Link>. Tip: use an
              email containing "parent" for the parent view.
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
