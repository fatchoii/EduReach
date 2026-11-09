import React, { useState } from 'react';
import { SKILLS, questions0 } from '../../data';
import {
  Badge,
  Button,
  DataTable,
  FormInput,
  Modal,
  PageHeader,
} from '../../components';

export function QuestionBank() {
  const [qs, setQs] = useState(questions0);
  const [s, setS] = useState('');
  const [sk, setSk] = useState('');
  const [d, setD] = useState('');
  const [ed, setEd] = useState(null);

  const rows = qs.filter(
    (q) =>
      q.q.includes(s) &&
      (!sk || q.skill === sk) &&
      (!d || q.diff === d)
  );

  const save = (e) => {
    e.preventDefault();

    ed.id
      ? setQs(qs.map((q) => (q.id === ed.id ? ed : q)))
      : setQs([...qs, { ...ed, id: Date.now() }]);

    setEd(null);
  };

  const k = (x) => (e) =>
    setEd({ ...ed, [x]: e.target.value });

  return (
    <>
      <PageHeader t="Question Bank">
        <Button
          onClick={() =>
            setEd({
              q: '',
              a: '',
              skill: SKILLS[0],
              diff: 'Easy',
            })
          }
        >
          + Create Question
        </Button>
      </PageHeader>

      <div className="row">
        <input
          placeholder="Search questions"
          value={s}
          onChange={(e) => setS(e.target.value)}
        />

        <select value={sk} onChange={(e) => setSk(e.target.value)}>
          <option value="">All skills</option>
          {SKILLS.map((x) => (
            <option key={x}>{x}</option>
          ))}
        </select>

        <select value={d} onChange={(e) => setD(e.target.value)}>
          <option value="">All difficulty</option>
          {['Easy', 'Medium', 'Hard'].map((x) => (
            <option key={x}>{x}</option>
          ))}
        </select>
      </div>

      <DataTable
        cols={['Question', 'Skill', 'Difficulty', 'Correct Answer', 'Actions']}
        rows={rows.map((q) => [
          q.q,
          q.skill,
          <Badge t={q.diff} />,
          q.a,
          <div className="row">
            <Button v="ghost" onClick={() => setEd(q)}>
              Edit
            </Button>

            <Button
              v="danger"
              onClick={() => setQs(qs.filter((x) => x.id !== q.id))}
            >
              Delete
            </Button>
          </div>,
        ])}
      />

      {ed && (
        <Modal
          title={ed.id ? 'Edit Question' : 'New Question'}
          onClose={() => setEd(null)}
        >
          <form onSubmit={save}>
            <FormInput
              label="Question"
              required
              value={ed.q}
              onChange={k('q')}
            />

            <FormInput
              label="Correct answer"
              required
              value={ed.a}
              onChange={k('a')}
            />

            <FormInput label="Skill">
              <select value={ed.skill} onChange={k('skill')}>
                {SKILLS.map((x) => (
                  <option key={x}>{x}</option>
                ))}
              </select>
            </FormInput>

            <FormInput label="Difficulty">
              <select value={ed.diff} onChange={k('diff')}>
                {['Easy', 'Medium', 'Hard'].map((x) => (
                  <option key={x}>{x}</option>
                ))}
              </select>
            </FormInput>

            <Button>Save</Button>
          </form>
        </Modal>
      )}
    </>
  );
}
