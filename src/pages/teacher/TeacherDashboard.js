import React from 'react';
import {
  SKILLS,
  activity,
  assignments0,
  students,
  submissions,
} from '../../data';
import {
  Card,
  DashboardCard,
  PageHeader,
  ProgressBar,
} from '../../components';
import { skillAvg } from '../../utils/skillStats';

export function TeacherDashboard() {
  const low = students.filter((s) => s.overall < 70);

  return (
    <>
      <PageHeader t="Dashboard" />

      <div className="grid4">
        <DashboardCard
          icon="🎓"
          label="Total Students"
          value={students.length}
        />

        <DashboardCard
          icon="📝"
          label="Active Assignments"
          value={assignments0.filter((a) => a.status === 'Active').length}
        />

        <DashboardCard
          icon="⏳"
          label="Pending Submissions"
          value={submissions.filter((s) => s.status !== 'Graded').length}
        />

        <DashboardCard
          icon="⭐"
          label="Average Skill Mastery"
          value={Math.round(
            skillAvg.reduce((a, b) => a + b[1], 0) / 5
          ) + '%'}
        />
      </div>

      <div className="grid2">
        <Card title="Skill Mastery">
          {skillAvg.map(([s, v]) => (
            <ProgressBar key={s} label={s} value={v} />
          ))}
        </Card>

        <Card title="Needs Attention">
          <ul>
            {low.map((s) => (
              <li key={s.id}>
                <b>{s.name}</b>: weakest in{' '}
                {SKILLS[s.m.indexOf(Math.min(...s.m))]} ({Math.min(...s.m)}%)
              </li>
            ))}
          </ul>
        </Card>
      </div>

      <Card title="Recent Activity">
        <ul>
          {activity.map((a) => (
            <li key={a}>{a}</li>
          ))}
        </ul>
      </Card>
    </>
  );
}
