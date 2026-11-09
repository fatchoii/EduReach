import React from 'react';

export const Card = ({ title, children, right }) => (
  <section className="card">
    {title && (
      <div className="ch">
        <h3>{title}</h3>
        {right}
      </div>
    )}
    {children}
  </section>
);
