import { useState } from 'react';
import StatusBadge from '../components/StatusBadge';

// Mock data - all reports use same organization, department, and request date
const mockSubscriptions = [
  {
    id: '1',
    reportDomain: 'Financial Performance Report Q4 2024',
    organization: 'Standard Chartered Bank',
    requestDate: '2024-03-15',
    status: 'Approved',
    department: 'Finance Department'
  },
  {
    id: '2',
    reportDomain: 'Market Analysis Report - Asia Pacific',
    organization: 'Standard Chartered Bank',
    requestDate: '2024-03-15',
    status: 'Pending',
    department: 'Finance Department'
  },
  {
    id: '3',
    reportDomain: 'Risk Assessment Report 2024',
    organization: 'Standard Chartered Bank',
    requestDate: '2024-03-15',
    status: 'Approved',
    department: 'Finance Department'
  },
  {
    id: '4',
    reportDomain: 'Customer Satisfaction Survey Results',
    organization: 'Standard Chartered Bank',
    requestDate: '2024-03-15',
    status: 'Rejected',
    department: 'Finance Department'
  },
  {
    id: '5',
    reportDomain: 'Technology Infrastructure Review',
    organization: 'Standard Chartered Bank',
    requestDate: '2024-03-15',
    status: 'Pending',
    department: 'Finance Department'
  },
  {
    id: '6',
    reportDomain: 'HR Workforce Planning Report',
    organization: 'Standard Chartered Bank',
    requestDate: '2024-03-15',
    status: 'Approved',
    department: 'Finance Department'
  }
];

