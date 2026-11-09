
import React, { useState, useMemo } from 'react';
import { Link, useNavigate } from 'react-router-dom';

import {
  SKILLS,
  students,
  classes,
  classAvg,
  modules,
  lessons0,
  questions0,
  assignments0,
  submissions,
  notifs,
  activity,
  gen,
} from './data';

import {
  Button,
  FormInput,
  Card,
  DashboardCard,
  ProgressBar,
  SkillCard,
  Chart,
  DataTable,
  Modal,
  Badge,
  NotificationItem,
  AssignmentCard,
  LessonCard,
} from './components';

// ==========================================
// HELPER FUNCTIONS
// ==========================================

const avg = (i) =>
  Math.round(
    students.reduce((a, s) => a + s.m[i], 0) / students.length
  );

const skillAvg = SKILLS.map((s, i) => [s, avg(i)]);

const Head = ({ t, children }) => (
  <div className="ph">
    <h2>{t}</h2>
    {children}
  </div>
);

// ==========================================
// LANDING PAGE
// ==========================================

export function Landing() {
  return (
    <div className="land">
      <header className="top">
        <span className="logo">➕➖ EduReach</span>
        <span className="sp" />

        {[['#top', 'Home'], ['#features', 'Features'], ['#about', 'About']].map(
          ([h, l]) => (
            <a key={l} href={h}>
              {l}
            </a>
          )
        )}

        <Link to="/login">Login</Link>
        <Link to="/register" className="btn primary">
          Register
        </Link>
      </header>

      <section className="hero" id="top">
        <div>
          <h1>
            Helping Grade 3 Students Master Addition and Subtraction
          </h1>

          <p>
            EduReach helps teachers provide focused addition and subtraction
            practice while giving parents a simple way to monitor their
            child's progress.
          </p>

          <Link to="/register" className="btn primary">
            Get Started
          </Link>

          <Link to="/login" className="btn ghost">
            Login
          </Link>
        </div>

        <div className="art">
          {['23+15', '72−38', '+', '−', '405−167', '36+28'].map((x, i) => (
            <span
              key={i}
              style={{ animationDelay: i * 0.3 + 's' }}
            >
              {x}
            </span>
          ))}
        </div>
      </section>

      <section id="features" className="feat">
        {[
          ['📚', 'Lessons & Question Bank', 'Build modules for every skill.'],
          ['📝', 'Assignments & Grading', 'Review answers item by item.'],
          ['📊', 'Skill Analytics', 'See exactly which skill needs reteaching.'],
          ['👪', 'Parent View', 'Simple progress and suggested practice.'],
        ].map(([i, t, d]) => (
          <Card key={t}>
            <h3>
              {i} {t}
            </h3>
            <p>{d}</p>
          </Card>
        ))}
      </section>

      <section id="about" className="feat">
        <Card title="About">
          <p>
            EduReach focuses only on Grade 3 addition, regrouping, subtraction,
            borrowing, and borrowing across zero.
          </p>
        </Card>
      </section>
    </div>
  );
}

// ==========================================
// AUTHENTICATION
// ==========================================

