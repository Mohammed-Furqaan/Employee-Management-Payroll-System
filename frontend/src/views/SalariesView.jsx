import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../components/Toast';
import { salaryApi, employeeApi } from '../api/client';
import { DollarSign, Plus, FileText, CheckCircle2, TrendingUp, Calculator } from 'lucide-react';
import Modal from '../components/Modal';

const SalariesView = () => {
  const { isAdmin } = useAuth();
  const { showToast } = useToast();

  const [salaries, setSalaries] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(false);

  // Modal State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [formData, setFormData] = useState({
    employeeId: '',
    basicPay: '5000.00',
    allowances: '500.00',
    deductions: '300.00',
    month: new Date().getMonth() + 1,
    year: new Date().getFullYear(),
  });

  const fetchSalaries = async () => {
    try {
      setLoading(true);
      const res = await salaryApi.getAll();
      setSalaries(res.data || []);
    } catch (err) {
      showToast('Failed to load salary logs.', 'error');
    } finally {
      setLoading(false);
    }
  };

  const fetchEmployees = async () => {
    try {
      const res = await employeeApi.getAll({ size: 100 });
      setEmployees(res.data.content || []);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchSalaries();
    if (isAdmin) {
      fetchEmployees();
    }
  }, [isAdmin]);

  const handleOpenGenerateModal = () => {
    setFormData({
      employeeId: employees[0]?.employeeId || '',
      basicPay: '5000.00',
      allowances: '500.00',
      deductions: '300.00',
      month: new Date().getMonth() + 1,
      year: new Date().getFullYear(),
    });
    setIsModalOpen(true);
  };

  const calculatedNetPay = (
    parseFloat(formData.basicPay || 0) +
    parseFloat(formData.allowances || 0) -
    parseFloat(formData.deductions || 0)
  ).toFixed(2);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (parseFloat(calculatedNetPay) < 0) {
      showToast('Net pay cannot be negative. Please adjust deductions.', 'warning');
      return;
    }

    try {
      const payload = {
        employeeId: Number(formData.employeeId),
        basicPay: parseFloat(formData.basicPay),
        allowances: parseFloat(formData.allowances),
        deductions: parseFloat(formData.deductions),
        month: Number(formData.month),
        year: Number(formData.year),
      };

      await salaryApi.generate(payload);
      showToast('Salary slip generated successfully!', 'success');
      setIsModalOpen(false);
      fetchSalaries();
    } catch (err) {
      showToast(err.response?.data?.message || 'Failed to generate salary.', 'error');
    }
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(amount || 0);
  };

  const getMonthName = (monthNumber) => {
    const date = new Date();
    date.setMonth(monthNumber - 1);
    return date.toLocaleString('en-US', { month: 'long' });
  };

  return (
    <div className="page-container">
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px', flexWrap: 'wrap', gap: '16px' }}>
        <div>
          <h2 style={{ fontSize: '1.5rem', fontWeight: 800 }}>Payroll & Salaries</h2>
          <p style={{ fontSize: '0.875rem', color: 'var(--text-muted)' }}>
            Monthly salary distribution and compensation records.
          </p>
        </div>

        {isAdmin && (
          <button className="btn btn-primary" onClick={handleOpenGenerateModal}>
            <Plus size={18} />
            Generate Monthly Pay Slip
          </button>
        )}
      </div>

      {/* Table */}
      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>Slip ID</th>
              <th>Employee Name</th>
              <th>Pay Period</th>
              <th>Basic Pay</th>
              <th>Allowances</th>
              <th>Deductions</th>
              <th>Calculated Net Pay</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={7} style={{ textAlign: 'center', padding: '32px', color: 'var(--text-muted)' }}>
                  Loading salary records...
                </td>
              </tr>
            ) : salaries.length === 0 ? (
              <tr>
                <td colSpan={7} style={{ textAlign: 'center', padding: '32px', color: 'var(--text-muted)' }}>
                  No salary slips generated yet.
                </td>
              </tr>
            ) : (
              salaries.map((sal) => (
                <tr key={sal.salaryId}>
                  <td style={{ fontFamily: 'var(--font-mono)', color: 'var(--text-muted)' }}>
                    #{sal.salaryId}
                  </td>
                  <td style={{ fontWeight: 600 }}>
                    {sal.employeeName || 'Staff Member'}
                  </td>
                  <td>
                    <span className="badge badge-purple">
                      {getMonthName(sal.month)} {sal.year}
                    </span>
                  </td>
                  <td style={{ fontFamily: 'var(--font-mono)' }}>
                    {formatCurrency(sal.basicPay)}
                  </td>
                  <td style={{ fontFamily: 'var(--font-mono)', color: 'var(--emerald)' }}>
                    +{formatCurrency(sal.allowances)}
                  </td>
                  <td style={{ fontFamily: 'var(--font-mono)', color: 'var(--rose)' }}>
                    -{formatCurrency(sal.deductions)}
                  </td>
                  <td>
                    <span style={{
                      fontFamily: 'var(--font-mono)',
                      fontWeight: 700,
                      color: 'var(--emerald)',
                      padding: '4px 10px',
                      borderRadius: '6px',
                      background: 'var(--emerald-subtle)',
                      border: '1px solid rgba(16, 185, 129, 0.2)'
                    }}>
                      {formatCurrency(sal.netPay)}
                    </span>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Generate Salary Modal */}
      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title="Generate Monthly Salary Slip"
      >
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Select Employee *</label>
            <select
              className="form-select"
              value={formData.employeeId}
              onChange={(e) => setFormData({ ...formData, employeeId: e.target.value })}
              required
            >
              <option value="">Choose employee...</option>
              {employees.map((emp) => (
                <option key={emp.employeeId} value={emp.employeeId}>
                  {emp.name} ({emp.email})
                </option>
              ))}
            </select>
          </div>

          <div className="grid-2">
            <div className="form-group">
              <label className="form-label">Month *</label>
              <select
                className="form-select"
                value={formData.month}
                onChange={(e) => setFormData({ ...formData, month: e.target.value })}
              >
                {Array.from({ length: 12 }, (_, i) => i + 1).map((m) => (
                  <option key={m} value={m}>{getMonthName(m)}</option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label className="form-label">Year *</label>
              <input
                type="number"
                className="form-input"
                value={formData.year}
                onChange={(e) => setFormData({ ...formData, year: e.target.value })}
                required
              />
            </div>
          </div>

          <div className="grid-3">
            <div className="form-group">
              <label className="form-label">Basic Pay ($) *</label>
              <input
                type="number"
                step="0.01"
                min="0"
                className="form-input"
                value={formData.basicPay}
                onChange={(e) => setFormData({ ...formData, basicPay: e.target.value })}
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label">Allowances ($) *</label>
              <input
                type="number"
                step="0.01"
                min="0"
                className="form-input"
                value={formData.allowances}
                onChange={(e) => setFormData({ ...formData, allowances: e.target.value })}
                required
              />
            </div>

            <div className="form-group">
              <label className="form-label">Deductions ($) *</label>
              <input
                type="number"
                step="0.01"
                min="0"
                className="form-input"
                value={formData.deductions}
                onChange={(e) => setFormData({ ...formData, deductions: e.target.value })}
                required
              />
            </div>
          </div>

          {/* Live Calculation Preview Box */}
          <div style={{
            background: 'var(--bg-input)',
            border: '1px solid var(--border-glass)',
            borderRadius: 'var(--radius-sm)',
            padding: '16px',
            marginTop: '8px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between'
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: 'var(--text-muted)' }}>
              <Calculator size={18} />
              <span style={{ fontSize: '0.85rem' }}>Calculated Net Pay:</span>
            </div>
            <span style={{
              fontFamily: 'var(--font-mono)',
              fontSize: '1.25rem',
              fontWeight: 800,
              color: parseFloat(calculatedNetPay) >= 0 ? 'var(--emerald)' : 'var(--rose)'
            }}>
              ${calculatedNetPay}
            </span>
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
              Generate Pay Slip
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default SalariesView;
