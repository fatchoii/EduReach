import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { useOnlineStatus } from '../../hooks/useOnlineStatus';
import { useQueueCount } from '../../hooks/useOfflineQueue';
import { syncNow } from '../../services/offlineService';
import { Button } from './Button';

/** Tells the learner when they are offline or have changes waiting to sync. */
export const OfflineBanner = () => {
  const online = useOnlineStatus();
  const pending = useQueueCount();
  const [busy, setBusy] = useState(false);

  if (online && pending === 0) return null;

  const sync = async () => {
    setBusy(true);
    try {
      await syncNow();
    } catch {
      // The sync page shows details; keep the banner quiet.
    } finally {
      setBusy(false);
    }
  };

  return (
    <div className={`banner ${online ? 'info' : 'warn'}`}>
      {online ? (
        <span>
          {pending} change{pending === 1 ? '' : 's'} saved on this device and waiting to sync.
        </span>
      ) : (
        <span>
          You are offline.{' '}
          {pending > 0
            ? `${pending} change${pending === 1 ? '' : 's'} saved on this device and waiting to sync.`
            : 'Your progress is saved on this device.'}{' '}
          It will sync automatically when the connection returns.{' '}
          <Link to="/learner/offline">Open downloaded courses</Link>
        </span>
      )}
      {online && pending > 0 && (
        <Button v="ghost" disabled={busy} onClick={sync}>
          {busy ? 'Syncing...' : 'Sync now'}
        </Button>
      )}
    </div>
  );
};
