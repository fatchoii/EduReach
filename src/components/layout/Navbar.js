import React from 'react';
import { Link } from 'react-router-dom';
import { Button } from '../common/Button';

export const Navbar = ({ onMenu, user, onLogout }) => (
  <header className="top">
    <Button
      v="ghost"
      className="burger"
      onClick={onMenu}
    >
      ☰
    </Button>

    <Link to="/" className="logo">
      ➕➖ EduReach
    </Link>

    <span className="sp" />

    <small>
      {user?.name} ({user?.role})
    </small>

    <Button v="ghost" onClick={onLogout}>
      Logout
    </Button>
  </header>
);
