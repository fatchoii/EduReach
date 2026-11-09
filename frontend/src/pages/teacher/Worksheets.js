import React, { useState } from 'react';
import { SKILLS, gen } from '../../data';
import {
  Button,
  Card,
  FormInput,
  PageHeader,
} from '../../components';

export function Worksheets() {
  const [c, setC] = useState({
    skill: SKILLS[0],
    diff: 'Easy',
    n: 10,
    key: true,
  });

  const [w, setW] = useState(null);

  return (
    <>
      <PageHeader t="Worksheet Generator" />

      <Card title="Options">
        <div className="row">
          <FormInput label="Skill">
            <select
              value={c.skill}
              onChange={(e) => setC({ ...c, skill: e.target.value })}
            >
              {SKILLS.map((x) => (
                <option key={x}>{x}</option>
              ))}
            </select>
          </FormInput>

          <FormInput label="Difficulty">
            <select
              value={c.diff}
              onChange={(e) => setC({ ...c, diff: e.target.value })}
            >
              {['Easy', 'Medium', 'Hard'].map((x) => (
                <option key={x}>{x}</option>
              ))}
            </select>
          </FormInput>

          <FormInput
            label="Questions"
            type="number"
            min="1"
            max="30"
            value={c.n}
            onChange={(e) => setC({ ...c, n: +e.target.value })}
          />

          <label>
            <input
              type="checkbox"
              checked={c.key}
              onChange={(e) => setC({ ...c, key: e.target.checked })}
            />{' '}
            Include answer key
          </label>
        </div>

        <div className="row">
          <Button
            onClick={() =>
              setW({
                qs: gen(c.skill, c.diff, c.n),
                key: c.key,
                skill: c.skill,
              })
            }
          >
            Generate Worksheet
          </Button>

          <Button
            v="ghost"
            disabled={!w}
            onClick={() =>
              window.scrollTo(0, document.body.scrollHeight)
            }
          >
            Preview
          </Button>

          <Button
            v="ghost"
            disabled={!w}
            onClick={() => window.print()}
          >
            Print
          </Button>
        </div>
      </Card>

      {w ? (
        <div className="sheet">
          <h1>EduReach</h1>
          <h3>Grade 3 Addition & Subtraction — {w.skill}</h3>

          <p>
            Student Name: ______________________ Date: ______________
          </p>

          <ol>
            {w.qs.map((q, i) => (
              <li key={i}>{q.q} ________</li>
            ))}
          </ol>

          {w.key && (
            <div className="akey">
              <h4>Answer Key</h4>
              <p>
                {w.qs.map((q, i) => `${i + 1}. ${q.a}`).join('   ')}
              </p>
            </div>
          )}
        </div>
      ) : (
        <p>Choose options and generate a worksheet.</p>
      )}
    </>
  );
}
