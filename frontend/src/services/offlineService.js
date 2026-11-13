import { get, post } from './api';
import { getUser } from './session';
import { formatBytes, toLocalIso, uid } from '../utils/format';

// Offline storage lives in the browser (localStorage), separately for every signed-in user.
const keyFor = (name) => {
  const user = getUser();
  return `edureach.offline.${user ? user.id : 'anon'}.${name}`;
};

const read = (name, fallback) => {
  try {
    const raw = localStorage.getItem(keyFor(name));
    return raw ? JSON.parse(raw) : fallback;
  } catch {
    return fallback;
  }
};

const write = (name, value) => {
  try {
    localStorage.setItem(keyFor(name), JSON.stringify(value));
    return true;
  } catch {
    return false;
  }
};

const listeners = new Set();
const notify = () => listeners.forEach((fn) => fn());

/** Calls fn whenever downloads or the sync queue change. Returns an unsubscribe function. */
export const subscribe = (fn) => {
  listeners.add(fn);
  return () => listeners.delete(fn);
};

// ---- downloads
export const listDownloads = () => Object.values(read('packages', {}));
export const getDownload = (courseId) => read('packages', {})[courseId] || null;

export async function downloadCourse(courseId) {
  const pkg = await get(`/api/offline/courses/${courseId}/package`);
  if (!pkg.withinLimit) {
    throw new Error(
      `This course is ${formatBytes(pkg.sizeBytes)}, above the ${pkg.maxPackageMb} MB limit set by the admin.`
    );
  }
  const all = read('packages', {});
  all[courseId] = { ...pkg, downloadedAt: toLocalIso() };
  if (!write('packages', all)) {
    throw new Error('There is not enough browser storage to save this course.');
  }
  notify();
  return all[courseId];
}

export function removeDownload(courseId) {
  const all = read('packages', {});
  delete all[courseId];
  write('packages', all);
  notify();
}

// ---- lessons completed offline (shown as done before they sync)
export const getLocalCompleted = (courseId) => read('completed', {})[courseId] || [];

export function addLocalCompleted(courseId, lessonId) {
  const all = read('completed', {});
  const list = all[courseId] || [];
  if (!list.includes(lessonId)) list.push(lessonId);
  all[courseId] = list;
  write('completed', all);
  notify();
}

// ---- queue of things done offline
export const getQueue = () => read('queue', []);

export function queueEvent(event) {
  const queue = getQueue();
  const full = { clientId: uid(), occurredAt: toLocalIso(), ...event };
  queue.push(full);
  write('queue', queue);
  notify();
  return full;
}

export const getLastSync = () => read('lastSync', null);

const deviceId = () => {
  try {
    let id = localStorage.getItem('edureach.deviceId');
    if (!id) {
      id = `web-${uid()}`;
      localStorage.setItem('edureach.deviceId', id);
    }
    return id;
  } catch {
    return 'web-unknown';
  }
};

let syncing = false;

/** Uploads everything in the queue. Events that fail stay in the queue with the reason. */
export async function syncNow() {
  if (syncing) return null;
  const queue = getQueue();
  if (!queue.length) return { synced: 0, duplicates: 0, failed: 0, results: [] };
  syncing = true;
  try {
    const events = queue.map((e) => ({
      clientId: e.clientId,
      type: e.type,
      occurredAt: e.occurredAt,
      lessonId: e.lessonId,
      assessmentId: e.assessmentId,
      answers: e.answers,
    }));
    const response = await post('/api/sync', { deviceId: deviceId(), events });
    const byId = new Map(response.results.map((r) => [r.clientId, r]));
    const remaining = queue
      .filter((e) => {
        const result = byId.get(e.clientId);
        return !result || result.status === 'FAILED';
      })
      .map((e) => ({ ...e, error: (byId.get(e.clientId) || {}).message }));
    write('queue', remaining);
    write('lastSync', {
      at: toLocalIso(),
      synced: response.synced,
      duplicates: response.duplicates,
      failed: response.failed,
    });
    notify();
    return response;
  } finally {
    syncing = false;
  }
}

const onWifiOrUnknown = () => {
  const connection = navigator.connection;
  return !connection || !connection.type || ['wifi', 'ethernet', 'unknown'].includes(connection.type);
};

/** Background sync: runs when back online, unless the admin policy says Wi-Fi only and we are on mobile data. */
export async function autoSyncIfAllowed() {
  if (!getQueue().length) return null;
  const policy = await get('/api/sync-policy');
  if (policy.autoSyncOnWifi && !onWifiOrUnknown()) return null;
  return syncNow();
}
