import React from 'react';
import { Card, DashboardCard, PageHeader, ProgressBar } from '../../components';
import { useAsync } from '../../hooks/useAsync';
import { getCurrentUser } from '../../services/authService';
import { loadTeacherDashboard } from '../../services/teacherDashboardService';

export function TeacherDashboard() {
  const user = getCurrentUser();
  const { data, loading, error } = useAsync(() => loadTeacherDashboard(user.id), [user.id]);

  if (loading) {
    return (
      <>
        <PageHeader t="Dashboard" />
        <p>Loading dashboard...</p>
      </>
    );
  }

  if (error) {
    return (
      <>
        <PageHeader t="Dashboard" />
        <p className="err">{error}</p>
      </>
    );
  }

  return (
    <>
      <PageHeader t="Dashboard" />

      <div className="grid4">
        <DashboardCard icon="🎓" label="Total Students" value={data.studentCount} />
        <DashboardCard icon="📝" label="Active Assignments" value={data.activeAssignments} />
        <DashboardCard icon="📥" label="Submissions Received" value={data.submissionCount} />
        <DashboardCard icon="⭐" label="Average Skill Mastery" value={data.averageMastery + '%'} />
      </div>

      <div className="grid2">
        <Card title="Skill Mastery">
          {data.skills.length ? (
            data.skills.map(([s, v]) => <ProgressBar key={s} label={s} value={v} />)
          ) : (
            <p>No submissions yet.</p>
          )}
        </Card>

        <Card title="Needs Attention">
          {data.needsAttention.length ? (
            <ul>
              {data.needsAttention.map((s) => (
                <li key={s.id}>
                  <b>{s.name}</b>: weakest in {s.skill} ({s.score}%)
                </li>
              ))}
            </ul>
          ) : (
            <p>No students need attention right now.</p>
          )}
        </Card>
      </div>

      <Card title="Recent Activity">
        {data.activity.length ? (
          <ul>
            {data.activity.map((a) => (
              <li key={a}>{a}</li>
            ))}
          </ul>
        ) : (
          <p>No activity yet.</p>
        )}
      </Card>
    </>
  );
}
