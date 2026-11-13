import { del, get, put } from './api';
import { qs } from './query';

export const getUsers = (role) => get(`/api/users${qs({ role })}`);
export const changeRole = (id, role) => put(`/api/users/${id}/role`, { role });
export const deleteUser = (id) => del(`/api/users/${id}`);
export const getSyncLogs = () => get('/api/admin/sync-logs');
export const getSyncPolicy = () => get('/api/sync-policy');
export const saveSyncPolicy = (policy) => put('/api/sync-policy', policy);
