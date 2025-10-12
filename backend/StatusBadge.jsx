// StatusBadge.jsx
export default function StatusBadge({ status }) {
  const s = (status || '').toLowerCase();

  const config = {
    approved: {
      bgColor: 'rgba(16, 185, 129, 0.15)',
      textColor: '#059669',
      dotColor: '#10b981',
      label: 'Approved',
    },
    rejected: {
      bgColor: 'rgba(239, 68, 68, 0.15)',
      textColor: '#dc2626',
      dotColor: '#ef4444',
      label: 'Rejected',
    },
    pending: {
      bgColor: 'rgba(245, 158, 11, 0.15)',
      textColor: '#d97706',
      dotColor: '#f59e0b',
      label: 'Pending',
    },
  }[s] || {
    bgColor: 'rgba(107, 114, 128, 0.15)',
    textColor: '#6b7280',
    dotColor: '#9ca3af',
    label: status,
  };

  return (
    <span
      className="d-inline-flex align-items-center gap-2 px-3 py-1 shadow-sm"
      style={{
        color: config.textColor,
        backgroundColor: config.bgColor,
        borderRadius: '16px',
      }}
    >
      <span
        className="rounded-circle d-inline-block"
        style={{
          width: '8px',
          height: '8px',
          backgroundColor: config.dotColor,
        }}
      ></span>
      <span className="fw-medium" style={{ fontSize: '0.875rem' }}>
        {config.label}
      </span>
    </span>
  );
}
