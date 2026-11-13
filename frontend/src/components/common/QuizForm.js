import React, { useState } from 'react';
import { Button } from './Button';

/**
 * Renders questions (multiple choice or fill in) and returns the answers as
 * [{ questionId, answer }] when the learner submits.
 */
export const QuizForm = ({ questions, onSubmit, busy, submitLabel = 'Submit answers' }) => {
  const [answers, setAnswers] = useState({});

  const set = (id, value) => setAnswers({ ...answers, [id]: value });

  const submit = (e) => {
    e.preventDefault();
    onSubmit(questions.map((q) => ({ questionId: q.id, answer: answers[q.id] || '' })));
  };

  const answered = questions.filter((q) => (answers[q.id] || '').trim() !== '').length;

  return (
    <form onSubmit={submit}>
      {questions.map((q, i) => (
        <div key={q.id} className="quiz-q">
          <b>
            {i + 1}. {q.questionText}
          </b>
          {q.options && q.options.length > 0 ? (
            <div className="opts">
              {q.options.map((option) => (
                <label key={option} className={`opt ${answers[q.id] === option ? 'on' : ''}`}>
                  <input
                    type="radio"
                    name={`q${q.id}`}
                    checked={answers[q.id] === option}
                    onChange={() => set(q.id, option)}
                  />
                  {option}
                </label>
              ))}
            </div>
          ) : (
            <input
              placeholder="Type your answer"
              value={answers[q.id] || ''}
              onChange={(e) => set(q.id, e.target.value)}
            />
          )}
        </div>
      ))}
      {questions.length === 0 && <p className="muted">This quiz has no questions yet.</p>}
      <div className="row">
        <Button disabled={busy || questions.length === 0}>{busy ? 'Submitting...' : submitLabel}</Button>
        <small className="muted">
          {answered} of {questions.length} answered
        </small>
      </div>
    </form>
  );
};
