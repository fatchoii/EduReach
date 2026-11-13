import { useEffect, useState } from 'react';
import { countUnread, getNotifications } from '../services/notificationService';

/** Unread notification count, refreshed every 20 seconds. */
export function useUnread(enabled) {
  const [count, setCount] = useState(0);
  useEffect(() => {
    if (!enabled) return undefined;
    let active = true;
    const load = () => {
      if (!navigator.onLine) return;
      getNotifications()
        .then((list) => active && setCount(countUnread(list)))
        .catch(() => {});
    };
    load();
    const timer = setInterval(load, 20000);
    return () => {
      active = false;
      clearInterval(timer);
    };
  }, [enabled]);
  return count;
}
