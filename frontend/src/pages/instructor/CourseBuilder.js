import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import {
  AssessmentManager,
  Badge,
  Button,
  Card,
  LessonEditor,
  LiveSessionManager,
  PageHeader,
} from '../../components';
import { useAsync } from '../../hooks/useAsync';
import { courses, lessons, modules } from '../../services/contentService';
import { statusLabel } from '../../utils/format';

export function CourseBuilder() {
  const id = Number(useParams().courseId);
  const course = useAsync(() => courses.get(id), [id]);
  const mods = useAsync(() => modules.list({ courseId: id }), [id]);

  const [moduleId, setModuleId] = useState(null);
  const [editing, setEditing] = useState(undefined); // undefined = closed, null = new lesson, object = edit
  const [newType, setNewType] = useState('TEXT');
  const [title, setTitle] = useState('');
  const [msg, setMsg] = useState({ text: '', ok: false });

  const moduleList = mods.data
    ? [...mods.data].sort((a, b) => (a.orderIndex || 0) - (b.orderIndex || 0))
    : [];

  useEffect(() => {
    if (moduleId === null && moduleList.length) setModuleId(moduleList[0].id);
  }, [moduleList, moduleId]);

  const current = moduleList.find((m) => m.id === moduleId) || null;
  const lessonData = useAsync(
    () => (moduleId ? lessons.list({ moduleId }) : Promise.resolve([])),
    [moduleId]
  );

  const run = async (fn, okText) => {
    setMsg({ text: '', ok: false });
    try {
      await fn();
      if (okText) setMsg({ text: okText, ok: true });
    } catch (ex) {
      setMsg({ text: ex.message, ok: false });
    }
  };

  const addModule = (e) => {
    e.preventDefault();
    if (!title.trim()) return;
    run(async () => {
      const created = await modules.create({ courseId: id, title: title.trim(), orderIndex: moduleList.length + 1 });
      setTitle('');
      setModuleId(created.id);
      mods.reload();
    });
  };

  const removeModule = (m) => {
    if (!window.confirm(`Delete module "${m.title}" with its lessons and quizzes?`)) return;
    run(async () => {
      await modules.remove(m.id);
      setModuleId(null);
      setEditing(undefined);
      mods.reload();
    });
  };

  const togglePublish = () =>
    run(async () => {
      const publish = course.data.status !== 'PUBLISHED';
      await courses.setPublished(id, publish);
      course.reload();
    }, 'Course status updated. Enrolled learners are notified when a course is published.');

  const afterLessonSaved = () => {
    setEditing(undefined);
    lessonData.reload();
  };

  if (course.loading) return <p>Loading course...</p>;
  if (course.error) return <p className="err">{course.error}</p>;

  return (
    <>
      <PageHeader t={course.data.title}>
        <div className="row">
          <Badge t={statusLabel(course.data.status)} />
          <Button onClick={togglePublish}>
            {course.data.status === 'PUBLISHED' ? 'Unpublish course' : 'Publish course'}
          </Button>
          <Link className="btn ghost" to="/instructor/courses">
            All courses
          </Link>
        </div>
      </PageHeader>
      {msg.text && <p className={msg.ok ? 'ok' : 'err'}>{msg.text}</p>}

      <div className="builder">
        <Card title="Course modules">
          {moduleList.map((m) => (
            <div key={m.id} className={`lc ${m.id === moduleId ? 'on' : ''}`} onClick={() => { setModuleId(m.id); setEditing(undefined); }}>
              <div className="ch">
                <b>
                  {m.orderIndex}. {m.title}
                </b>
                <Button v="danger" onClick={(e) => { e.stopPropagation(); removeModule(m); }}>
                  ✕
                </Button>
              </div>
            </div>
          ))}
          {moduleList.length === 0 && <p className="muted">Add the first module.</p>}
          <form onSubmit={addModule} className="row">
            <input placeholder="New module title" value={title} onChange={(e) => setTitle(e.target.value)} />
            <Button>Add</Button>
          </form>
        </Card>

        <Card title={current ? `Lessons: ${current.title}` : 'Lessons'}>
          {!current && <p className="muted">Select or add a module.</p>}
          {current && lessonData.data && lessonData.data.map((l) => (
            <div key={l.id} className="lc" onClick={() => setEditing(l)}>
              <div className="ch">
                <b>{l.title}</b>
                <Badge t={statusLabel(l.status)} />
              </div>
              <small className="muted">{statusLabel(l.contentType)}</small>
            </div>
          ))}
          {current && lessonData.data && lessonData.data.length === 0 && (
            <p className="muted">No lessons in this module yet.</p>
          )}
          {current && (
            <div className="row">
              <b>Add:</b>
              {['TEXT', 'VIDEO', 'AUDIO', 'SLIDES'].map((t) => (
                <Button key={t} v="ghost" onClick={() => { setNewType(t); setEditing(null); }}>
                  {statusLabel(t)}
                </Button>
              ))}
            </div>
          )}
        </Card>
      </div>

      {current && editing !== undefined && (
        <LessonEditor
          key={editing ? editing.id : `new-${newType}-${moduleId}`}
          moduleId={moduleId}
          lesson={editing}
          defaultType={newType}
          onSaved={afterLessonSaved}
          onCancel={() => setEditing(undefined)}
        />
      )}

      {current && <AssessmentManager key={current.id} module={current} />}
      <LiveSessionManager courseId={id} />
    </>
  );
}
