import React from 'react';

export const Badge = ({ t }) => (
  <span className={`bd ${t.replace(/\s/g, '')}`}>
    {t}
  </span>
);
