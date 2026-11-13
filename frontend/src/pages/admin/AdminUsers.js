import React, { useState } from 'react';
import { Button, Card, DataTable, PageHeader } from '../../components';
import { useAsync } from '../../hooks/useAsync';
import { changeRole, deleteUser, getUsers } from '../../services/adminService';
import { getCurrentUser } from '../../services/authService';
import { formatDate } from '../../utils/format';

const ROLES = ['LEARNER', 'INSTRUCTOR', 'ADMIN', 'PARENT'];

export function AdminUsers() {
  const me = getCurrentUser();
  const { data, loading, error, reload } = useAsync(() => getUsers(), []);
  const [msg, setMsg] = useState({ text: '', ok: false });

  const act = async (fn, okText) => {
    setMsg({ text: '', ok: false });
    try {
      await fn();
      setMsg({ text: okText, ok: true });
      reload();
    } catch (ex) {
      setMsg({ text: ex.message, ok: false });
    }
  };

  return (
    <>
      <PageHeader t="Users and roles" />
      {msg.text && <p className={msg.ok ? 'ok' : 'err'}>{msg.text}</p>}
      {loading && <p>Loading users...</p>}
      {error && <p className="err">{error}</p>}
      {data && (
        <Card>
          <DataTable
            cols={['Name', 'Email', 'Role', 'Joined', '']}
            rows={data.map((u) => [
              u.fullName,
              u.email,
              <select
                value={u.role}
                disabled={u.id === me.id}
                onChange={(e) => act(() => changeRole(u.id, e.target.value), `${u.fullName} is now ${e.target.value.toLowerCase()}.`)}
              >
                {ROLES.map((r) => (
                  <option key={r} value={r}>
                    {r.charAt(0) + r.slice(1).toLowerCase()}
                  </option>
                ))}
              </select>,
              formatDate(u.createdAt),
              u.id === me.id ? (
                <small className="muted">You</small>
              ) : (
                <Button
                  v="danger"
                  onClick={() =>
                    window.confirm(`Delete ${u.fullName}?`) && act(() => deleteUser(u.id), 'User deleted.')
                  }
                >
                  Delete
                </Button>
              ),
            ])}
          />
        </Card>
      )}
    </>
  );
}
