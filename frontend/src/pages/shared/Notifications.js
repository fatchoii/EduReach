import React, { useEffect, useState } from 'react';
import {
  Button,
  Card,
  FormInput,
  NotificationItem,
  PageHeader,
  Toggle,
} from '../../components';
import { useAsync } from '../../hooks/useAsync';
import { getCurrentUser } from '../../services/authService';
import { runReminders } from '../../services/gradingService';
import {
  countUnread,
  getNotifications,
  getSettings,
  markAllRead,
  markRead,
  saveSettings,
} from '../../services/notificationService';
import { formatDateTime } from '../../utils/format';

const TYPE_ICONS = { DEADLINE: '⏰', NEW_CONTENT: '🆕', LIVE_SESSION: '🎥', GRADE: '✅', GENERAL: '🔔' };

export function Notifications() {
  const user = getCurrentUser();
  const staff = user.role === 'Instructor' || user.role === 'Admin';
  const list = useAsync(getNotifications, [], { refreshMs: 15000 });
  const settings = useAsync(getSettings, []);
  const [form, setForm] = useState(null);
  const [msg, setMsg] = useState({ text: '', ok: false });

  useEffect(() => {
    if (settings.data && !form) setForm(settings.data);
  }, [settings.data, form]);

  const say = (text, ok = true) => setMsg({ text, ok });

  const open = async (n) => {
    if (n.read) return;
    try {
      await markRead(n.id);
      list.reload();
    } catch (ex) {
      say(ex.message, false);
    }
  };

  const readAll = async () => {
    try {
      await markAllRead();
      list.reload();
    } catch (ex) {
      say(ex.message, false);
    }
  };

  const save = async () => {
    try {
      await saveSettings({ ...form, reminderDaysBefore: Number(form.reminderDaysBefore) });
      say('Reminder settings saved.');
    } catch (ex) {
      say(ex.message, false);
    }
  };

  const reminders = async () => {
    try {
      const result = await runReminders();
      say(`Deadline check finished: ${result.remindersCreated} reminder(s) sent.`);
      list.reload();
    } catch (ex) {
      say(ex.message, false);
    }
  };

  return (
    <>
      <PageHeader t="Notifications and reminders">
        <div className="row">
          {staff && (
            <Button v="ghost" onClick={reminders}>
              Run deadline check now
            </Button>
          )}
          <Button v="ghost" onClick={readAll}>
            Mark all read
          </Button>
        </div>
      </PageHeader>
      {msg.text && <p className={msg.ok ? 'ok' : 'err'}>{msg.text}</p>}

      <div className="grid2">
        <Card title={`Notification center${list.data ? ` (${countUnread(list.data)} unread)` : ''}`}>
          {list.loading && <p>Loading...</p>}
          {list.error && <p className="err">{list.error}</p>}
          {list.data && list.data.length === 0 && <p className="muted">No notifications yet.</p>}
          {list.data &&
            list.data.map((n) => (
              <NotificationItem
                key={n.id}
                n={[`${TYPE_ICONS[n.type] || '🔔'} ${n.title}`, n.message, formatDateTime(n.createdAt), n.read ? 1 : 0]}
                onClick={() => open(n)}
              />
            ))}
        </Card>

        <Card title="Reminder settings">
          {settings.error && <p className="err">{settings.error}</p>}
          {form && (
            <>
              <Toggle
                label="Deadline reminders"
                hint="Remind me before a quiz or assignment is due."
                checked={form.deadlineReminders}
                onChange={(v) => setForm({ ...form, deadlineReminders: v })}
              />
              <Toggle
                label="New content alerts"
                hint="Tell me when lessons, quizzes or courses are published."
                checked={form.newContentAlerts}
                onChange={(v) => setForm({ ...form, newContentAlerts: v })}
              />
              <Toggle
                label="Live session invites"
                hint="Tell me when a live session is scheduled."
                checked={form.liveSessionInvites}
                onChange={(v) => setForm({ ...form, liveSessionInvites: v })}
              />
              <Toggle
                label="Also send alerts by email"
                hint="Works when the school's email server is set up."
                checked={form.emailAlerts}
                onChange={(v) => setForm({ ...form, emailAlerts: v })}
              />
              <FormInput label="Remind me this many days before the deadline">
                <select
                  value={form.reminderDaysBefore}
                  onChange={(e) => setForm({ ...form, reminderDaysBefore: e.target.value })}
                >
                  {[0, 1, 2, 3, 5, 7].map((d) => (
                    <option key={d} value={d}>
                      {d === 0 ? 'On the due date' : `${d} day${d === 1 ? '' : 's'}`}
                    </option>
                  ))}
                </select>
              </FormInput>
              <Button onClick={save}>Save settings</Button>
            </>
          )}
        </Card>
      </div>
    </>
  );
}
