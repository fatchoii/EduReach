import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Badge, Button, Card, DataTable, PageHeader } from '../../components';
import { useAsync } from '../../hooks/useAsync';
import { useOnlineStatus } from '../../hooks/useOnlineStatus';
import { getSyncPolicy } from '../../services/adminService';
import { getMyProgress } from '../../services/learnerService';
import {
  downloadCourse,
  getLastSync,
  getQueue,
  listDownloads,
  removeDownload,
  subscribe,
  syncNow,
} from '../../services/offlineService';
import { formatBytes, formatDateTime, statusLabel } from '../../utils/format';

const EVENT_LABELS = { LESSON_COMPLETE: 'Lesson completed', QUIZ_SUBMIT: 'Quiz answers' };

export function LearnerOffline() {
  const online = useOnlineStatus();
  const [, refresh] = useState(0);
  const [busy, setBusy] = useState('');
  const [msg, setMsg] = useState({ text: '', ok: false });
  const progress = useAsync(getMyProgress, []);
  const policy = useAsync(getSyncPolicy, []);

  useEffect(() => subscribe(() => refresh((n) => n + 1)), []);

  const downloads = listDownloads();
  const downloadedIds = new Set(downloads.map((d) => d.courseId));
  const queue = getQueue();
  const last = getLastSync();
  const available = (progress.data || []).filter((p) => !downloadedIds.has(p.courseId));

  const download = async (courseId) => {
    setBusy(`d${courseId}`);
    setMsg({ text: '', ok: false });
    try {
      const pkg = await downloadCourse(courseId);
      setMsg({ text: `${pkg.courseTitle} downloaded (${formatBytes(pkg.sizeBytes)}).`, ok: true });
    } catch (ex) {
      setMsg({ text: ex.message, ok: false });
    } finally {
      setBusy('');
    }
  };

  const sync = async () => {
    setBusy('sync');
    setMsg({ text: '', ok: false });
    try {
      const response = await syncNow();
      if (response) {
        setMsg({
          text: `Sync finished: ${response.synced} saved, ${response.duplicates} already saved, ${response.failed} failed.`,
          ok: response.failed === 0,
        });
      }
    } catch (ex) {
      setMsg({ text: ex.message, ok: false });
    } finally {
      setBusy('');
    }
  };

  return (
    <>
      <PageHeader t="Offline downloads and sync">
        <Badge t={online ? 'Online' : 'Offline'} />
      </PageHeader>
      {msg.text && <p className={msg.ok ? 'ok' : 'err'}>{msg.text}</p>}

      <Card title="Downloaded courses">
        <DataTable
          cols={['Course', 'Size', 'Downloaded', '']}
          rows={downloads.map((d) => [
            d.courseTitle,
            formatBytes(d.sizeBytes),
            formatDateTime(d.downloadedAt),
            <div className="row">
              <Link className="btn primary" to={`/learner/offline/${d.courseId}`}>
                Open offline
              </Link>
              <Button v="danger" onClick={() => removeDownload(d.courseId)}>
                Remove
              </Button>
            </div>,
          ])}
        />
        {downloads.length === 0 && <p className="muted">Nothing is downloaded yet.</p>}
      </Card>

      <Card title="Available to download">
        {progress.loading && <p>Loading...</p>}
        {progress.error && <p className="muted">Connect to the internet to see your courses.</p>}
        {available.map((p) => (
          <div key={p.courseId} className="ch">
            <b>{p.courseTitle}</b>
            <Button disabled={!!busy} onClick={() => download(p.courseId)}>
              {busy === `d${p.courseId}` ? 'Downloading...' : 'Download'}
            </Button>
          </div>
        ))}
        {progress.data && available.length === 0 && <p className="muted">All your courses are downloaded.</p>}
      </Card>

      <Card
        title={`Sync (${queue.length} waiting)`}
        right={
          <Button disabled={!online || !!busy || queue.length === 0} onClick={sync}>
            {busy === 'sync' ? 'Syncing...' : 'Sync now'}
          </Button>
        }
      >
        {queue.length > 0 && (
          <DataTable
            cols={['What', 'When', 'Status']}
            rows={queue.map((e) => [
              e.label || EVENT_LABELS[e.type] || statusLabel(e.type),
              formatDateTime(e.occurredAt),
              e.error ? <Badge t="Failed" /> : <Badge t="Waiting" />,
            ])}
          />
        )}
        {queue.some((e) => e.error) && (
          <p className="err">Some items failed: {queue.find((e) => e.error).error}</p>
        )}
        {queue.length === 0 && <p className="muted">Everything is synced.</p>}
        {last && (
          <p className="muted">
            Last sync {formatDateTime(last.at)}: {last.synced} saved, {last.duplicates} already saved, {last.failed} failed.
          </p>
        )}
        {policy.data && (
          <p className="muted">
            Policy: auto-sync {policy.data.autoSyncOnWifi ? 'on Wi-Fi only' : 'on any connection'}. Maximum download
            size {policy.data.maxPackageMb} MB per course.
          </p>
        )}
      </Card>
    </>
  );
}
