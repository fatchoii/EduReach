import React from 'react';
import { Link } from 'react-router-dom';
import { Card } from '../../components';

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
