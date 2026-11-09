import React, { useState } from 'react';
import {
  SKILLS,
  assignments0,
  classes,
  questions0,
} from '../../data';
import {
  AssignmentCard,
  Badge,
  Button,
  Card,
  DataTable,
  FormInput,
  Modal,
  PageHeader,
} from '../../components';

export function Assignments() {
  const [as, setAs] = useState(assignments0);
  const [f, setF] = useState(null);

  const k = (x) => (e) =>
    setF({ ...f, [x]: e.target.value });

  const save = (status) => {
    const c = classes.find((c) => c.name === f.cls);

    setAs([
      ...as,
      {
        id: Date.now(),
        name: f.name || 'Untitled',
        skill: f.skill,
        cls: f.cls,
        assigned: c.students.length,
        done: 0,
        due: f.due || '2026-10-15',
        status,
      },
    ]);

    setF(null);
  };

  return (
    <>
      <PageHeader t="Assignments">
        <Button
          onClick={() =>
            setF({
              name: '',
              skill: SKILLS[0],
              cls: classes[0].name,
              due: '',
              qs: [],
            })
          }
        >
          + Create Assignment
        </Button>
      </PageHeader>

      <div className="grid3">
        {as.map((a) => (
          <AssignmentCard key={a.id} a={a} />
        ))}
      </div>

      <Card title="All assignments">
        <DataTable
          cols={[
            'Name',
            'Skill',
            'Class',
            'Assigned',
            'Completion',
            'Due',
            'Status',
          ]}
          rows={as.map((a) => [
            a.name,
            a.skill,
            a.cls,
            a.assigned,
            a.done + '%',
            a.due,
            <Badge t={a.status} />,
          ])}
        />
      </Card>

      {f && (
        <Modal
          title="Create Assignment"
          onClose={() => setF(null)}
        >
          <FormInput
            label="Name"
            value={f.name}
            onChange={k('name')}
          />

          <FormInput label="Class">
            <select value={f.cls} onChange={k('cls')}>
              {classes.map((c) => (
                <option key={c.id}>{c.name}</option>
              ))}
            </select>
          </FormInput>

          <FormInput label="Skill">
            <select value={f.skill} onChange={k('skill')}>
              {SKILLS.map((x) => (
                <option key={x}>{x}</option>
              ))}
            </select>
          </FormInput>

          <FormInput label="Students (all in class by default)">
            <div className="chk">
              {classes
                .find((c) => c.name === f.cls)
                .students.map((s) => (
                  <label key={s.id}>
                    <input type="checkbox" defaultChecked /> {s.name}
                  </label>
                ))}
            </div>
          </FormInput>

          <FormInput label="Questions">
            <div className="chk">
              {questions0
                .filter((q) => q.skill === f.skill)
                .map((q) => (
                  <label key={q.id}>
                    <input type="checkbox" defaultChecked /> {q.q}
                  </label>
                ))}
            </div>
          </FormInput>

          <FormInput
            label="Due date"
            type="date"
            value={f.due}
            onChange={k('due')}
          />

          <div className="row">
            <Button v="ghost" onClick={() => save('Draft')}>
              Save Draft
            </Button>

            <Button onClick={() => save('Active')}>
              Publish
            </Button>
          </div>
        </Modal>
      )}
    </>
  );
}
