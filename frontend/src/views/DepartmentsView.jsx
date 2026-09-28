import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../components/Toast';
import { departmentApi, employeeApi } from '../api/client';
import { Building2, Plus, Edit2, Trash2, Crown, ShieldAlert } from 'lucide-react';
import Modal from '../components/Modal';

const DepartmentsView = () => {
  const { isAdmin } = useAuth();
  const { showToast } = useToast();

  const [departments, setDepartments] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [loading, setLoading] = useState(false);

  // Modal State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [currentDeptId, setCurrentDeptId] = useState(null);

  const [formData, setFormData] = useState({
    name: '',
    headEmployeeId: '',
  });

  const fetchDepartments = async () => {
    try {
      setLoading(true);
      const res = await departmentApi.getAll();
      setDepartments(res.data || []);
    } catch (err) {
      showToast('Failed to load departments.', 'error');
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
    fetchDepartments();
    fetchEmployees();
  }, []);

  const handleOpenAddModal = () => {
    setIsEditing(false);
    setCurrentDeptId(null);
    setFormData({ name: '', headEmployeeId: '' });
    setIsModalOpen(true);
  };

  const handleOpenEditModal = (dept) => {
    setIsEditing(true);
    setCurrentDeptId(dept.departmentId);
    setFormData({
      name: dept.name,
      headEmployeeId: dept.headEmployeeId || '',
    });
    setIsModalOpen(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        name: formData.name,
        headEmployeeId: formData.headEmployeeId ? Number(formData.headEmployeeId) : null,
      };

      if (isEditing) {
        await departmentApi.update(currentDeptId, payload);
        showToast('Department updated successfully!', 'success');
      } else {
        await departmentApi.create(payload);
        showToast('Department created successfully!', 'success');
      }

      setIsModalOpen(false);
      fetchDepartments();
    } catch (err) {
      showToast(err.response?.data?.message || 'Failed to save department.', 'error');
    }
  };

  const handleDelete = async (id, name) => {
    if (!window.confirm(`Are you sure you want to delete the department "${name}"?`)) return;

    try {
      await departmentApi.delete(id);
      showToast(`Department "${name}" deleted.`, 'success');
      fetchDepartments();
    } catch (err) {
      showToast(err.response?.data?.message || 'Cannot delete department. Employees are still assigned.', 'error');
    }
  };

  return (
    <div className="page-container">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px', flexWrap: 'wrap', gap: '16px' }}>
        <div>
          <h2 style={{ fontSize: '1.5rem', fontWeight: 800 }}>Department Structure</h2>
          <p style={{ fontSize: '0.875rem', color: 'var(--text-muted)' }}>
            Organizational divisions and appointed department heads.
          </p>
        </div>

        {isAdmin && (
          <button className="btn btn-primary" onClick={handleOpenAddModal}>
            <Plus size={18} />
            Create Department
          </button>
        )}
      </div>

      {/* Grid of Department Cards */}
      {loading ? (
        <div style={{ textAlign: 'center', padding: '40px', color: 'var(--text-muted)' }}>
          Loading organizational departments...
        </div>
      ) : departments.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: '40px' }}>
          <Building2 size={36} color="var(--text-muted)" style={{ margin: '0 auto 12px' }} />
          <h3 style={{ fontSize: '1.1rem', fontWeight: 700 }}>No Departments Registered</h3>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem', marginTop: '4px' }}>
            Click "Create Department" to set up your first company department.
          </p>
        </div>
      ) : (
        <div className="grid-3">
          {departments.map((dept) => (
            <div key={dept.departmentId} className="card">
              <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', marginBottom: '16px' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                  <div style={{
                    width: '40px',
                    height: '40px',
                    borderRadius: '10px',
                    background: 'var(--cyan-subtle)',
                    color: 'var(--cyan)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center'
                  }}>
                    <Building2 size={22} />
                  </div>
                  <div>
                    <h3 style={{ fontSize: '1.15rem', fontWeight: 700, color: 'var(--text-main)' }}>{dept.name}</h3>
                    <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', fontFamily: 'var(--font-mono)' }}>
                      DEPT-ID #{dept.departmentId}
                    </span>
                  </div>
                </div>

                {isAdmin && (
                  <div style={{ display: 'flex', gap: '6px' }}>
                    <button
                      className="btn btn-secondary btn-sm"
                      onClick={() => handleOpenEditModal(dept)}
                      title="Edit"
                    >
                      <Edit2 size={13} />
                    </button>
                    <button
                      className="btn btn-danger btn-sm"
                      onClick={() => handleDelete(dept.departmentId, dept.name)}
                      title="Delete"
                    >
                      <Trash2 size={13} />
                    </button>
                  </div>
                )}
              </div>

              {/* Head of department info */}
              <div style={{
                background: 'var(--bg-input)',
                padding: '12px 14px',
                borderRadius: 'var(--radius-sm)',
                border: '1px solid var(--border-glass)',
                display: 'flex',
                alignItems: 'center',
                gap: '10px'
              }}>
                <Crown size={16} color="var(--amber)" />
                <div style={{ fontSize: '0.8125rem' }}>
                  <span style={{ color: 'var(--text-muted)', display: 'block', fontSize: '0.7rem' }}>Head of Department</span>
                  <span style={{ fontWeight: 600, color: dept.headEmployeeName ? 'var(--text-main)' : 'var(--text-subtle)' }}>
                    {dept.headEmployeeName ? dept.headEmployeeName : 'Position Vacant'}
                  </span>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Modal */}
      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title={isEditing ? 'Edit Department' : 'Create New Department'}
      >
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Department Name *</label>
            <input
              type="text"
              className="form-input"
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              placeholder="e.g. Engineering, Human Resources, Finance"
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Head Employee (Supervisor)</label>
            <select
              className="form-select"
              value={formData.headEmployeeId}
              onChange={(e) => setFormData({ ...formData, headEmployeeId: e.target.value })}
            >
              <option value="">None (Leave Position Vacant)</option>
              {employees.map((emp) => (
                <option key={emp.employeeId} value={emp.employeeId}>
                  {emp.name} ({emp.email})
                </option>
              ))}
            </select>
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
              {isEditing ? 'Save Changes' : 'Create Department'}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default DepartmentsView;
