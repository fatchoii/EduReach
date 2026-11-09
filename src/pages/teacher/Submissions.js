import React, { useState } from 'react';
import { submissions } from '../../data';
import {
  Badge,
  DataTable,
  Modal,
  PageHeader,
} from '../../components';

export function Submissions() {
  const [o, setO] = useState(null);
  const s = submissions[o];

  return (
    <>
      <PageHeader t="Submissions & Grading" />

      <DataTable
        cols={['Student', 'Assignment', 'Score', 'Date Submitted', 'Status']}
        onRow={setO}
        rows={submissions.map((s) => [
          s.student,
          s.assignment,
          s.score,
          s.date,
          <Badge t={s.status} />,
        ])}
      />

      {s && (
        <Modal
          title={`${s.student} — ${s.assignment}`}
          onClose={() => setO(null)}
        >
          <DataTable
            cols={[
              'Question',
              'Student Answer',
              'Correct Answer',
              'Result',
              'Skill',
            ]}
            rows={s.items.map(([q, a, c, sk]) => [
              q,
              a,
              c,
              a === c ? <Badge t="Correct" /> : <Badge t="Incorrect" />,
              sk,
            ])}
          />
        </Modal>
      )}
    </>
  );
}
