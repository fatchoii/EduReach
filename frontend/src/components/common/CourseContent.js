import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { Badge } from './Badge';
import { Button } from './Button';
import { Card } from './Card';
import { LessonViewer } from './LessonViewer';
import { formatDate, statusLabel } from '../../utils/format';

/**
 * Modules, lessons and quizzes of a course, with a viewer for the selected lesson.
 * Used for both the online course and the downloaded (offline) copy.
 */
export const CourseContent = ({ modules, completed, scores = {}, onComplete, quizPath, busy }) => {
  const firstLesson = modules.flatMap((m) => m.lessons)[0];
  const [selectedId, setSelectedId] = useState(firstLesson ? firstLesson.id : null);

  const done = new Set(completed);
  const selected = modules.flatMap((m) => m.lessons).find((l) => l.id === selectedId) || null;

  return (
    <div className="builder">
      <Card title="Modules">
        {modules.length === 0 && <p className="muted">No content has been published yet.</p>}
        {modules.map((m) => (
          <div key={m.id} className="mod">
            <b>{m.title}</b>
            {m.lessons.map((l) => (
              <div
                key={l.id}
                className={`lc ${l.id === selectedId ? 'on' : ''}`}
                onClick={() => setSelectedId(l.id)}
              >
                <span>
                  {done.has(l.id) ? '✅' : '▫️'} {l.title}
                </span>
                <small className="muted">
                  {statusLabel(l.contentType)}
                  {l.durationMinutes ? ` - ${l.durationMinutes} min` : ''}
                </small>
              </div>
            ))}
            {m.assessments.map((a) => {
              const result = scores[a.id];
              return (
                <div key={a.id} className="lc quiz-row">
                  <span>
                    📝 {a.title} <small className="muted">due {formatDate(a.dueDate)}</small>
                  </span>
                  <span className="row">
                    {result && <Badge t={`${Math.round(result.score)}% ${statusLabel(result.status)}`} />}
                    <Link className="btn primary" to={`${quizPath}/${a.id}`}>
                      {result ? 'Retake' : 'Start'}
                    </Link>
                  </span>
                </div>
              );
            })}
          </div>
        ))}
      </Card>

      <Card title="Lesson">
        <LessonViewer lesson={selected} />
        {selected && (
          <div className="row">
            {done.has(selected.id) ? (
              <Badge t="Completed" />
            ) : (
              <Button disabled={busy} onClick={() => onComplete(selected.id)}>
                Mark as complete
              </Button>
            )}
          </div>
        )}
      </Card>
    </div>
  );
};
