import React from 'react';
import { Badge } from '../common/Badge';
import { ProgressBar } from '../common/ProgressBar';

export const AssignmentCard = ({ a }) => (
  <div className="card">
    <div className="ch">
      <h4>{a.name}</h4>
      <Badge t={a.status} />
    </div>

    <small>
      {a.skill} · {a.cls}
    </small>

    <ProgressBar
      value={a.done}
      label="Completion"
    />

    <small>Due {a.due}</small>
  </div>
);