export default function SubscriptionDashboard() {
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('All');

  const filteredSubscriptions = mockSubscriptions.filter(sub => {
    const matchesSearch = searchQuery === '' || 
      sub.reportDomain.toLowerCase().includes(searchQuery.toLowerCase()) ||
      sub.organization.toLowerCase().includes(searchQuery.toLowerCase());
    
    const matchesStatus = statusFilter === 'All' || sub.status === statusFilter;
    
    return matchesSearch && matchesStatus;
  });

  return (
    <div 
      className="min-vh-100" 
      style={{ 
        background: 'linear-gradient(135deg, #e0f2f1 0%, #b2ebf2 50%, #80deea 100%)'
      }}
    >
      <main className="container py-4 py-md-5" style={{ maxWidth: '1200px' }}>
        <div className="bg-white shadow-lg p-4 mb-4" style={{ borderRadius: '24px' }}>
          <h1 
            className="fw-bold mb-4" 
            style={{ 
              fontSize: '2rem', 
              color: '#0d47a1'
            }} 
            data-testid="text-page-title"
          >
            Subscription Status
          </h1>

          <div className="row g-3 align-items-center mb-4">
            <div className="col-12 col-md-6">
              <div className="position-relative">
                <i className="bi bi-search position-absolute top-50 start-0 translate-middle-y ms-3 text-muted" style={{ fontSize: '1.1rem' }}></i>
                <input
                  type="search"
                  className="form-control ps-5 border-0 shadow-sm"
                  placeholder="Search reports..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  style={{ 
                    height: '48px',
                    borderRadius: '24px',
                    backgroundColor: '#f8f9fa'
                  }}
                  data-testid="input-search"
                />
              </div>
            </div>

            <div className="col-12 col-md-6">
              <div className="d-flex gap-2 justify-content-md-end flex-wrap">
                <button
                  className={`btn ${statusFilter === 'All' ? 'btn-primary' : 'btn-light'} shadow-sm`}
                  onClick={() => setStatusFilter('All')}
                  data-testid="button-filter-all"
                  style={{ 
                    minWidth: '90px',
                    borderRadius: '20px',
                    fontWeight: '500'
                  }}
                >
                  <i className="bi bi-circle-fill me-1" style={{ fontSize: '8px' }}></i>
                  All
                </button>
                <button
                  className={`btn ${statusFilter === 'Pending' ? 'btn-warning' : 'btn-light'} shadow-sm`}
                  onClick={() => setStatusFilter('Pending')}
                  data-testid="button-filter-pending"
                  style={{ 
                    minWidth: '90px',
                    borderRadius: '20px',
                    fontWeight: '500'
                  }}
                >
                  <i className="bi bi-circle-fill me-1" style={{ fontSize: '8px', color: '#f59e0b' }}></i>
                  Pending
                </button>
                <button
                  className={`btn ${statusFilter === 'Approved' ? 'btn-success' : 'btn-light'} shadow-sm`}
                  onClick={() => setStatusFilter('Approved')}
                  data-testid="button-filter-approved"
                  style={{ 
                    minWidth: '90px',
                    borderRadius: '20px',
                    fontWeight: '500'
                  }}
                >
                  <i className="bi bi-circle-fill me-1" style={{ fontSize: '8px', color: '#10b981' }}></i>
                  Approved
                </button>
                <button
                  className={`btn ${statusFilter === 'Rejected' ? 'btn-danger' : 'btn-light'} shadow-sm`}
                  onClick={() => setStatusFilter('Rejected')}
                  data-testid="button-filter-rejected"
                  style={{ 
                    minWidth: '90px',
                    borderRadius: '20px',
                    fontWeight: '500'
                  }}
                >
                  <i className="bi bi-circle-fill me-1" style={{ fontSize: '8px', color: '#ef4444' }}></i>
                  Rejected
                </button>
              </div>
            </div>
          </div>

          {filteredSubscriptions.length === 0 ? (
            <div className="text-center py-5">
              <div className="bg-light rounded-circle d-inline-flex align-items-center justify-content-center mb-3" 
                   style={{ width: '80px', height: '80px' }}>
                <i className="bi bi-inbox fs-1 text-muted"></i>
              </div>
              <p className="text-muted mb-0" data-testid="text-empty-state">No subscriptions yet.</p>
            </div>
          ) : (
            <div className="table-responsive">
              <table className="table align-middle mb-0">
                <thead>
                  <tr style={{ borderBottom: '2px solid #e9ecef' }}>
                    <th 
                      className="fw-semibold text-uppercase border-0" 
                      style={{ 
                        fontSize: '0.75rem', 
                        letterSpacing: '0.05em',
                        paddingTop: '1rem',
                        paddingBottom: '1rem',
                        color: '#6c757d'
                      }}
                    >
                      Report Domain
                    </th>
                    <th 
                      className="fw-semibold text-uppercase border-0 text-end" 
                      style={{ 
                        fontSize: '0.75rem', 
                        letterSpacing: '0.05em',
                        paddingTop: '1rem',
                        paddingBottom: '1rem',
                        color: '#6c757d'
                      }}
                    >
                      Status
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {filteredSubscriptions.map((subscription, index) => (
                    <tr 
                      key={subscription.id} 
                      data-testid={`row-subscription-${subscription.id}`}
                      className="hover-elevate transition"
                      style={{ 
                        borderBottom: index === filteredSubscriptions.length - 1 ? 'none' : '1px solid #f1f3f5',
                        cursor: 'pointer'
                      }}
                    >
                      <td className="py-3 border-0">
                        <div>
                          <div className="fw-medium mb-1" data-testid={`text-report-name-${subscription.id}`}>
                            {subscription.reportDomain}
                          </div>
                          <div className="d-flex flex-wrap gap-2 align-items-center">
                            <span className="badge bg-light text-dark border-0" style={{ borderRadius: '12px', fontWeight: '500' }}>
                              <i className="bi bi-building me-1"></i>
                              {subscription.organization}
                            </span>
                            <span className="badge bg-light text-dark border-0" style={{ borderRadius: '12px', fontWeight: '500' }}>
                              <i className="bi bi-calendar3 me-1"></i>
                              {new Date(subscription.requestDate).toLocaleDateString('en-US', { 
                                month: 'short', 
                                day: 'numeric', 
                                year: 'numeric' 
                              })}
                            </span>
                            {subscription.department && (
                              <span className="badge bg-light text-dark border-0" style={{ borderRadius: '12px', fontWeight: '500' }}>
                                <i className="bi bi-briefcase me-1"></i>
                                {subscription.department}
                              </span>
                            )}
                          </div>
                        </div>
                      </td>
                      <td className="text-end py-3 border-0">
                        <StatusBadge status={subscription.status} />
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </main>
    </div>
  );
}
