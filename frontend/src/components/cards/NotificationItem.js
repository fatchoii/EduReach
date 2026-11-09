import React from 'react';

export const NotificationItem = ({ n, onClick }) => (
  <div
    className={`ni ${n[3] ? '' : 'unread'}`}
    onClick={onClick}
  >
    <span className="dot" />

    <div>
      <b>{n[0]}</b>
      <p>{n[1]}</p>
    </div>

    <small>{n[2]}</small>
  </div>
);
