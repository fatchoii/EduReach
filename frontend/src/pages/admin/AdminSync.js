import React, { useEffect, useState } from 'react';
import { Badge, Button, Card, DataTable, FormInput, PageHeader, Toggle } from '../../components';
import { useAsync } from '../../hooks/useAsync';
import { getSyncLogs, getSyncPolicy, saveSyncPolicy } from '../../services/adminService';
import { formatDateTime, statusLabel } from '../../utils/format';

export function AdminSync() {
  const logs = useAsync(getSyncLogs, [], { refreshMs: 10000 });
  const policy = useAsync(getSyncPolicy, []);
  const [form, setForm] = useState(null);
  const [msg, setMsg] = useState({ text: '', ok: false });

  useEffect(() => {
    if (policy.data && !form) setForm(policy.data);
  }, [policy.data, form]);

  const save = async () => {
    setMsg({ text: '', ok: false });
    try {
      await saveSyncPolicy({ autoSyncOnWifi: form.autoSyncOnWifi, maxPackageMb: Number(form.maxPackageMb) });
      setMsg({ text: 'Sync policy saved.', ok: true });
    } catch (ex) {
      setMsg({ text: ex.message, ok: false });
    }
  };

  return (
    <>
      <PageHeader t="Offline content and sync monitor" />

      <Card title="Sync policy">
        {msg.text && <p className={msg.ok ? 'ok' : 'err'}>{msg.text}</p>}
        {policy.error && <p className="err">{policy.error}</p>}
        {form && (
          <>
            <Toggle
              label="Auto-sync on Wi-Fi"
              hint="When on, devices on mobile data wait for Wi-Fi before uploading offline progress."
              checked={form.autoSyncOnWifi}
              onChange={(v) => setForm({ ...form, autoSyncOnWifi: v })}
            />
            <FormInput
              label="Offline package size limit (MB per course)"
              type="number"
              min="1"
              value={form.maxPackageMb}
              onChange={(e) => setForm({ ...form, maxPackageMb: e.target.value })}
            />
            <Button onClick={save}>Save policy</Button>
          </>
        )}
      </Card>

      <Card title="Sync monitor">
        {logs.loading && <p>Loading...</p>}
        {logs.error && <p className="err">{logs.error}</p>}
        {logs.data && (
          <DataTable
            cols={['Learner', 'Device', 'Event', 'Status', 'Details', 'Synced']}
            rows={logs.data.map((l) => [
              l.userName,
              l.deviceId || '-',
              statusLabel(l.eventType),
              <Badge t={statusLabel(l.status)} />,
              l.message || '-',
              formatDateTime(l.syncedAt),
            ])}
          />
        )}
        {logs.data && logs.data.length === 0 && <p className="muted">Nothing has been synced yet.</p>}
      </Card>
    </>
  );
}
