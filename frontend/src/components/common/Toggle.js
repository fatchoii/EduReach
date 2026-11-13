import React from 'react';

// A labelled on/off switch.
export const Toggle = ({ label, hint, checked, onChange }) => (
  <label className="toggle">
    <input type="checkbox" checked={!!checked} onChange={(e) => onChange(e.target.checked)} />
    <span>
      <b>{label}</b>
      {hint && <small>{hint}</small>}
    </span>
  </label>
);
