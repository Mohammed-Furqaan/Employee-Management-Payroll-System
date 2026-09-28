import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../components/Toast';
import { employeeApi, departmentApi, leaveApi, attendanceApi } from '../api/client';
import { Users, Building2, CalendarDays, Clock, CheckCircle, AlertCircle, ArrowUpRight, Sparkles } from 'lucide-react';
import StatusBadge from '../components/StatusBadge';

const DashboardView = ({ setActiveTab }) => {
  const { user, role, isAdmin, isManager } = useAuth();
  const { showToast } = useToast();

  const [stats, setStats] = useState({
    totalEmployees: 0,
    totalDepartments: 0,
    pendingLeaves: 0,
    todayAttendance: 'NOT_CHECKED_IN',
    checkInTime: null,
    checkOutTime: null,
  });
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      const [empRes, deptRes, leaveRes, myAttRes] = await Promise.allSettled([
        employeeApi.getAll({ size: 1 }),
        departmentApi.getAll(),
        leaveApi.getAll({ status: 'PENDING', size: 1 }),
        attendanceApi.getMyAttendance(),
      ]);

      const totalEmployees = empRes.status === 'fulfilled' ? empRes.value.data.totalElements || 0 : 0;
      const totalDepartments = deptRes.status === 'fulfilled' ? deptRes.value.data.length || 0 : 0;
      const pendingLeaves = leaveRes.status === 'fulfilled' ? leaveRes.value.data.totalElements || 0 : 0;

      let todayAttendance = 'NOT_CHECKED_IN';
      let checkInTime = null;
      let checkOutTime = null;

      if (myAttRes.status === 'fulfilled' && Array.isArray(myAttRes.value.data)) {
        const todayStr = new Date().toISOString().split('T')[0];
        const todayRecord = myAttRes.value.data.find((rec) => rec.date === todayStr);
        if (todayRecord) {
          todayAttendance = todayRecord.status;
          checkInTime = todayRecord.checkIn;
          checkOutTime = todayRecord.checkOut;
        }
      }

      setStats({
        totalEmployees,
        totalDepartments,
        pendingLeaves,
        todayAttendance,
        checkInTime,
        checkOutTime,
      });
    } catch (err) {
      console.error('Error fetching stats:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleCheckIn = async () => {
    try {
      setActionLoading(true);
      const res = await attendanceApi.checkIn();
      showToast(`Checked in successfully at ${res.data.checkIn}! Status: ${res.data.status}`, 'success');
      fetchDashboardData();
    } catch (err) {
      showToast(err.response?.data?.message || 'Check-in failed.', 'error');
    } finally {
      setActionLoading(false);
    }
  };

  const handleCheckOut = async () => {
    try {
      setActionLoading(true);
      const res = await attendanceApi.checkOut();
      showToast(`Checked out successfully at ${res.data.checkOut}!`, 'success');
      fetchDashboardData();
    } catch (err) {
      showToast(err.response?.data?.message || 'Check-out failed.', 'error');
    } finally {
      setActionLoading(false);
    }
  };

  return (
    <div className="page-container">
      {/* Welcome Banner */}
      <div style={{
        background: 'linear-gradient(135deg, rgba(99, 102, 241, 0.15) 0%, rgba(168, 85, 247, 0.08) 100%)',
        border: '1px solid rgba(99, 102, 241, 0.25)',
        borderRadius: 'var(--radius-lg)',
        padding: '28px 32px',
        marginBottom: '28px',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        flexWrap: 'wrap',
        gap: '20px'
      }}>
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '6px' }}>
            <span style={{ fontSize: '0.8125rem', fontWeight: 600, color: 'var(--primary)', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
              Welcome back
            </span>
            <Sparkles size={16} color="var(--primary)" />
          </div>
          <h2 style={{ fontSize: '1.65rem', fontWeight: 800, color: 'var(--text-main)', letterSpacing: '-0.02em' }}>
            {user?.email}
          </h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', marginTop: '4px' }}>
            Role clearance: <StatusBadge status={role} /> — All systems operational.
          </p>
        </div>

        {/* Quick Check-In / Check-Out Action Widget */}
        <div style={{
          background: 'var(--bg-card-solid)',
          border: '1px solid var(--border-glass)',
          borderRadius: 'var(--radius-md)',
          padding: '16px 20px',
          display: 'flex',
          alignItems: 'center',
          gap: '16px',
          boxShadow: 'var(--shadow-md)'
        }}>
          <div>
            <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'block' }}>Daily Attendance</span>
            <span style={{ fontSize: '0.9rem', fontWeight: 700, color: 'var(--text-main)' }}>
              {stats.checkInTime ? `In: ${stats.checkInTime}` : 'Not checked in today'}
              {stats.checkOutTime && ` | Out: ${stats.checkOutTime}`}
            </span>
          </div>

          {!stats.checkInTime ? (
            <button
              onClick={handleCheckIn}
              disabled={actionLoading}
              className="btn btn-success btn-sm"
            >
              <Clock size={16} />
              Check In
            </button>
          ) : !stats.checkOutTime ? (
            <button
              onClick={handleCheckOut}
              disabled={actionLoading}
              className="btn btn-danger btn-sm"
            >
              <Clock size={16} />
              Check Out
            </button>
          ) : (
            <span className="badge badge-emerald">
              <CheckCircle size={14} /> Completed
            </span>
          )}
        </div>
      </div>

      {/* Metric Cards Grid */}
      <div className="grid-4" style={{ marginBottom: '28px' }}>
        {/* Total Employees */}
        <div className="card" onClick={() => setActiveTab('employees')} style={{ cursor: 'pointer' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '14px' }}>
            <span style={{ fontSize: '0.85rem', fontWeight: 600, color: 'var(--text-muted)' }}>Personnel Count</span>
            <div style={{ padding: '8px', borderRadius: '8px', background: 'var(--primary-subtle)', color: 'var(--primary)' }}>
              <Users size={20} />
            </div>
          </div>
          <div style={{ fontSize: '2rem', fontWeight: 800, color: 'var(--text-main)', fontFamily: 'var(--font-mono)' }}>
            {loading ? '...' : stats.totalEmployees}
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '4px', marginTop: '6px', fontSize: '0.75rem', color: 'var(--primary)' }}>
            <span>View directory roster</span>
            <ArrowUpRight size={14} />
          </div>
        </div>

        {/* Total Departments */}
        <div className="card" onClick={() => setActiveTab('departments')} style={{ cursor: 'pointer' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '14px' }}>
            <span style={{ fontSize: '0.85rem', fontWeight: 600, color: 'var(--text-muted)' }}>Departments</span>
            <div style={{ padding: '8px', borderRadius: '8px', background: 'var(--cyan-subtle)', color: 'var(--cyan)' }}>
              <Building2 size={20} />
            </div>
          </div>
          <div style={{ fontSize: '2rem', fontWeight: 800, color: 'var(--text-main)', fontFamily: 'var(--font-mono)' }}>
            {loading ? '...' : stats.totalDepartments}
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '4px', marginTop: '6px', fontSize: '0.75rem', color: 'var(--cyan)' }}>
            <span>View departments architecture</span>
            <ArrowUpRight size={14} />
          </div>
        </div>

        {/* Pending Leaves */}
        <div className="card" onClick={() => setActiveTab('leaves')} style={{ cursor: 'pointer' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '14px' }}>
            <span style={{ fontSize: '0.85rem', fontWeight: 600, color: 'var(--text-muted)' }}>Pending Leaves</span>
            <div style={{ padding: '8px', borderRadius: '8px', background: 'var(--amber-subtle)', color: 'var(--amber)' }}>
              <CalendarDays size={20} />
            </div>
          </div>
          <div style={{ fontSize: '2rem', fontWeight: 800, color: 'var(--text-main)', fontFamily: 'var(--font-mono)' }}>
            {loading ? '...' : stats.pendingLeaves}
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '4px', marginTop: '6px', fontSize: '0.75rem', color: 'var(--amber)' }}>
            <span>Review requests</span>
            <ArrowUpRight size={14} />
          </div>
        </div>

        {/* Daily Attendance Status */}
        <div className="card" onClick={() => setActiveTab('attendance')} style={{ cursor: 'pointer' }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '14px' }}>
            <span style={{ fontSize: '0.85rem', fontWeight: 600, color: 'var(--text-muted)' }}>My Status</span>
            <div style={{ padding: '8px', borderRadius: '8px', background: 'var(--emerald-subtle)', color: 'var(--emerald)' }}>
              <Clock size={20} />
            </div>
          </div>
          <div style={{ fontSize: '1.4rem', fontWeight: 800, color: 'var(--text-main)', marginTop: '6px' }}>
            <StatusBadge status={stats.todayAttendance} />
          </div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '4px', marginTop: '12px', fontSize: '0.75rem', color: 'var(--emerald)' }}>
            <span>Attendance history</span>
            <ArrowUpRight size={14} />
          </div>
        </div>
      </div>

      {/* Quick Access Grid */}
      <div className="grid-3">
        <div className="card">
          <h3 style={{ fontSize: '1.1rem', fontWeight: 700, marginBottom: '10px' }}>Leave Applications</h3>
          <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '18px' }}>
            Apply for vacation, sick, or casual leave and track supervisor approval status.
          </p>
          <button className="btn btn-primary btn-sm" onClick={() => setActiveTab('leaves')}>
            Apply for Leave
          </button>
        </div>

        <div className="card">
          <h3 style={{ fontSize: '1.1rem', fontWeight: 700, marginBottom: '10px' }}>Payroll & Compensation</h3>
          <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '18px' }}>
            {isAdmin ? 'Process monthly payroll runs and generate employee pay slips.' : 'Access and review your monthly pay slip breakdown.'}
          </p>
          <button className="btn btn-secondary btn-sm" onClick={() => setActiveTab('salaries')}>
            View Payroll Hub
          </button>
        </div>

        <div className="card">
          <h3 style={{ fontSize: '1.1rem', fontWeight: 700, marginBottom: '10px' }}>Attendance Roster</h3>
          <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: '18px' }}>
            Monitor daily check-in timestamps, late arrivals, and attendance logs.
          </p>
          <button className="btn btn-secondary btn-sm" onClick={() => setActiveTab('attendance')}>
            View Attendance Sheets
          </button>
        </div>
      </div>
    </div>
  );
};

export default DashboardView;
