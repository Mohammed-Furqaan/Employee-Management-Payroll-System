import React, { useState, useEffect, useCallback } from 'react';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../components/Toast';
import { employeeApi, departmentApi, roleApi } from '../api/client';
import { Plus, Search, Filter, Edit2, Trash2, ChevronLeft, ChevronRight, UserPlus } from 'lucide-react';
import StatusBadge from '../components/StatusBadge';
import Modal from '../components/Modal';

const EmployeesView = () => {
  const { isAdmin } = useAuth();
  const { showToast } = useToast();

  const [employees, setEmployees] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [roles, setRoles] = useState([]);
  const [allEmployeesList, setAllEmployeesList] = useState([]); // for manager selector

  const [loading, setLoading] = useState(false);

  // Pagination & Filter State
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [totalPages, setTotalPages] = useState(1);
  const [totalElements, setTotalElements] = useState(0);

  const [searchName, setSearchName] = useState('');
  const [searchEmail, setSearchEmail] = useState('');
  const [selectedDept, setSelectedDept] = useState('');
  const [selectedRole, setSelectedRole] = useState('');

  // Modal State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [currentEmployeeId, setCurrentEmployeeId] = useState(null);

  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    hireDate: new Date().toISOString().split('T')[0],
    password: '',
    departmentId: '',
    roleId: '',
    managerId: '',
  });

  const fetchEmployees = useCallback(async () => {
    try {
      setLoading(true);
      const params = {
        page,
        size: pageSize,
        sort: 'employeeId,asc',
      };
      if (searchName) params.name = searchName;
      if (searchEmail) params.email = searchEmail;
      if (selectedDept) params.departmentId = selectedDept;
      if (selectedRole) params.roleId = selectedRole;

      const response = await employeeApi.getAll(params);
      setEmployees(response.data.content || []);
      setTotalPages(response.data.totalPages || 1);
      setTotalElements(response.data.totalElements || 0);
    } catch (error) {
      showToast('Failed to load employee roster.', 'error');
    } finally {
      setLoading(false);
    }
  }, [page, pageSize, searchName, searchEmail, selectedDept, selectedRole, showToast]);

  const fetchMetadata = async () => {
    try {
      const [deptRes, roleRes, empAllRes] = await Promise.allSettled([
        departmentApi.getAll(),
        roleApi.getAll(),
        employeeApi.getAll({ size: 100 }),
      ]);

      if (deptRes.status === 'fulfilled') setDepartments(deptRes.value.data || []);
      if (roleRes.status === 'fulfilled') setRoles(roleRes.value.data || []);
      if (empAllRes.status === 'fulfilled') setAllEmployeesList(empAllRes.value.data.content || []);
    } catch (err) {
      console.error('Error fetching metadata:', err);
    }
  };

  useEffect(() => {
    fetchEmployees();
  }, [fetchEmployees]);

  useEffect(() => {
    fetchMetadata();
  }, []);

  const handleOpenAddModal = () => {
    setIsEditing(false);
    setCurrentEmployeeId(null);
    setFormData({
      name: '',
      email: '',
      phone: '',
      hireDate: new Date().toISOString().split('T')[0],
      password: '',
      departmentId: departments[0]?.departmentId || '',
      roleId: roles[0]?.roleId || '',
      managerId: '',
    });
    setIsModalOpen(true);
  };

  const handleOpenEditModal = (emp) => {
    setIsEditing(true);
    setCurrentEmployeeId(emp.employeeId);
    setFormData({
      name: emp.name || '',
      email: emp.email || '',
      phone: emp.phone || '',
      hireDate: emp.hireDate || new Date().toISOString().split('T')[0],
      password: '', // leave empty unless changing
      departmentId: emp.departmentId || '',
      roleId: emp.roleId || '',
      managerId: emp.managerId || '',
    });
    setIsModalOpen(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const payload = {
        name: formData.name,
        email: formData.email,
        phone: formData.phone,
        hireDate: formData.hireDate,
        password: formData.password || undefined,
        departmentId: formData.departmentId ? Number(formData.departmentId) : null,
        roleId: formData.roleId ? Number(formData.roleId) : null,
        managerId: formData.managerId ? Number(formData.managerId) : null,
      };

      if (isEditing) {
        await employeeApi.update(currentEmployeeId, payload);
        showToast('Employee updated successfully!', 'success');
      } else {
        await employeeApi.create(payload);
        showToast('Employee created successfully!', 'success');
      }

      setIsModalOpen(false);
      fetchEmployees();
      fetchMetadata();
    } catch (error) {
      const msg = error.response?.data?.message || 'Failed to save employee.';
      showToast(msg, 'error');
    }
  };

  const handleDelete = async (id, name) => {
    if (!window.confirm(`Are you sure you want to delete employee "${name}"?`)) return;

    try {
      await employeeApi.delete(id);
      showToast(`Employee ${name} deleted.`, 'success');
      fetchEmployees();
      fetchMetadata();
    } catch (error) {
      showToast(error.response?.data?.message || 'Failed to delete employee.', 'error');
    }
  };

  return (
    <div className="page-container">
      {/* Header & Controls */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px', flexWrap: 'wrap', gap: '16px' }}>
        <div>
          <h2 style={{ fontSize: '1.5rem', fontWeight: 800 }}>Employee Directory</h2>
          <p style={{ fontSize: '0.875rem', color: 'var(--text-muted)' }}>
            Total personnel registered: {totalElements}
          </p>
        </div>

        {isAdmin && (
          <button className="btn btn-primary" onClick={handleOpenAddModal}>
            <UserPlus size={18} />
            Add New Employee
          </button>
        )}
      </div>

      {/* Filter Bar */}
      <div className="card" style={{ marginBottom: '24px', padding: '16px 20px' }}>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '14px', alignItems: 'center' }}>
          <div>
            <label className="form-label" style={{ fontSize: '0.75rem' }}>Search by Name</label>
            <div style={{ position: 'relative' }}>
              <input
                type="text"
                className="form-input"
                placeholder="Search name..."
                value={searchName}
                onChange={(e) => { setSearchName(e.target.value); setPage(0); }}
              />
            </div>
          </div>

          <div>
            <label className="form-label" style={{ fontSize: '0.75rem' }}>Search by Email</label>
            <input
              type="text"
              className="form-input"
              placeholder="Search email..."
              value={searchEmail}
              onChange={(e) => { setSearchEmail(e.target.value); setPage(0); }}
            />
          </div>

          <div>
            <label className="form-label" style={{ fontSize: '0.75rem' }}>Filter Department</label>
            <select
              className="form-select"
              value={selectedDept}
              onChange={(e) => { setSelectedDept(e.target.value); setPage(0); }}
            >
              <option value="">All Departments</option>
              {departments.map((d) => (
                <option key={d.departmentId} value={d.departmentId}>{d.name}</option>
              ))}
            </select>
          </div>

          <div>
            <label className="form-label" style={{ fontSize: '0.75rem' }}>Filter Role</label>
            <select
              className="form-select"
              value={selectedRole}
              onChange={(e) => { setSelectedRole(e.target.value); setPage(0); }}
            >
              <option value="">All Roles</option>
              {roles.map((r) => (
                <option key={r.roleId} value={r.roleId}>{r.roleName}</option>
              ))}
            </select>
          </div>
        </div>
      </div>

      {/* Data Table */}
      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Employee Name</th>
              <th>Work Email</th>
              <th>Department</th>
              <th>Role</th>
              <th>Manager</th>
              <th>Hire Date</th>
              {isAdmin && <th style={{ textAlign: 'right' }}>Actions</th>}
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={isAdmin ? 8 : 7} style={{ textAlign: 'center', padding: '32px', color: 'var(--text-muted)' }}>
                  Loading employee records...
                </td>
              </tr>
            ) : employees.length === 0 ? (
              <tr>
                <td colSpan={isAdmin ? 8 : 7} style={{ textAlign: 'center', padding: '32px', color: 'var(--text-muted)' }}>
                  No employee records match the search criteria.
                </td>
              </tr>
            ) : (
              employees.map((emp) => (
                <tr key={emp.employeeId}>
                  <td style={{ fontFamily: 'var(--font-mono)', color: 'var(--text-muted)' }}>
                    #{emp.employeeId}
                  </td>
                  <td>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                      <div style={{
                        width: '32px',
                        height: '32px',
                        borderRadius: '50%',
                        background: 'linear-gradient(135deg, var(--primary) 0%, #a855f7 100%)',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        fontSize: '0.75rem',
                        fontWeight: 700,
                        color: '#fff'
                      }}>
                        {emp.name ? emp.name.charAt(0).toUpperCase() : 'E'}
                      </div>
                      <span style={{ fontWeight: 600 }}>{emp.name}</span>
                    </div>
                  </td>
                  <td>{emp.email}</td>
                  <td>
                    {emp.departmentName ? (
                      <span className="badge badge-primary">{emp.departmentName}</span>
                    ) : (
                      <span style={{ color: 'var(--text-subtle)' }}>Unassigned</span>
                    )}
                  </td>
                  <td>
                    <StatusBadge status={emp.roleName} />
                  </td>
                  <td>
                    {emp.managerName ? emp.managerName : <span style={{ color: 'var(--text-subtle)' }}>None</span>}
                  </td>
                  <td style={{ fontFamily: 'var(--font-mono)', fontSize: '0.8rem' }}>
                    {emp.hireDate}
                  </td>
                  {isAdmin && (
                    <td style={{ textAlign: 'right' }}>
                      <div style={{ display: 'inline-flex', gap: '8px' }}>
                        <button
                          className="btn btn-secondary btn-sm"
                          onClick={() => handleOpenEditModal(emp)}
                          title="Edit"
                        >
                          <Edit2 size={14} />
                        </button>
                        <button
                          className="btn btn-danger btn-sm"
                          onClick={() => handleDelete(emp.employeeId, emp.name)}
                          title="Delete"
                        >
                          <Trash2 size={14} />
                        </button>
                      </div>
                    </td>
                  )}
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* Pagination Controls */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '20px', flexWrap: 'wrap', gap: '12px' }}>
        <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
          Showing page {page + 1} of {totalPages} ({totalElements} total entries)
        </div>

        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <button
            className="btn btn-secondary btn-sm"
            onClick={() => setPage((p) => Math.max(0, p - 1))}
            disabled={page === 0}
          >
            <ChevronLeft size={16} /> Previous
          </button>

          <span style={{ padding: '4px 12px', fontSize: '0.85rem', fontWeight: 600, color: 'var(--primary)' }}>
            {page + 1}
          </span>

          <button
            className="btn btn-secondary btn-sm"
            onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
            disabled={page >= totalPages - 1}
          >
            Next <ChevronRight size={16} />
          </button>
        </div>
      </div>

      {/* Add / Edit Employee Modal */}
      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title={isEditing ? 'Edit Employee Profile' : 'Register New Employee'}
      >
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Full Name *</label>
            <input
              type="text"
              className="form-input"
              value={formData.name}
              onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              placeholder="Full name"
              required
            />
          </div>

          <div className="grid-2">
            <div className="form-group">
              <label className="form-label">Work Email *</label>
              <input
                type="email"
                className="form-input"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                placeholder="name@company.com"
                required
              />
            </div>
            <div className="form-group">
              <label className="form-label">Phone</label>
              <input
                type="tel"
                className="form-input"
                value={formData.phone}
                onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                placeholder="+1 (555) 000-0000"
              />
            </div>
          </div>

          <div className="grid-2">
            <div className="form-group">
              <label className="form-label">Hire Date *</label>
              <input
                type="date"
                className="form-input"
                value={formData.hireDate}
                onChange={(e) => setFormData({ ...formData, hireDate: e.target.value })}
                required
              />
            </div>
            <div className="form-group">
              <label className="form-label">{isEditing ? 'Password (Leave blank to keep)' : 'Initial Password *'}</label>
              <input
                type="password"
                className="form-input"
                value={formData.password}
                onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                placeholder="••••••••"
                required={!isEditing}
              />
            </div>
          </div>

          <div className="grid-2">
            <div className="form-group">
              <label className="form-label">Department</label>
              <select
                className="form-select"
                value={formData.departmentId}
                onChange={(e) => setFormData({ ...formData, departmentId: e.target.value })}
              >
                <option value="">None / Unassigned</option>
                {departments.map((d) => (
                  <option key={d.departmentId} value={d.departmentId}>{d.name}</option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label className="form-label">System Role</label>
              <select
                className="form-select"
                value={formData.roleId}
                onChange={(e) => setFormData({ ...formData, roleId: e.target.value })}
              >
                <option value="">None / Default</option>
                {roles.map((r) => (
                  <option key={r.roleId} value={r.roleId}>{r.roleName}</option>
                ))}
              </select>
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Reporting Manager</label>
            <select
              className="form-select"
              value={formData.managerId}
              onChange={(e) => setFormData({ ...formData, managerId: e.target.value })}
            >
              <option value="">No Manager (Self-Reporting / Executive)</option>
              {allEmployeesList
                .filter((emp) => !currentEmployeeId || emp.employeeId !== currentEmployeeId)
                .map((emp) => (
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
              {isEditing ? 'Save Changes' : 'Create Employee'}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
};

export default EmployeesView;
