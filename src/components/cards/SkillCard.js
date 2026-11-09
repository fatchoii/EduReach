import React from 'react';
import { ProgressBar } from '../common/ProgressBar';

export const SkillCard = ({ skill, value }) => (
  <div className="card">
    <ProgressBar label={skill} value={value} />
  </div>
);
