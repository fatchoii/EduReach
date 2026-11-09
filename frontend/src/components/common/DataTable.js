import React from 'react';

export const DataTable = ({ cols, rows, onRow }) => (
  <div className="tw">
    <table>
      <thead>
        <tr>
          {cols.map((c) => (
            <th key={c}>{c}</th>
          ))}
        </tr>
      </thead>

      <tbody>
        {rows.map((r, i) => (
          <tr
            key={i}
            onClick={onRow && (() => onRow(i))}
            className={onRow ? 'click' : ''}
          >
            {r.map((c, j) => (
              <td key={j} data-l={cols[j]}>
                {c}
              </td>
            ))}
          </tr>
        ))}
      </tbody>
    </table>
  </div>
);
