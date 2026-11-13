import React, { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Badge, Button, Card, DataTable, PageHeader, QuizForm } from '../../components';
import { useAsync } from '../../hooks/useAsync';
import { getQuiz, submitQuiz } from '../../services/learnerService';
import { queueEvent } from '../../services/offlineService';
import { formatDate, statusLabel } from '../../utils/format';

export function LearnerQuiz() {
  const id = Number(useParams().assessmentId);
  const nav = useNavigate();
  const { data, loading, error } = useAsync(() => getQuiz(id), [id]);
  const [result, setResult] = useState(null);
  const [busy, setBusy] = useState(false);
  const [msg, setMsg] = useState('');

  const submit = async (answers) => {
    setBusy(true);
    setMsg('');
    try {
      if (!navigator.onLine) {
        queueEvent({ type: 'QUIZ_SUBMIT', assessmentId: id, answers, label: data.title });
        setMsg('You are offline. Your answers are saved on this device and will be graded when you reconnect.');
      } else {
        setResult(await submitQuiz(id, answers));
      }
    } catch (ex) {
      setMsg(ex.message);
    } finally {
      setBusy(false);
    }
  };

  if (loading) return <p>Loading quiz...</p>;
  if (error) return <p className="err">{error}</p>;

  return (
    <>
      <PageHeader t={data.title}>
        <Button v="ghost" onClick={() => nav(-1)}>
          Back
        </Button>
      </PageHeader>

      {!result && (
        <Card>
          <p className="muted">
            {statusLabel(data.type)} - due {formatDate(data.dueDate)} - passing score {data.passingScore}%
          </p>
          {msg && <p className="err">{msg}</p>}
          <QuizForm questions={data.questions} onSubmit={submit} busy={busy} />
        </Card>
      )}

      {result && (
        <Card title="Your result">
          <div className="big">{Math.round(result.score)}%</div>
          <p>
            {result.correctCount} of {result.totalQuestions} correct <Badge t={result.passed ? 'Passed' : 'Not passed'} />
          </p>
          {result.status === 'PENDING_REVIEW' && (
            <p className="ok">Your instructor will review this assignment. The score may change.</p>
          )}
          <DataTable
            cols={['Question', 'Your answer', 'Correct answer', 'Result']}
            rows={result.answers.map((a) => [
              a.questionText,
              a.studentAnswer || '(no answer)',
              a.correctAnswer,
              <Badge t={a.correct ? 'Correct' : 'Incorrect'} />,
            ])}
          />
          <div className="row">
            <Button v="ghost" onClick={() => setResult(null)}>
              Try again
            </Button>
            <Button onClick={() => nav(-1)}>Back to course</Button>
          </div>
        </Card>
      )}
    </>
  );
}
