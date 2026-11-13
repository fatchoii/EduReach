import React, { useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Badge, CourseContent, PageHeader } from '../../components';
import { useOnlineStatus } from '../../hooks/useOnlineStatus';
import { addLocalCompleted, getDownload, getLocalCompleted, queueEvent, syncNow } from '../../services/offlineService';
import { formatDateTime } from '../../utils/format';

/** The downloaded copy of a course. Works without a connection; progress waits in the sync queue. */
export function OfflineCourse() {
  const id = Number(useParams().courseId);
  const pkg = getDownload(id);
  const online = useOnlineStatus();
  const [completed, setCompleted] = useState(() => getLocalCompleted(id));
  const [msg, setMsg] = useState('');

  if (!pkg) {
    return (
      <>
        <p className="err">This course has not been downloaded.</p>
        <Link to="/learner/offline">Offline downloads</Link>
      </>
    );
  }

  const complete = async (lessonId) => {
    addLocalCompleted(id, lessonId);
    queueEvent({ type: 'LESSON_COMPLETE', lessonId, label: 'Lesson completed' });
    setCompleted(getLocalCompleted(id));
    if (navigator.onLine) {
      try {
        await syncNow();
        setMsg('Saved and synced.');
      } catch {
        setMsg('Saved on this device. It will sync automatically.');
      }
    } else {
      setMsg('Saved on this device. It will sync automatically when you are back online.');
    }
  };

  return (
    <>
      <PageHeader t={`${pkg.courseTitle} (offline copy)`}>
        <div className="row">
          <Badge t={online ? 'Online' : 'Offline'} />
          <small className="muted">Downloaded {formatDateTime(pkg.downloadedAt)}</small>
          <Link className="btn ghost" to="/learner/offline">
            Back
          </Link>
        </div>
      </PageHeader>
      {msg && <p className="ok">{msg}</p>}
      <CourseContent
        modules={pkg.modules}
        completed={completed}
        onComplete={complete}
        quizPath={`/learner/offline/${id}/quiz`}
      />
    </>
  );
}
