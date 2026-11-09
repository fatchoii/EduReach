import React from 'react';
import {
  Button,
  Card,
  FormInput,
  PageHeader,
} from '../../components';

export function Profile({ user }) {
  return (
    <>
      <PageHeader t="Profile & Settings" />

      <Card title="Profile">
        <FormInput label="Name" defaultValue={user?.name} />
        <FormInput label="Email" defaultValue={user?.email} />
        <Button>Save</Button>
      </Card>
    </>
  );
}
