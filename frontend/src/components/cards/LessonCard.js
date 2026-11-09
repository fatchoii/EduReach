import React from 'react';
import { Badge } from '../common/Badge';

export const LessonCard = ({ l, active, onClick }) => (
  <div
    className={`lc ${active ? 'on' : ''}`}
    onClick={onClick}
  >
    <b>{l.title}</b>
    <small>{l.desc}</small>
    <Badge t={l.status} />
  </div>
);
