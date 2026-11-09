import React from 'react';
import { getProgressColor } from '../../utils/progressColor';

export const Chart = ({ data }) => (
  <div className="chart">
    {data.map(([l, v]) => (
      <div key={l}>
        <i
          style={{
            height: v + '%',
            background: getProgressColor(v)
          }}
        >
          <b>{v}%</b>
        </i>
        <small>{l}</small>
      </div>
    ))}
  </div>
);
