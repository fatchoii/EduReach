import React, { useState } from 'react';
import { classAvg, classes } from '../../data';
import {
  Button,
  Card,
  DataTable,
  PageHeader,
  ProgressBar,
} from '../../components';

export function Classes() {
  const [open, setOpen] = useState(null);
  const c = classes.find((x) => x.id === open);

  return (
    <>
      <PageHeader t={c ? c.name : 'Classes'}>
        {c && (
          <Button v="ghost" onClick={() => setOpen(null)}>
            ← All classes
          </Button>
        )}
      </PageHeader>

      {!c ? (
        <div className="grid3">
          {classes.map((c) => (
            <Card key={c.id} title={c.name}>
              <p>
                Grade {c.grade} · {c.students.length} students
              </p>

              <ProgressBar
                label="Average mastery"
                value={classAvg(c)}
              />

              <Button onClick={() => setOpen(c.id)}>
                Open class
              </Button>
            </Card>
          ))}
        </div>
      ) : (
        <DataTable
          cols={['Student', ...SKILLS, 'Overall Mastery']}
          rows={c.students.map((s) => [
            s.name,
            ...s.m.map((v) => <ProgressBar value={v} />),
            <b>{s.overall}%</b>,
          ])}
        />
      )}
    </>
  );
}
