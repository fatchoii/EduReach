import React from 'react';

export const DashboardCard = ({ label, value, icon }) => (
  <div className="card stat">
    <span className="ic">{icon}</span>
    <div>
      <b>{value}</b>
      <small>{label}</small>
    </div>
  </div>
);
