import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Card, CourseContent, PageHeader } from '../../components';
import { useAsync } from '../../hooks/useAsync';
import { completeLesson, loadCourseContent } from '../../services/learnerService';
import { addLocalCompleted, downloadCourse, getDownload, queueEvent, subscribe } from '../../services/offlineService';
import { formatBytes } from '../../utils/format';

export function LearnerCourse() {
  const id = Number(useParams().courseId);
  const { data, loading, error, reload } = useAsync(() => loadCourseContent(id), [id]);
  const [extraDone, setExtraDone] = useState([]);
  const [busy, setBusy] = useState(false);
  const [msg, setMsg] = useState({ text: '', ok: false });
  const [download, setDownload] = useState(() => getDownload(id));

  useEffect(() => subscribe(() => setDownload(getDownload(id))), [id]);

  const complete = async (lessonId) => {
    setBusy(true);
    setMsg({ text: '', ok: false });
    try {
      if (!navigator.onLine) {
        addLocalCompleted(id, lessonId);
        queueEvent({ type: 'LESSON_COMPLETE', lessonId, label: 'Lesson completed' });
        setExtraDone((list) => [...list, lessonId]);
        setMsg({ text: 'You are offline. Saved on this device and it will sync automatically.', ok: true });
      } else {
        const progress = await completeLesson(lessonId);
        setExtraDone((list) => [...list, lessonId]);
        setMsg({ text: `Lesson completed. Course progress: ${Math.round(progress.progressPercent)}%`, ok: true });
        reload();
      }
    } catch (ex) {
      setMsg({ text: ex.message, ok: false });
    } finally {
      setBusy(false);
    }
  };

  const save = async () => {
    setBusy(true);
    setMsg({ text: '', ok: false });
    try {
      const pkg = await downloadCourse(id);
      setMsg({ text: `Downloaded for offline use (${formatBytes(pkg.sizeBytes)}).`, ok: true });
    } catch (ex) {
      setMsg({ text: ex.message, ok: false });
    } finally {
      setBusy(false);
    }
  };

  if (loading) return <p>Loading course...</p>;
  if (error) {
    return (
      <>
        <p className="err">{error}</p>
        {download && <Link to={`/learner/offline/${id}`}>Open the downloaded copy</Link>}
      </>
    );
  }

  return (
    <>
      <PageHeader t={data.course.title}>
        <div className="row">
          <Button v="ghost" disabled={busy} onClick={save}>
            {download ? 'Update offline copy' : 'Download for offline'}
          </Button>
          {download && <Link className="btn ghost" to={`/learner/offline/${id}`}>Open offline copy</Link>}
          <Link className="btn ghost" to="/learner/dashboard">Back</Link>
        </div>
      </PageHeader>
      {msg.text && <p className={msg.ok ? 'ok' : 'err'}>{msg.text}</p>}
      {data.course.description && <Card><p>{data.course.description}</p></Card>}
      <CourseContent
        modules={data.modules}
        completed={[...data.completed, ...extraDone]}
        scores={data.scores}
        onComplete={complete}
        quizPath="/learner/quiz"
        busy={busy}
      />
    </>
  );
}
