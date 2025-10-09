export default function StatusBadge({ status }) {
  const getStatusConfig = (status) => {
    switch (status) {
      case 'accepted':
      case 'Approved':
        return {
          bgColor: 'rgba(16, 185, 129, 0.15)',
          textColor: '#059669',
          dotColor: '#10b981',
          icon: 'bi-check-circle-fill',
          label: 'Approved'
        };
      case 'rejected':
      case 'Rejected':
        return {
          bgColor: 'rgba(239, 68, 68, 0.15)',
          textColor: '#dc2626',
          dotColor: '#ef4444',
          icon: 'bi-x-circle-fill',
          label: 'Rejected'
        };
      case 'pending':
      case 'Pending':
        return {
          bgColor: 'rgba(245, 158, 11, 0.15)',
          textColor: '#d97706',
          dotColor: '#f59e0b',
          icon: 'bi-clock-fill',
          label: 'Pending'
        };
      default:
        return {
          bgColor: 'rgba(107, 114, 128, 0.15)',
          textColor: '#6b7280',
          dotColor: '#9ca3af',
          icon: 'bi-circle-fill',
          label: status
        };
    }
  };

  const config = getStatusConfig(status);

  return (
    <span
      className="d-inline-flex align-items-center gap-2 px-3 py-1 shadow-sm"
      style={{ 
        color: config.textColor,
        backgroundColor: config.bgColor,
        borderRadius: '16px'
      }}
      data-testid={`badge-status-${status.toLowerCase()}`}
    >
      <span 
        className="rounded-circle d-inline-block" 
        style={{ 
          width: '8px', 
          height: '8px', 
          backgroundColor: config.dotColor 
        }}
      ></span>
      <span className="fw-medium" style={{ fontSize: '0.875rem' }}>
        {config.label}
      </span>
    </span>
  );
}
