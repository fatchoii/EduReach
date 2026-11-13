import { get, post, put } from './api';

export const getNotifications = () => get('/api/me/notifications');
export const markRead = (id) => post(`/api/me/notifications/${id}/read`);
export const markAllRead = () => post('/api/me/notifications/read-all');
export const getSettings = () => get('/api/me/notification-settings');
export const saveSettings = (settings) => put('/api/me/notification-settings', settings);
export const countUnread = (list) => list.filter((n) => !n.read).length;
