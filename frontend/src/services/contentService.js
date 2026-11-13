import { del, get, post, put } from './api';
import { qs } from './query';

const crud = (path) => ({
  list: (params) => get(`/api/${path}${qs(params)}`),
  get: (id) => get(`/api/${path}/${id}`),
  create: (body) => post(`/api/${path}`, body),
  update: (id, body) => put(`/api/${path}/${id}`, body),
  remove: (id) => del(`/api/${path}/${id}`),
});

const publishable = (path) => ({
  setPublished: (id, on) => post(`/api/${path}/${id}/${on ? 'publish' : 'unpublish'}`),
});

export const courses = { ...crud('courses'), ...publishable('courses') };
export const modules = crud('modules');
export const lessons = { ...crud('lessons'), ...publishable('lessons') };
export const assessments = { ...crud('assessments'), ...publishable('assessments') };
export const questions = crud('questions');
export const liveSessions = crud('live-sessions');
export const enrollments = crud('enrollments');
