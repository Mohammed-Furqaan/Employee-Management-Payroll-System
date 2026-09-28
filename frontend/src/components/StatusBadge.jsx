import React from 'react';

const StatusBadge = ({ status }) => {
  if (!status) return null;

  let badgeClass = 'badge-primary';

  switch (status) {
    case 'APPROVED':
    case 'PRESENT':
      badgeClass = 'badge-emerald';
      break;
    case 'PENDING':
    case 'LATE':
      badgeClass = 'badge-amber';
      break;
    case 'REJECTED':
    case 'ABSENT':
      badgeClass = 'badge-rose';
      break;
    case 'ROLE_ADMIN':
      badgeClass = 'badge-purple';
      break;
    case 'ROLE_MANAGER':
      badgeClass = 'badge-primary';
      break;
    case 'ROLE_EMPLOYEE':
      badgeClass = 'badge-cyan';
      break;
    default:
      badgeClass = 'badge-primary';
  }

  // Format label: remove ROLE_ prefix if role
  const displayLabel = status.startsWith('ROLE_') ? status.replace('ROLE_', '') : status;

  return <span className={`badge ${badgeClass}`}>{displayLabel}</span>;
};

export default StatusBadge;
