import React, { useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Card, PageHeader, QuizForm } from '../../components';
import { getDownload, queueEvent, syncNow } from '../../services/offlineService';
import { formatDate, statusLabel } from '../../utils/format';

/** A quiz from the downloaded copy. Answers are saved on the device and graded by the server when synced. */
export function OfflineQuiz() {
  const params = useParams();
  const courseId = Number(params.courseId);
  const assessmentId = Number(params.assessmentId);
  const nav = useNavigate();
  const pkg = getDownload(courseId);
  const quiz = pkg ? pkg.modules.flatMap((m) => m.assessments).find((a) => a.id === assessmentId) : null;
  const [outcome, setOutcome] = useState(null);
  const [busy, setBusy] = useState(false);

  if (!quiz) {
    return (
      <>
        <p className="err">This quiz is not in the downloaded copy.</p>
        <Link to="/learner/offline">Offline downloads</Link>
      </>
    );
  }

  const submit = async (answers) => {
    setBusy(true);
    const event = queueEvent({ type: 'QUIZ_SUBMIT', assessmentId, answers, label: quiz.title });
    if (navigator.onLine) {
      try {
        const response = await syncNow();
        const result = response && response.results.find((r) => r.clientId === event.clientId);
        if (result && result.status === 'SYNCED') {
          setOutcome({ text: `Submitted and graded: ${Math.round(result.score)}%.`, ok: true });
        } else {
          setOutcome({ text: 'Saved on this device. It will sync automatically.', ok: true });
        }
      } catch {
        setOutcome({ text: 'Saved on this device. It will sync automatically.', ok: true });
      }
    } else {
      setOutcome({
        text: 'You are offline. Your answers are saved on this device. The score will appear after you reconnect and sync.',
        ok: true,
      });
    }
    setBusy(false);
  };

  return (
    <>
      <PageHeader t={`${quiz.title} (offline)`}>
        <Button v="ghost" onClick={() => nav(-1)}>
          Back
        </Button>
      </PageHeader>
      {!outcome && (
        <Card>
          <p className="muted">
            {statusLabel(quiz.type)} - due {formatDate(quiz.dueDate)}
          </p>
          <QuizForm questions={quiz.questions} onSubmit={submit} busy={busy} />
        </Card>
      )}
      {outcome && (
        <Card title="Saved">
          <p className={outcome.ok ? 'ok' : 'err'}>{outcome.text}</p>
          <Link className="btn primary" to={`/learner/offline/${courseId}`}>
            Back to the course
          </Link>
        </Card>
      )}
    </>
  );
}
