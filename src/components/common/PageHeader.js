import React from 'react';

export const PageHeader = ({ t, children }) => (
  <div className="ph">
    <h2>{t}</h2>
    {children}
  </div>
);