export function Auth({ mode, onAuth }) {
  const nav = useNavigate();

  const [f, setF] = useState({
    name: '',
    email: '',
    password: '',
    confirm: '',
    role: 'Teacher',
  });

  const [err, setErr] = useState('');

  const set = (k) => (e) =>
    setF({ ...f, [k]: e.target.value });

  const go = (e) => {
    e.preventDefault();

    if (!f.email || !f.password) {
      return setErr('Email and password are required.');
    }

    if (mode === 'register' && f.password !== f.confirm) {
      return setErr('Passwords do not match.');
    }

    const role =
      mode === 'register'
        ? f.role
        : f.email.includes('parent')
          ? 'Parent'
          : 'Teacher';

    onAuth({
      name: f.name || (role === 'Parent' ? 'Maria Reyes' : 'Ms. Santos'),
      email: f.email,
      role,
    });

    nav(role === 'Parent' ? '/parent/dashboard' : '/teacher/dashboard');
  };

  return (
    <div className="auth">
      <form className="card" onSubmit={go}>
        <h2>
          {mode === 'login' ? 'Welcome back' : 'Create account'}
        </h2>

        {err && <p className="err">{err}</p>}

        {mode === 'register' && (
          <FormInput
            label="Full Name"
            value={f.name}
            onChange={set('name')}
          />
        )}

        <FormInput
          label="Email"
          type="email"
          value={f.email}
          onChange={set('email')}
        />

        <FormInput
          label="Password"
          type="password"
          value={f.password}
          onChange={set('password')}
        />

        {mode === 'register' && (
          <>
            <FormInput
              label="Confirm Password"
              type="password"
              value={f.confirm}
              onChange={set('confirm')}
            />

            <FormInput label="Role">
              <select value={f.role} onChange={set('role')}>
                <option>Teacher</option>
                <option>Parent</option>
              </select>
            </FormInput>
          </>
        )}

        <Button>
          {mode === 'login' ? 'Login' : 'Register'}
        </Button>

        <small>
          {mode === 'login' ? (
            <>
              No account? <Link to="/register">Register</Link>. Tip: use an
              email containing "parent" for the parent view.
            </>
          ) : (
            <>
              Have an account? <Link to="/login">Login</Link>
            </>
          )}
        </small>
      </form>
    </div>
  );
}

// ==========================================
// TEACHER DASHBOARD
// ==========================================

export function TDash() {
  const low = students.filter((s) => s.overall < 70);

  return (
    <>
      <Head t="Dashboard" />

      <div className="grid4">
        <DashboardCard
          icon="🎓"
          label="Total Students"
          value={students.length}
        />

        <DashboardCard
          icon="📝"
          label="Active Assignments"
          value={assignments0.filter((a) => a.status === 'Active').length}
        />

        <DashboardCard
          icon="⏳"
          label="Pending Submissions"
          value={submissions.filter((s) => s.status !== 'Graded').length}
        />

        <DashboardCard
          icon="⭐"
          label="Average Skill Mastery"
          value={Math.round(
            skillAvg.reduce((a, b) => a + b[1], 0) / 5
          ) + '%'}
        />
      </div>

      <div className="grid2">
        <Card title="Skill Mastery">
          {skillAvg.map(([s, v]) => (
            <ProgressBar key={s} label={s} value={v} />
          ))}
        </Card>

        <Card title="Needs Attention">
          <ul>
            {low.map((s) => (
              <li key={s.id}>
                <b>{s.name}</b>: weakest in{' '}
                {SKILLS[s.m.indexOf(Math.min(...s.m))]} ({Math.min(...s.m)}%)
              </li>
            ))}
          </ul>
        </Card>
      </div>

      <Card title="Recent Activity">
        <ul>
          {activity.map((a) => (
            <li key={a}>{a}</li>
          ))}
        </ul>
      </Card>
    </>
  );
}

// ==========================================
// CLASSES
// ==========================================

