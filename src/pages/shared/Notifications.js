import React, { useState } from 'react';
import { notifs } from '../../data';
import {
  Button,
  Card,
  NotificationItem,
  PageHeader,
} from '../../components';

export function Notifications({ role }) {
  const [n, setN] = useState(notifs[role]);

  return (
    <>
      <PageHeader t="Notification Center">
        <Button
          v="ghost"
          onClick={() => setN(n.map((x) => [...x.slice(0, 3), 1]))}
        >
          Mark all read
        </Button>
      </PageHeader>

      <Card title={`Recent (${n.filter((x) => !x[3]).length} unread)`}>
        {n.map((x, i) => (
          <NotificationItem
            key={i}
            n={x}
            onClick={() =>
              setN(
                n.map((y, j) =>
                  j === i ? [...y.slice(0, 3), 1] : y
                )
              )
            }
          />
        ))}
      </Card>
    </>
  );
}
