import React, { useState, useEffect, useCallback } from 'react';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../components/Toast';
import { leaveApi } from '../api/client';
import { CalendarDays, Plus, Check, X, Clock, Filter, AlertCircle } from 'lucide-react';
import StatusBadge from '../components/StatusBadge';
import Modal from '../components/Modal';

const LeavesView = () => {
  const { user, isAdmin, isManager } = useAuth();
  const { showToast } = useToast();

  const [leaves, setLeaves] = useState([]);
  const [loading, setLoading] = useState(false);
  const [filterStatus, setFilterStatus] = useState('');
  const [filterType, setFilterType] = useState('');

  // Pagination
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);

  // Modal State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formData, setFormData] = useState({
    leaveType: 'CASUAL',
    startDate: new Date().toISOString().split('T')[0],
    endDate: new Date().toISOString().split('T')[0],
  });

  const fetchLeaves = useCallback(async () => {
    try {
      setLoading(true);
      const params = {
        page,
        size: 10,
        sort: 'leaveId,desc',
      };
      if (filterStatus) params.status = filterStatus;
      if (filterType) params.leaveType = filterType;

      const res = await leaveApi.getAll(params);
      setLeaves(res.data.content || []);
      setTotalPages(res.data.totalPages || 1);
      setTotalElements(res.data.totalElements || 0);
    } catch (err) {
      showToast('Failed to load leave records.', 'error');
    } finally {
      setLoading(false);
    }
  }, [page, filterStatus, filterType, showToast]);

  useEffect(() => {
    fetchLeaves();
  }, [fetchLeaves]);

  const handleApplyLeave = async (e) => {
    e.preventDefault();
    if (new Date(formData.startDate) > new Date(formData.endDate)) {
      showToast('Start date cannot be after end date.', 'warning');
      return;
    }

    try {
      await leaveApi.apply(formData);
      showToast('Leave request submitted successfully!', 'success');
      setIsModalOpen(false);
      fetchLeaves();
    } catch (err) {
      showToast(err.response?.data?.message || 'Failed to submit leave request.', 'error');
    }
  };

  const handleUpdateStatus = async (id, status) => {
    try {
      await leaveApi.updateStatus(id, status);
      showToast(`Leave request ${status.toLowerCase()}!`, 'success');
      fetchLeaves();
    } catch (err) {
      showToast(err.response?.data?.message || 'Failed to update leave status.', 'error');
    }
  };

  return (
    <div className="page-container">
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px', flexWrap: 'wrap', gap: '16px' }}>
        <div>
          <h2 style={{ fontSize: '1.5rem', fontWeight: 800 }}>Leave Management</h2>
          <p style={{ fontSize: '0.875rem', color: 'var(--text-muted)' }}>
            Submit time-off applications and manage team approvals.
          </p>
        </div>

        <button className="btn btn-primary" onClick={() => setIsModalOpen(true)}>
          <Plus size={18} />
          Apply for Leave
        </button>
      </div>

      {/* Filter Bar */}
      <div className="card" style={{ marginBottom: '24px', padding: '16px 20px' }}>
        <div style={{ display: 'flex', gap: '16px', flexWrap: 'wrap', alignItems: 'center' }}>
          <div>
            <label className="form-label" style={{ fontSize: '0.75rem' }}>Filter by Status</label>
            <select
              className="form-select"
              value={filterStatus}
              onChange={(e) => { setFilterStatus(e.target.value); setPage(0); }}
              style={{ minWidth: '180px' }}
            >
              <option value="">All Statuses</option>
              <option value="PENDING">Pending Approval</option>
              <option value="APPROVED">Approved</option>
              <option value="REJECTED">Rejected</option>
            </select>
          </div>

          <div>
            <label className="form-label" style={{ fontSize: '0.75rem' }}>Filter by Leave Type</label>
            <select
              className="form-select"
              value={filterType}
              onChange={(e) => { setFilterType(e.target.value); setPage(0); }}
              style={{ minWidth: '180px' }}
            >
              <option value="">All Types</option>
              <option value="CASUAL">Casual Leave</option>
              <option value="SICK">Sick Leave</option>
              <option value="VACATION">Vacation Leave</option>
            </select>
          </div>
        </div>
      </div>

      {/* Table */}
      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Applicant</th>
              <th>Leave Type</th>
              <th>Duration (Dates)</th>
              <th>Status</th>
              <th>Reviewed By</th>
              {(isAdmin || isManager) && <th style={{ textAlign: 'right' }}>Review Actions</th>}
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={7} style={{ textAlign: 'center', padding: '32px', color: 'var(--text-muted)' }}>
                  Loading leave records...
                </td>
              </tr>
            ) : leaves.length === 0 ? (
              <tr>
                <td colSpan={7} style={{ textAlign: 'center', padding: '32px', color: 'var(--text-muted)' }}>
                  No leave requests found.
                </td>
              </tr>
            ) : (
              leaves.map((leave) => (
                <tr key={leave.leaveId}>
                  <td style={{ fontFamily: 'var(--font-mono)', color: 'var(--text-muted)' }}>
                    #{leave.leaveId}
                  </td>
                  <td style={{ fontWeight: 600 }}>
                    {leave.employeeName || 'Staff Member'}
                  </td>
                  <td>
                    <span className="badge badge-purple">{leave.leaveType}</span>
                  </td>
                  <td style={{ fontFamily: 'var(--font-mono)', fontSize: '0.8125rem' }}>
                    {leave.startDate} → {leave.endDate}
                  </td>
                  <td>
                    <StatusBadge status={leave.status} />
                  </td>
                  <td style={{ color: leave.approvedByName ? 'var(--text-main)' : 'var(--text-subtle)' }}>
                    {leave.approvedByName ? leave.approvedByName : 'Awaiting Review'}
                  </td>

                  {(isAdmin || isManager) && (
                    <td style={{ textAlign: 'right' }}>
                      {leave.status === 'PENDING' ? (
                        <div style={{ display: 'inline-flex', gap: '8px' }}>
                          <button
                            className="btn btn-success btn-sm"
                            onClick={() => handleUpdateStatus(leave.leaveId, 'APPROVED')}
                            title="Approve Leave"
                          >
                            <Check size={14} /> Approve
                          </button>
                          <button
                            className="btn btn-danger btn-sm"
                            onClick={() => handleUpdateStatus(leave.leaveId, 'REJECTED')}
                            title="Reject Leave"
                          >
                            <X size={14} /> Reject
                          </button>
                        </div>
                      ) : (
                        <span style={{ fontSize: '0.75rem', color: 'var(--text-subtle)' }}>Finalized</span>
                      )}
                    </td>
                  )}
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Apply Modal */}
      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title="Apply for Time Off"
      >
        <form onSubmit={handleApplyLeave}>
          <div className="form-group">
            <label className="form-label">Type of Leave *</label>
            <select
              className="form-select"
              value={formData.leaveType}
              onChange={(e) => setFormData({ ...formData, leaveType: e.target.value })}
            >
              <option value="CASUAL">Casual Leave</option>
              <option value="SICK">Sick Leave</option>
              <option value="VACATION">Vacation / Paid Time Off</option>
            </select>
          </div>

          <div className="grid-2">
            <div className="form-group">
              <label className="form-label">Start Date *</label>
              <input
                type="date"
                className="form-input"
                value={formData.startDate}
                onChange={(e) => setFormData({ ...formData, startDate: e.target.value })}
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label">End Date *</label>
              <input
                type="date"
                className="form-input"
                value={formData.endDate}
                onChange={(e) => setFormData({ ...formData, endDate: e.target.value })}
                required
              />
            </div>
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px', marginTop: '24px' }}>
            <button
              type="button"
              className="btn btn-secondary"
              onClick={() => setIsModalOpen(false)}
            >
              Cancel
            </button>
            <button
              type="submit"
              className="btn btn-primary"
            >
              Submit Application
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default LeavesView;
