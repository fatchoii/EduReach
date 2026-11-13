import React from 'react';
import { Badge, Card, DashboardCard, DataTable, PageHeader } from '../../components';
import { useAsync } from '../../hooks/useAsync';
import { getSyncLogs, getUsers } from '../../services/adminService';
import { formatDateTime, statusLabel } from '../../utils/format';

export function AdminDashboard() {
  const { data, loading, error } = useAsync(
    async () => {
      const [users, logs] = await Promise.all([getUsers(), getSyncLogs()]);
      return { users, logs };
    },
    [],
    { refreshMs: 15000 }
  );

  if (loading) return <p>Loading...</p>;
  if (error) return <p className="err">{error}</p>;

  const count = (role) => data.users.filter((u) => u.role === role).length;

  return (
    <>
      <PageHeader t="Admin dashboard" />
      <div className="grid4">
        <DashboardCard icon="👥" label="Users" value={data.users.length} />
        <DashboardCard icon="🎓" label="Learners" value={count('LEARNER')} />
        <DashboardCard icon="🧑‍🏫" label="Instructors" value={count('INSTRUCTOR')} />
        <DashboardCard icon="🔄" label="Sync events" value={data.logs.length} />
      </div>
      <Card title="Latest sync activity">
        <DataTable
          cols={['Learner', 'Event', 'Status', 'When']}
          rows={data.logs.slice(0, 8).map((l) => [
            l.userName,
            statusLabel(l.eventType),
            <Badge t={statusLabel(l.status)} />,
            formatDateTime(l.syncedAt),
          ])}
        />
        {data.logs.length === 0 && <p className="muted">No sync activity yet.</p>}
      </Card>
    </>
  );
}
