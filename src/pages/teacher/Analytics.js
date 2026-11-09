import React from 'react';
import { SKILLS, assignments0, students } from '../../data';
import {
  Card,
  Chart,
  DashboardCard,
  DataTable,
  PageHeader,
  ProgressBar,
  SkillCard,
} from '../../components';
import { skillAvg } from '../../utils/skillStats';

export function Analytics() {
  const weak = [...skillAvg].sort((a, b) => a[1] - b[1]).slice(0, 2);

  return (
    <>
      <PageHeader t="Skill Analytics" />

      <div className="grid3">
        <DashboardCard
          icon="🏫"
          label="Overall Class Mastery"
          value={
            Math.round(
              students.reduce((a, s) => a + s.overall, 0) / students.length
            ) + '%'
          }
        />

        <DashboardCard
          icon="📈"
          label="Strongest Skill"
          value={[...skillAvg].sort((a, b) => b[1] - a[1])[0][0]}
        />

        <DashboardCard
          icon="⚠️"
          label="Weakest Skill"
          value={weak[0][0]}
        />
      </div>

      <div className="grid2">
        <Card title="Skill Mastery">
          <Chart
            data={skillAvg.map(([s, v]) => [
              s.split(' ').slice(-1)[0],
              v,
            ])}
          />
        </Card>

        <Card title="Skills Needing Attention">
          {weak.map(([s, v]) => (
            <SkillCard key={s} skill={s} value={v} />
          ))}
        </Card>
      </div>

      <Card title="Individual Student Mastery">
        <DataTable
          cols={['Student', 'Overall', 'Weakest skill']}
          rows={students.map((s) => [
            s.name,
            <ProgressBar value={s.overall} />,
            SKILLS[s.m.indexOf(Math.min(...s.m))],
          ])}
        />
      </Card>

      <Card title="Assignment Performance">
        <DataTable
          cols={['Assignment', 'Skill', 'Completion']}
          rows={assignments0.map((a) => [
            a.name,
            a.skill,
            <ProgressBar value={a.done} />,
          ])}
        />
      </Card>
    </>
  );
}
