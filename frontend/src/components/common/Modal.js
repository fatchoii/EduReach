import React from 'react';
import { Button } from './Button';

export const Modal = ({ title, onClose, children }) => (
  <div className="mo" onClick={onClose}>
    <div
      className="md"
      onClick={(e) => e.stopPropagation()}
    >
      <div className="ch">
        <h3>{title}</h3>
        <Button v="ghost" onClick={onClose}>
          ✕
        </Button>
      </div>

      {children}
    </div>
  </div>
);
