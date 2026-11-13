import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Button, Card, DashboardCard, PageHeader, ProgressBar } from '../../components';
import { useAsync } from '../../hooks/useAsync';
import { enroll, loadLearnerDashboard } from '../../services/learnerService';
import { formatDate, formatDateTime } from '../../utils/format';

export function LearnerDashboard() {
  // Progress refreshes by itself every 10 seconds.
  const { data, loading, error, reload } = useAsync(loadLearnerDashboard, [], { refreshMs: 10000 });
  const [msg, setMsg] = useState('');

  const join = async (courseId) => {
    setMsg('');
    try {
      await enroll(courseId);
      reload();
    } catch (ex) {
      setMsg(ex.message);
    }
  };

  if (loading) return <p>Loading your courses...</p>;
  if (error) return <p className="err">{error}</p>;

  const average = data.progress.length
    ? Math.round(data.progress.reduce((s, p) => s + p.progressPercent, 0) / data.progress.length)
    : 0;

  return (
    <>
      <PageHeader t="My learning">
        <small className="muted">Progress updates automatically</small>
      </PageHeader>

      <div className="grid3">
        <DashboardCard icon="📚" label="Courses" value={data.progress.length} />
        <DashboardCard icon="⭐" label="Average progress" value={`${average}%`} />
        <DashboardCard icon="📝" label="Quizzes to do" value={data.upcoming.length} />
      </div>

      <h3>My courses</h3>
      {data.progress.length === 0 && <p className="muted">You are not enrolled in a course yet.</p>}
      <div className="grid2">
        {data.progress.map((p) => (
          <Card key={p.courseId} title={p.courseTitle}>
            <ProgressBar label="Course progress" value={Math.round(p.progressPercent)} />
            <p className="muted">
              Lessons {p.lessonsCompleted}/{p.lessonsTotal} - Quizzes {p.assessmentsDone}/{p.assessmentsTotal}
            </p>
            <Link className="btn primary" to={`/learner/courses/${p.courseId}`}>
              {p.progressPercent > 0 ? 'Continue' : 'Start'}
            </Link>
          </Card>
        ))}
      </div>

      <div className="grid2">
        <Card title="Upcoming quizzes and assignments">
          {data.upcoming.length === 0 && <p className="muted">You are all caught up.</p>}
          <ul>
            {data.upcoming.map((a) => (
              <li key={a.id}>
                <Link to={`/learner/quiz/${a.id}`}>{a.title}</Link> <small className="muted">({a.courseTitle}, due {formatDate(a.dueDate)})</small>
              </li>
            ))}
          </ul>
        </Card>

        <Card title="Live sessions">
          {data.sessions.length === 0 && <p className="muted">No live sessions scheduled.</p>}
          <ul>
            {data.sessions.map((s) => (
              <li key={s.id}>
                <b>{s.title}</b> <small className="muted">{s.courseTitle}, {formatDateTime(s.scheduledAt)}</small>{' '}
                {s.meetingUrl && (
                  <a href={s.meetingUrl} target="_blank" rel="noreferrer">
                    Join
                  </a>
                )}
              </li>
            ))}
          </ul>
        </Card>
      </div>

      <Card title="Available courses">
        {msg && <p className="err">{msg}</p>}
        {data.available.length === 0 && <p className="muted">No other courses are open right now.</p>}
        {data.available.map((c) => (
          <div key={c.id} className="ch">
            <span>
              <b>{c.title}</b> <small className="muted">{c.instructorName}</small>
            </span>
            <Button onClick={() => join(c.id)}>Enroll</Button>
          </div>
        ))}
      </Card>
    </>
  );
}
