import React, { useState } from 'react';
import { useAsync } from '../../hooks/useAsync';
import { liveSessions } from '../../services/contentService';
import { Button } from '../common/Button';
import { Card } from '../common/Card';
import { DataTable } from '../common/DataTable';
import { formatDateTime } from '../../utils/format';

/** Schedule live sessions. Learners enrolled in the course are sent an invite notification. */
export const LiveSessionManager = ({ courseId }) => {
  const { data, loading, error, reload } = useAsync(() => liveSessions.list({ courseId }), [courseId]);
  const [f, setF] = useState({ title: '', scheduledAt: '', meetingUrl: '' });
  const [msg, setMsg] = useState({ text: '', ok: false });

  const set = (k) => (e) => setF({ ...f, [k]: e.target.value });

  const create = async (e) => {
    e.preventDefault();
    if (!f.title.trim() || !f.scheduledAt) return setMsg({ text: 'Add a title and a date and time.', ok: false });
    try {
      await liveSessions.create({
        courseId,
        title: f.title.trim(),
        scheduledAt: f.scheduledAt,
        meetingUrl: f.meetingUrl.trim() || null,
      });
      setF({ title: '', scheduledAt: '', meetingUrl: '' });
      setMsg({ text: 'Session scheduled. Invites were sent to the enrolled learners.', ok: true });
      reload();
    } catch (ex) {
      setMsg({ text: ex.message, ok: false });
    }
  };

  const remove = async (id) => {
    try {
      await liveSessions.remove(id);
      reload();
    } catch (ex) {
      setMsg({ text: ex.message, ok: false });
    }
  };

  return (
    <Card title="Live sessions">
      {msg.text && <p className={msg.ok ? 'ok' : 'err'}>{msg.text}</p>}
      {loading && <p>Loading...</p>}
      {error && <p className="err">{error}</p>}
      {data && data.length > 0 && (
        <DataTable
          cols={['Session', 'When', 'Link', '']}
          rows={data.map((s) => [
            s.title,
            formatDateTime(s.scheduledAt),
            s.meetingUrl ? (
              <a href={s.meetingUrl} target="_blank" rel="noreferrer">
                Join link
              </a>
            ) : (
              '-'
            ),
            <Button v="danger" onClick={() => remove(s.id)}>
              Delete
            </Button>,
          ])}
        />
      )}
      {data && data.length === 0 && <p className="muted">No live sessions scheduled.</p>}
      <form onSubmit={create} className="row top-gap">
        <input placeholder="Session title" value={f.title} onChange={set('title')} />
        <input type="datetime-local" value={f.scheduledAt} onChange={set('scheduledAt')} />
        <input placeholder="Meeting link (Meet, Zoom...)" value={f.meetingUrl} onChange={set('meetingUrl')} />
        <Button>Schedule and invite</Button>
      </form>
    </Card>
  );
};
