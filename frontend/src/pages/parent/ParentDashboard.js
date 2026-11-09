import React from 'react';
import { SKILLS } from '../../data';
import { Card, PageHeader, ProgressBar } from '../../components';

export function ParentDashboard() {
  const m = [90, 72, 85, 65, 60];
  const o = Math.round(m.reduce((a, b) => a + b) / 5);
  const w = m.indexOf(Math.min(...m));

  return (
    <>
      <PageHeader t="Ana Reyes" />

      <div className="grid2">
        <Card title="Overall Mastery">
          <div className="big">{o}%</div>
          <ProgressBar value={o} />
        </Card>

        <Card title="Suggested Practice">
          <p>
            Your child may benefit from additional practice with{' '}
            {SKILLS[w].toLowerCase()}. Try 10 minutes a day using a number line.
          </p>
        </Card>
      </div>

      <Card title="Skill Progress">
        {SKILLS.map((s, i) => (
          <ProgressBar key={s} label={s} value={m[i]} />
        ))}
      </Card>

      <Card title="Recent Activity">
        <ul>
          <li>Completed Regrouping Practice 1 (4/4)</li>
          <li>Started Borrowing Basics</li>
          <li>Earned "Addition Star" badge</li>
        </ul>
      </Card>
    </>
  );
}
