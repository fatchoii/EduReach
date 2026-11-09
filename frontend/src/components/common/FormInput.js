import React from 'react';

export const FormInput = ({ label, children, ...p }) => (
  <label className="fi">
    <span>{label}</span>
    {children || <input {...p} />}
  </label>
);
