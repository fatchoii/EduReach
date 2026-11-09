import React from 'react';

export const Button = ({ v = 'primary', ...p }) => (
  <button
    {...p}
    className={`btn ${v} ${p.className || ''}`}
  />
);
