import React, { useState } from 'react';
import { lessons } from '../../services/contentService';
import { Badge } from '../common/Badge';
import { Button } from '../common/Button';
import { Card } from '../common/Card';
import { FormInput } from '../common/FormInput';
import { LessonViewer } from '../common/LessonViewer';
import { statusLabel } from '../../utils/format';

const TYPES = ['TEXT', 'VIDEO', 'AUDIO', 'SLIDES'];

/** Create or edit one lesson (text, video, audio or slides), preview it, and publish it. */
export const LessonEditor = ({ moduleId, lesson, defaultType = 'TEXT', onSaved, onCancel }) => {
  const [f, setF] = useState({
    title: lesson ? lesson.title : '',
    description: lesson ? lesson.description || '' : '',
    instructions: lesson ? lesson.instructions || '' : '',
    textContent: lesson ? lesson.textContent || '' : '',
    contentType: lesson ? lesson.contentType || 'TEXT' : defaultType,
    contentUrl: lesson ? lesson.contentUrl || '' : '',
    durationMinutes: lesson && lesson.durationMinutes ? lesson.durationMinutes : '',
    downloadable: lesson ? lesson.downloadable !== false : true,
  });
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');

  const set = (k) => (e) => setF({ ...f, [k]: e.target.value });

  const save = async (publish) => {
    if (!f.title.trim()) return setError('Give the lesson a title.');
    setBusy(true);
    setError('');
    try {
      const payload = {
        ...(lesson || {}),
        moduleId,
        title: f.title.trim(),
        description: f.description,
        instructions: f.instructions,
        textContent: f.textContent,
        contentType: f.contentType,
        contentUrl: f.contentUrl.trim() || null,
        durationMinutes: f.durationMinutes === '' ? null : Number(f.durationMinutes),
        downloadable: f.downloadable,
      };
      const saved = lesson ? await lessons.update(lesson.id, payload) : await lessons.create(payload);
      if (publish) await lessons.setPublished(saved.id, true);
      onSaved();
    } catch (ex) {
      setError(ex.message);
    } finally {
      setBusy(false);
    }
  };

  const unpublish = async () => {
    setBusy(true);
    try {
      await lessons.setPublished(lesson.id, false);
      onSaved();
    } catch (ex) {
      setError(ex.message);
    } finally {
      setBusy(false);
    }
  };

  const remove = async () => {
    if (!window.confirm('Delete this lesson?')) return;
    setBusy(true);
    try {
      await lessons.remove(lesson.id);
      onSaved();
    } catch (ex) {
      setError(ex.message);
      setBusy(false);
    }
  };

  const needsUrl = f.contentType !== 'TEXT';

  return (
    <div className="grid2">
      <Card
        title={lesson ? 'Edit lesson' : 'New lesson'}
        right={lesson && <Badge t={statusLabel(lesson.status)} />}
      >
        {error && <p className="err">{error}</p>}
        <FormInput label="Title" value={f.title} onChange={set('title')} />
        <FormInput label="Content type">
          <select value={f.contentType} onChange={set('contentType')}>
            {TYPES.map((t) => (
              <option key={t} value={t}>
                {statusLabel(t)}
              </option>
            ))}
          </select>
        </FormInput>
        {needsUrl && (
          <FormInput
            label={`${statusLabel(f.contentType)} link (URL)`}
            placeholder="https://..."
            value={f.contentUrl}
            onChange={set('contentUrl')}
          />
        )}
        <FormInput label={needsUrl ? 'Notes shown under the media (optional)' : 'Lesson text'}>
          <textarea value={f.textContent} onChange={set('textContent')} />
        </FormInput>
        <FormInput label="Short description" value={f.description} onChange={set('description')} />
        <FormInput label="How to (instructions)" value={f.instructions} onChange={set('instructions')} />
        <div className="row">
          <FormInput
            label="Minutes"
            type="number"
            min="0"
            value={f.durationMinutes}
            onChange={set('durationMinutes')}
          />
          <label>
            <input
              type="checkbox"
              checked={f.downloadable}
              onChange={(e) => setF({ ...f, downloadable: e.target.checked })}
            />{' '}
            Learners can download for offline use
          </label>
        </div>
        <div className="row">
          <Button disabled={busy} onClick={() => save(false)}>
            Save draft
          </Button>
          <Button v="ghost" disabled={busy} onClick={() => save(true)}>
            Save and publish
          </Button>
          {lesson && lesson.status === 'PUBLISHED' && (
            <Button v="ghost" disabled={busy} onClick={unpublish}>
              Unpublish
            </Button>
          )}
          {lesson && (
            <Button v="danger" disabled={busy} onClick={remove}>
              Delete
            </Button>
          )}
          <Button v="ghost" onClick={onCancel}>
            Close
          </Button>
        </div>
      </Card>

      <Card title="Content preview">
        <LessonViewer lesson={{ ...f, title: f.title || 'Untitled lesson' }} />
      </Card>
    </div>
  );
};
