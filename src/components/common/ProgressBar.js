import React from 'react';
import { getProgressColor } from '../../utils/progressColor';

export const ProgressBar = ({ value, label }) => (
  <div className="pb">
    {label && (
      <small>
        {label}
        <b>{value}%</b>
      </small>
    )}

    <div>
      <i
        style={{
          width: value + '%',
          background: getProgressColor(value)
        }}
      />
    </div>
  </div>
);
