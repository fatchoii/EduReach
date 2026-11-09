import React from 'react';
import { NavLink } from 'react-router-dom';

export const Sidebar = ({ items, open, close }) => (
  <nav className={`side ${open ? 'open' : ''}`}>
    {items.map(([p, l, i]) => (
      <NavLink
        key={p}
        to={p}
        onClick={close}
      >
        {i} {l}
      </NavLink>
    ))}
  </nav>
);