export function Classes() {
  const [open, setOpen] = useState(null);
  const c = classes.find((x) => x.id === open);

  return (
    <>
      <Head t={c ? c.name : 'Classes'}>
        {c && (
          <Button v="ghost" onClick={() => setOpen(null)}>
            ← All classes
          </Button>
        )}
      </Head>

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

// ==========================================
// LESSON MANAGEMENT
// ==========================================

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
      <Head t="Grade 3: Addition & Subtraction">
        <Button onClick={create}>+ Create Lesson</Button>
      </Head>

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

// ==========================================
// QUESTION BANK
// ==========================================

export function QBank() {
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
      <Head t="Question Bank">
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
      </Head>

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

// ==========================================
// ASSIGNMENTS
// ==========================================

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
      <Head t="Assignments">
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
      </Head>

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

// ==========================================
// SUBMISSIONS & GRADING
// ==========================================

export function Submissions() {
  const [o, setO] = useState(null);
  const s = submissions[o];

  return (
    <>
      <Head t="Submissions & Grading" />

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

// ==========================================
// ANALYTICS
// ==========================================

export function Analytics() {
  const weak = [...skillAvg].sort((a, b) => a[1] - b[1]).slice(0, 2);

  return (
    <>
      <Head t="Skill Analytics" />

      <div className="grid3">
        <DashboardCard
          icon="🏫"
          label="Overall Class Mastery"
          value={
            Math.round(
              students.reduce((a, s) => a + s.overall, 0) / students.length
            ) + '%'
          }
        />

        <DashboardCard
          icon="📈"
          label="Strongest Skill"
          value={[...skillAvg].sort((a, b) => b[1] - a[1])[0][0]}
        />

        <DashboardCard
          icon="⚠️"
          label="Weakest Skill"
          value={weak[0][0]}
        />
      </div>

      <div className="grid2">
        <Card title="Skill Mastery">
          <Chart
            data={skillAvg.map(([s, v]) => [
              s.split(' ').slice(-1)[0],
              v,
            ])}
          />
        </Card>

        <Card title="Skills Needing Attention">
          {weak.map(([s, v]) => (
            <SkillCard key={s} skill={s} value={v} />
          ))}
        </Card>
      </div>

      <Card title="Individual Student Mastery">
        <DataTable
          cols={['Student', 'Overall', 'Weakest skill']}
          rows={students.map((s) => [
            s.name,
            <ProgressBar value={s.overall} />,
            SKILLS[s.m.indexOf(Math.min(...s.m))],
          ])}
        />
      </Card>

      <Card title="Assignment Performance">
        <DataTable
          cols={['Assignment', 'Skill', 'Completion']}
          rows={assignments0.map((a) => [
            a.name,
            a.skill,
            <ProgressBar value={a.done} />,
          ])}
        />
      </Card>
    </>
  );
}

// ==========================================
// NOTIFICATIONS
// ==========================================

export function Notifications({ role }) {
  const [n, setN] = useState(notifs[role]);

  return (
    <>
      <Head t="Notification Center">
        <Button
          v="ghost"
          onClick={() => setN(n.map((x) => [...x.slice(0, 3), 1]))}
        >
          Mark all read
        </Button>
      </Head>

      <Card title={`Recent (${n.filter((x) => !x[3]).length} unread)`}>
        {n.map((x, i) => (
          <NotificationItem
            key={i}
            n={x}
            onClick={() =>
              setN(
                n.map((y, j) =>
                  j === i ? [...y.slice(0, 3), 1] : y
                )
              )
            }
          />
        ))}
      </Card>
    </>
  );
}

// ==========================================
// WORKSHEET GENERATOR
// ==========================================

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
      <Head t="Worksheet Generator" />

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

// ==========================================
// PROFILE & SETTINGS
// ==========================================

export function Profile({ user }) {
  return (
    <>
      <Head t="Profile & Settings" />

      <Card title="Profile">
        <FormInput label="Name" defaultValue={user?.name} />
        <FormInput label="Email" defaultValue={user?.email} />
        <Button>Save</Button>
      </Card>
    </>
  );
}

// ==========================================
// PARENT DASHBOARD
// ==========================================

export function PDash() {
  const m = [90, 72, 85, 65, 60];
  const o = Math.round(m.reduce((a, b) => a + b) / 5);
  const w = m.indexOf(Math.min(...m));

  return (
    <>
      <Head t="Ana Reyes" />

      <div className="grid2">
        <Card title="Overall Mastery">
          <div className="big">{o}%</div>
          <ProgressBar value={o} />
        </Card>

        <Card title="Suggested Practice">
          <p>
            Your child may benefit from additional practice with{' '}
            {SKILLS[w].toLowerCase()}. Try 10 minutes a day using a number line.
          </p>
        </Card>
      </div>

      <Card title="Skill Progress">
        {SKILLS.map((s, i) => (
          <ProgressBar key={s} label={s} value={m[i]} />
        ))}
      </Card>

      <Card title="Recent Activity">
        <ul>
          <li>Completed Regrouping Practice 1 (4/4)</li>
          <li>Started Borrowing Basics</li>
          <li>Earned "Addition Star" badge</li>
        </ul>
      </Card>
    </>
  );
}
