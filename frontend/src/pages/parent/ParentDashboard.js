import React from 'react';
import { Card, PageHeader, ProgressBar } from '../../components';
import { useAsync } from '../../hooks/useAsync';
import { getCurrentUser } from '../../services/authService';
import { loadParentDashboard } from '../../services/parentService';

export function ParentDashboard() {
  const user = getCurrentUser();
  const { data, loading, error } = useAsync(() => loadParentDashboard(user.id), [user.id], { refreshMs: 20000 });

  if (loading) return <p>Loading...</p>;
  if (error) return <p className="err">{error}</p>;

  return (
    <>
      <PageHeader t="My children" />
      {data.length === 0 && <p className="muted">No child is linked to your account yet. Ask the instructor to link one.</p>}
      {data.map((child) => {
        const scored = child.mastery.skills.filter((s) => s.attempts > 0);
        const weakest = scored.length ? scored.reduce((low, cur) => (cur.score < low.score ? cur : low)) : null;
        return (
          <div key={child.studentId}>
            <h3>{child.studentName}</h3>
            <div className="grid2">
              <Card title="Overall mastery">
                <div className="big">{Math.round(child.mastery.overall)}%</div>
                <ProgressBar value={Math.round(child.mastery.overall)} />
              </Card>
              <Card title="Suggested practice">
                {weakest ? (
                  <p>
                    {child.studentName.split(' ')[0]} may benefit from more practice with{' '}
                    <b>{weakest.moduleTitle.toLowerCase()}</b>. Try 10 minutes a day using a number line.
                  </p>
                ) : (
                  <p className="muted">No quiz results yet.</p>
                )}
              </Card>
            </div>
            <Card title="Skill progress">
              {scored.map((s) => (
                <ProgressBar key={s.moduleId} label={s.moduleTitle} value={Math.round(s.score)} />
              ))}
              {scored.length === 0 && <p className="muted">No quiz results yet.</p>}
            </Card>
          </div>
        );
      })}
    </>
  );
}
