import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Badge, Button, Card, DataTable, FormInput, Modal, PageHeader } from '../../components';
import { useAsync } from '../../hooks/useAsync';
import { getCurrentUser } from '../../services/authService';
import { courses } from '../../services/contentService';
import { statusLabel } from '../../utils/format';

export function Courses() {
  const user = getCurrentUser();
  const nav = useNavigate();
  const isAdmin = user.role === 'Admin';
  const { data, loading, error, reload } = useAsync(
    () => courses.list(isAdmin ? {} : { instructorId: user.id }),
    [user.id]
  );
  const [open, setOpen] = useState(false);
  const [f, setF] = useState({ title: '', description: '', category: 'Mathematics', language: 'English' });
  const [msg, setMsg] = useState('');

  const set = (k) => (e) => setF({ ...f, [k]: e.target.value });

  const create = async (e) => {
    e.preventDefault();
    if (!f.title.trim()) return setMsg('Give the course a title.');
    try {
      const course = await courses.create({ ...f, title: f.title.trim(), instructorId: user.id });
      nav(`/instructor/courses/${course.id}`);
    } catch (ex) {
      setMsg(ex.message);
    }
  };

  const act = async (fn) => {
    setMsg('');
    try {
      await fn();
      reload();
    } catch (ex) {
      setMsg(ex.message);
    }
  };

  return (
    <>
      <PageHeader t="Courses">
        <Button onClick={() => setOpen(true)}>+ New course</Button>
      </PageHeader>

      {msg && <p className="err">{msg}</p>}
      {loading && <p>Loading courses...</p>}
      {error && <p className="err">{error}</p>}

      {data && (
        <Card>
          <DataTable
            cols={['Course', 'Category', 'Instructor', 'Status', 'Actions']}
            rows={data.map((c) => [
              c.title,
              c.category || '-',
              c.instructorName,
              <Badge t={statusLabel(c.status)} />,
              <div className="row">
                <Link className="btn primary" to={`/instructor/courses/${c.id}`}>
                  Open builder
                </Link>
                <Button
                  v="ghost"
                  onClick={() => act(() => courses.setPublished(c.id, c.status !== 'PUBLISHED'))}
                >
                  {c.status === 'PUBLISHED' ? 'Unpublish' : 'Publish'}
                </Button>
                <Button
                  v="danger"
                  onClick={() =>
                    window.confirm(`Delete "${c.title}" and all of its content?`) &&
                    act(() => courses.remove(c.id))
                  }
                >
                  Delete
                </Button>
              </div>,
            ])}
          />
          {data.length === 0 && <p className="muted">No courses yet. Create your first one.</p>}
        </Card>
      )}

      {open && (
        <Modal title="New course" onClose={() => setOpen(false)}>
          <form onSubmit={create}>
            <FormInput label="Title" value={f.title} onChange={set('title')} />
            <FormInput label="Description">
              <textarea value={f.description} onChange={set('description')} />
            </FormInput>
            <FormInput label="Category" value={f.category} onChange={set('category')} />
            <FormInput label="Language" value={f.language} onChange={set('language')} />
            <Button>Create and open builder</Button>
          </form>
        </Modal>
      )}
    </>
  );
}
