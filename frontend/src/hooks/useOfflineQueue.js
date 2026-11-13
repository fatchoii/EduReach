import { useEffect, useState } from 'react';
import { autoSyncIfAllowed, getQueue, subscribe } from '../services/offlineService';

/** Number of changes waiting to be synced; updates whenever the queue changes. */
export function useQueueCount() {
  const [count, setCount] = useState(() => getQueue().length);
  useEffect(() => {
    const update = () => setCount(getQueue().length);
    update();
    return subscribe(update);
  }, []);
  return count;
}

/** Syncs offline work automatically: when the connection returns and every 30 seconds while online. */
export function useAutoSync(enabled) {
  useEffect(() => {
    if (!enabled) return undefined;
    const attempt = () => {
      if (navigator.onLine) autoSyncIfAllowed().catch(() => {});
    };
    attempt();
    window.addEventListener('online', attempt);
    const timer = setInterval(attempt, 30000);
    return () => {
      window.removeEventListener('online', attempt);
      clearInterval(timer);
    };
  }, [enabled]);
}
