import React, { useState } from 'react';
import { useAsync } from '../../hooks/useAsync';
import { assessments, questions } from '../../services/contentService';
import { Badge } from '../common/Badge';
import { Button } from '../common/Button';
import { Card } from '../common/Card';
import { DataTable } from '../common/DataTable';
import { FormInput } from '../common/FormInput';
import { Modal } from '../common/Modal';
import { formatDate, statusLabel } from '../../utils/format';

const emptyQuestion = { questionText: '', questionType: 'MULTIPLE_CHOICE', options: '', correctAnswer: '', points: 1, difficulty: 'EASY' };

/** Add, edit and remove the questions of one quiz or assignment. */
function QuestionManager({ assessment, onClose }) {
  const { data, loading, error, reload } = useAsync(() => questions.list({ assessmentId: assessment.id }), [assessment.id]);
  const [f, setF] = useState(emptyQuestion);
  const [msg, setMsg] = useState('');

  const set = (k) => (e) => setF({ ...f, [k]: e.target.value });

  const add = async (e) => {
    e.preventDefault();
    setMsg('');
    const options = f.questionType === 'MULTIPLE_CHOICE'
      ? f.options.split('\n').map((o) => o.replace(/\|/g, '').trim()).filter(Boolean)
      : [];
    if (!f.questionText.trim()) return setMsg('Type the question.');
    if (!f.correctAnswer.trim()) return setMsg('Enter the correct answer.');
    if (f.questionType === 'MULTIPLE_CHOICE' && (options.length < 2 || !options.includes(f.correctAnswer.trim()))) {
      return setMsg('Multiple choice needs at least two options (one per line), and the correct answer must match one of them.');
    }
    try {
      await questions.create({
        assessmentId: assessment.id,
        questionText: f.questionText.trim(),
        questionType: f.questionType,
        options: options.join('|'),
        correctAnswer: f.correctAnswer.trim(),
        points: Number(f.points) || 1,
        difficulty: f.difficulty,
      });
      setF(emptyQuestion);
      reload();
    } catch (ex) {
      setMsg(ex.message);
    }
  };

  const remove = async (id) => {
    try {
      await questions.remove(id);
      reload();
    } catch (ex) {
      setMsg(ex.message);
    }
  };

  return (
    <Modal title={`Questions: ${assessment.title}`} onClose={onClose}>
      {loading && <p>Loading...</p>}
      {error && <p className="err">{error}</p>}
      {data && (
        <DataTable
          cols={['Question', 'Type', 'Answer', 'Points', '']}
          rows={data.map((q) => [
            q.questionText,
            statusLabel(q.questionType),
            q.correctAnswer,
            q.points,
            <Button v="danger" onClick={() => remove(q.id)}>
              Delete
            </Button>,
          ])}
        />
      )}
      {data && data.length === 0 && <p className="muted">No questions yet.</p>}

      <form onSubmit={add}>
        <h4>Add a question</h4>
        {msg && <p className="err">{msg}</p>}
        <FormInput label="Question" value={f.questionText} onChange={set('questionText')} />
        <div className="row">
          <FormInput label="Type">
            <select value={f.questionType} onChange={set('questionType')}>
              <option value="MULTIPLE_CHOICE">Multiple choice</option>
              <option value="FILL_IN">Fill in</option>
            </select>
          </FormInput>
          <FormInput label="Points" type="number" min="1" value={f.points} onChange={set('points')} />
          <FormInput label="Difficulty">
            <select value={f.difficulty} onChange={set('difficulty')}>
              <option value="EASY">Easy</option>
              <option value="MEDIUM">Medium</option>
              <option value="HARD">Hard</option>
            </select>
          </FormInput>
        </div>
        {f.questionType === 'MULTIPLE_CHOICE' && (
          <FormInput label="Options (one per line)">
            <textarea value={f.options} onChange={set('options')} />
          </FormInput>
        )}
        <FormInput label="Correct answer" value={f.correctAnswer} onChange={set('correctAnswer')} />
        <Button>Add question</Button>
      </form>
    </Modal>
  );
}

/** Quizzes and assignments of one module: create, add questions, publish. */
export const AssessmentManager = ({ module }) => {
  const { data, loading, error, reload } = useAsync(() => assessments.list({ moduleId: module.id }), [module.id]);
  const [f, setF] = useState({ title: '', type: 'QUIZ', dueDate: '', passingScore: 75 });
  const [open, setOpen] = useState(null);
  const [msg, setMsg] = useState('');

  const set = (k) => (e) => setF({ ...f, [k]: e.target.value });

  const create = async (e) => {
    e.preventDefault();
    setMsg('');
    if (!f.title.trim()) return setMsg('Give the quiz a title.');
    try {
      await assessments.create({
        moduleId: module.id,
        title: f.title.trim(),
        type: f.type,
        dueDate: f.dueDate || null,
        passingScore: Number(f.passingScore) || 75,
      });
      setF({ ...f, title: '', dueDate: '' });
      reload();
    } catch (ex) {
      setMsg(ex.message);
    }
  };

  const act = async (fn) => {
    setMsg('');
    try {
      await fn();
      reload();
    } catch (ex) {
      setMsg(ex.message);
    }
  };

  return (
    <Card title={`Quizzes and assignments: ${module.title}`}>
      {msg && <p className="err">{msg}</p>}
      {loading && <p>Loading...</p>}
      {error && <p className="err">{error}</p>}
      {data && (
        <DataTable
          cols={['Title', 'Type', 'Due', 'Status', 'Actions']}
          rows={data.map((a) => [
            a.title,
            statusLabel(a.type),
            formatDate(a.dueDate),
            <Badge t={statusLabel(a.status)} />,
            <div className="row">
              <Button v="ghost" onClick={() => setOpen(a)}>
                Questions
              </Button>
              <Button v="ghost" onClick={() => act(() => assessments.setPublished(a.id, a.status !== 'ACTIVE'))}>
                {a.status === 'ACTIVE' ? 'Unpublish' : 'Publish'}
              </Button>
              <Button v="danger" onClick={() => window.confirm('Delete this quiz?') && act(() => assessments.remove(a.id))}>
                Delete
              </Button>
            </div>,
          ])}
        />
      )}
      {data && data.length === 0 && <p className="muted">No quizzes in this module yet.</p>}

      <form onSubmit={create} className="row top-gap">
        <input placeholder="New quiz title" value={f.title} onChange={set('title')} />
        <select value={f.type} onChange={set('type')}>
          <option value="QUIZ">Quiz (auto-graded)</option>
          <option value="ASSIGNMENT">Assignment (instructor reviews)</option>
        </select>
        <input type="date" value={f.dueDate} onChange={set('dueDate')} title="Due date" />
        <Button>Add</Button>
      </form>

      {open && <QuestionManager assessment={open} onClose={() => { setOpen(null); reload(); }} />}
    </Card>
  );
};
