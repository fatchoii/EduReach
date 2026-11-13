import React, { useEffect, useState } from 'react';
import {
  Badge,
  Button,
  Card,
  Chart,
  DataTable,
  FormInput,
  Modal,
  PageHeader,
  ProgressBar,
} from '../../components';
import { useAsync } from '../../hooks/useAsync';
import { getCurrentUser } from '../../services/authService';
import { courses } from '../../services/contentService';
import { getAnswers, gradeSubmission, loadGradebook } from '../../services/gradingService';
import { formatDateTime, statusLabel } from '../../utils/format';

/** Review one submission: correct individual answers, add feedback, set the grade. */
function ReviewModal({ submission, onClose, onSaved }) {
  const { data, loading, error } = useAsync(() => getAnswers(submission.id), [submission.id]);
  const [marks, setMarks] = useState({});
  const [score, setScore] = useState('');
  const [feedback, setFeedback] = useState(submission.feedback || '');
  const [msg, setMsg] = useState('');
  const [busy, setBusy] = useState(false);

  const isCorrect = (a) => (a.id in marks ? marks[a.id] : a.correct);

  const save = async () => {
    setBusy(true);
    setMsg('');
    try {
      const overrides = (data || [])
        .filter((a) => a.id in marks && marks[a.id] !== a.correct)
        .map((a) => ({ answerId: a.id, correct: marks[a.id] }));
      await gradeSubmission(submission.id, {
        overrides,
        score: score === '' ? null : Number(score),
        feedback,
      });
      onSaved();
    } catch (ex) {
      setMsg(ex.message);
      setBusy(false);
    }
  };

  return (
    <Modal title={`${submission.userName}: ${submission.assessmentTitle}`} onClose={onClose}>
      <p>
        Current score: <b>{Math.round(submission.score || 0)}%</b> <Badge t={statusLabel(submission.status)} />
      </p>
      {loading && <p>Loading answers...</p>}
      {error && <p className="err">{error}</p>}
      {data && (
        <DataTable
          cols={['Question', 'Learner answer', 'Correct answer', 'Correct?']}
          rows={data.map((a) => [
            a.questionText,
            a.studentAnswer || '(no answer)',
            a.correctAnswer,
            <label>
              <input
                type="checkbox"
                checked={!!isCorrect(a)}
                onChange={(e) => setMarks({ ...marks, [a.id]: e.target.checked })}
              />{' '}
              {isCorrect(a) ? 'Correct' : 'Incorrect'}
            </label>,
          ])}
        />
      )}
      <p className="muted">The score is recalculated from your corrections. You can also type a final score.</p>
      <FormInput
        label="Final score override (optional, 0 to 100)"
        type="number"
        min="0"
        max="100"
        value={score}
        onChange={(e) => setScore(e.target.value)}
      />
      <FormInput label="Feedback for the learner">
        <textarea value={feedback} onChange={(e) => setFeedback(e.target.value)} />
      </FormInput>
      {msg && <p className="err">{msg}</p>}
      <Button disabled={busy} onClick={save}>
        {busy ? 'Saving...' : 'Save grade'}
      </Button>
    </Modal>
  );
}

export function Gradebook() {
  const user = getCurrentUser();
  const isAdmin = user.role === 'Admin';
  const courseList = useAsync(() => courses.list(isAdmin ? {} : { instructorId: user.id }), [user.id]);
  const [courseId, setCourseId] = useState(null);
  const [review, setReview] = useState(null);

  useEffect(() => {
    if (courseId === null && courseList.data && courseList.data.length) setCourseId(courseList.data[0].id);
  }, [courseList.data, courseId]);

  const book = useAsync(
    () => (courseId ? loadGradebook(courseId) : Promise.resolve(null)),
    [courseId],
    { refreshMs: 15000 }
  );

  const data = book.data;
  const reviewButton = (s) => (
    <Button v={s.status === 'PENDING_REVIEW' ? 'primary' : 'ghost'} onClick={() => setReview(s)}>
      {s.status === 'PENDING_REVIEW' ? 'Review' : 'Open'}
    </Button>
  );

  return (
    <>
      <PageHeader t="Gradebook">
        {courseList.data && courseList.data.length > 0 && (
          <select value={courseId || ''} onChange={(e) => setCourseId(Number(e.target.value))}>
            {courseList.data.map((c) => (
              <option key={c.id} value={c.id}>
                {c.title}
              </option>
            ))}
          </select>
        )}
      </PageHeader>

      {courseList.error && <p className="err">{courseList.error}</p>}
      {courseList.data && courseList.data.length === 0 && <p className="muted">Create a course first.</p>}
      {book.loading && courseId && <p>Loading gradebook...</p>}
      {book.error && <p className="err">{book.error}</p>}

      {data && (
        <>
          <div className="grid2">
            <Card title="Gradebook: learner scores">
              <DataTable
                cols={['Learner', 'Progress', 'Average score', 'Submissions']}
                rows={data.learners.map((l) => [
                  l.name,
                  <ProgressBar value={Math.round(l.progress)} />,
                  `${l.average}%`,
                  l.submissions,
                ])}
              />
              {data.learners.length === 0 && <p className="muted">No learners are enrolled yet.</p>}
            </Card>

            <Card title="Class progress">
              {data.learners.length ? (
                <Chart data={data.learners.map((l) => [l.name.split(' ')[0], Math.round(l.progress)])} />
              ) : (
                <p className="muted">Nothing to show yet.</p>
              )}
            </Card>
          </div>

          <Card title={`Submission queue: ${data.pending.length} pending review${data.pending.length === 1 ? '' : 's'}`}>
            {data.pending.length === 0 && <p className="muted">Nothing waiting for review.</p>}
            {data.pending.length > 0 && (
              <DataTable
                cols={['Learner', 'Assignment', 'Submitted', 'Auto score', '']}
                rows={data.pending.map((s) => [
                  s.userName,
                  s.assessmentTitle,
                  formatDateTime(s.submittedAt),
                  `${Math.round(s.score || 0)}%`,
                  reviewButton(s),
                ])}
              />
            )}
          </Card>

          <Card title="All submissions">
            <DataTable
              cols={['Learner', 'Quiz', 'Score', 'Status', 'Date', '']}
              rows={data.submissions.map((s) => [
                s.userName,
                s.assessmentTitle,
                `${Math.round(s.score || 0)}%`,
                <Badge t={statusLabel(s.status)} />,
                formatDateTime(s.submittedAt),
                reviewButton(s),
              ])}
            />
            {data.submissions.length === 0 && <p className="muted">No submissions yet.</p>}
          </Card>
        </>
      )}

      {review && (
        <ReviewModal
          submission={review}
          onClose={() => setReview(null)}
          onSaved={() => {
            setReview(null);
            book.reload();
          }}
        />
      )}
    </>
  );
}
