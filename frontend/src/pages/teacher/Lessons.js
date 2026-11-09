import React, { useState } from 'react';
import { lessons0, modules } from '../../data';
import {
  Button,
  Card,
  FormInput,
  LessonCard,
  Modal,
  PageHeader,
} from '../../components';

export function Lessons() {
  const [ls, setLs] = useState(lessons0);
  const [mod, setMod] = useState(1);
  const [sel, setSel] = useState(1);

  const cur = ls.find((l) => l.id === sel);
  const inMod = ls.filter((l) => l.module === mod);

  const [prev, setPrev] = useState(false);

  const upd = (k, v) =>
    setLs(ls.map((l) => (l.id === sel ? { ...l, [k]: v } : l)));

  const create = () => {
    const id = Math.max(0, ...ls.map((l) => l.id)) + 1;

    setLs([
      ...ls,
      {
        id,
        module: mod,
        title: 'New Lesson',
        desc: '',
        instructions: '',
        questions: '',
        status: 'Draft',
      },
    ]);

    setSel(id);
  };

  const del = () => {
    setLs(ls.filter((l) => l.id !== sel));
    setSel(null);
  };

  return (
    <>
      <PageHeader t="Grade 3: Addition & Subtraction">
        <Button onClick={create}>+ Create Lesson</Button>
      </PageHeader>

      <div className="grid3 lay">
        <Card title="Modules">
          {modules.map((m) => (
            <div
              key={m.id}
              className={`lc ${m.id === mod ? 'on' : ''}`}
              onClick={() => {
                setMod(m.id);
                setSel(null);
              }}
            >
              <b>
                Module {m.id}: {m.title}
              </b>
            </div>
          ))}
        </Card>

        <Card title="Lessons & Editor">
          {inMod.map((l) => (
            <LessonCard
              key={l.id}
              l={l}
              active={l.id === sel}
              onClick={() => setSel(l.id)}
            />
          ))}

          {!inMod.length && <p>No lessons yet.</p>}

          {cur && cur.module === mod && (
            <>
              <FormInput
                label="Title"
                value={cur.title}
                onChange={(e) => upd('title', e.target.value)}
              />

              <FormInput
                label="Description"
                value={cur.desc}
                onChange={(e) => upd('desc', e.target.value)}
              />

              <FormInput label="Instructions">
                <textarea
                  value={cur.instructions}
                  onChange={(e) => upd('instructions', e.target.value)}
                />
              </FormInput>

              <FormInput label="Practice questions">
                <textarea
                  value={cur.questions}
                  onChange={(e) => upd('questions', e.target.value)}
                />
              </FormInput>

              <div className="row">
                <Button v="ghost" onClick={() => setPrev(true)}>
                  Preview
                </Button>

                <Button onClick={() => upd('status', 'Published')}>
                  Publish
                </Button>

                <Button v="danger" onClick={del}>
                  Delete
                </Button>
              </div>
            </>
          )}
        </Card>

        <Card title="Preview">
          {cur && cur.module === mod ? (
            <>
              <h3>{cur.title}</h3>
              <small>{modules[mod - 1].skill}</small>
              <p>{cur.desc}</p>
              <p>
                <b>How to:</b> {cur.instructions}
              </p>
              <pre>{cur.questions}</pre>
            </>
          ) : (
            <p>Select a lesson.</p>
          )}
        </Card>
      </div>

      {prev && cur && (
        <Modal
          title={'Preview: ' + cur.title}
          onClose={() => setPrev(false)}
        >
          <p>{cur.instructions}</p>
          <pre>{cur.questions}</pre>
        </Modal>
      )}
    </>
  );
}
