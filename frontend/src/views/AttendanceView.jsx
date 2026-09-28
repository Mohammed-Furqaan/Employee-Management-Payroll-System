import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../components/Toast';
import { attendanceApi } from '../api/client';
import { Clock, Calendar, CheckCircle, AlertTriangle, Search } from 'lucide-react';
import StatusBadge from '../components/StatusBadge';

const AttendanceView = () => {
  const { user, isAdmin, isManager } = useAuth();
  const { showToast } = useToast();

  const [myAttendance, setMyAttendance] = useState([]);
  const [dailyAttendance, setDailyAttendance] = useState([]);
  const [selectedDate, setSelectedDate] = useState(new Date().toISOString().split('T')[0]);

  const [loading, setLoading] = useState(false);
  const [dailyLoading, setDailyLoading] = useState(false);
  const [actionLoading, setActionLoading] = useState(false);

  const fetchMyAttendance = async () => {
    try {
      setLoading(true);
      const res = await attendanceApi.getMyAttendance();
      setMyAttendance(res.data || []);
    } catch (err) {
      showToast('Failed to load your attendance history.', 'error');
    } finally {
      setLoading(false);
    }
  };

  const fetchDailyAttendance = async (dateStr) => {
    try {
      setDailyLoading(true);
      const res = await attendanceApi.getByDate(dateStr);
      setDailyAttendance(res.data || []);
    } catch (err) {
      showToast('Failed to load daily attendance sheet.', 'error');
    } finally {
      setDailyLoading(false);
    }
  };

  useEffect(() => {
    fetchMyAttendance();
    if (isAdmin || isManager) {
      fetchDailyAttendance(selectedDate);
    }
  }, [isAdmin, isManager, selectedDate]);

  const todayStr = new Date().toISOString().split('T')[0];
  const todayRecord = myAttendance.find((rec) => rec.date === todayStr);

  const handleCheckIn = async () => {
    try {
      setActionLoading(true);
      const res = await attendanceApi.checkIn();
      showToast(`Checked in at ${res.data.checkIn}! Status: ${res.data.status}`, 'success');
      fetchMyAttendance();
      if (isAdmin || isManager) fetchDailyAttendance(selectedDate);
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
      showToast(`Checked out at ${res.data.checkOut}!`, 'success');
      fetchMyAttendance();
      if (isAdmin || isManager) fetchDailyAttendance(selectedDate);
    } catch (err) {
      showToast(err.response?.data?.message || 'Check-out failed.', 'error');
    } finally {
      setActionLoading(false);
    }
  };

  return (
    <div className="page-container">
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px', flexWrap: 'wrap', gap: '16px' }}>
        <div>
          <h2 style={{ fontSize: '1.5rem', fontWeight: 800 }}>Attendance Station</h2>
          <p style={{ fontSize: '0.875rem', color: 'var(--text-muted)' }}>
            Daily check-ins, late arrival thresholds (09:30 AM), and presence tracking.
          </p>
        </div>
      </div>

      {/* Personal Check-In / Check-Out Widget */}
      <div className="card" style={{ marginBottom: '28px', background: 'linear-gradient(135deg, rgba(17, 24, 39, 0.9) 0%, rgba(30, 41, 59, 0.7) 100%)' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '20px' }}>
          <div>
            <span style={{ fontSize: '0.8125rem', fontWeight: 600, color: 'var(--primary)', textTransform: 'uppercase' }}>
              My Check-In Console
            </span>
            <h3 style={{ fontSize: '1.25rem', fontWeight: 700, color: 'var(--text-main)', marginTop: '4px' }}>
              Today: {todayStr}
            </h3>
            <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginTop: '4px' }}>
              Status: {todayRecord ? <StatusBadge status={todayRecord.status} /> : <span style={{ color: 'var(--text-subtle)' }}>Not Checked In</span>}
            </p>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '14px' }}>
            {!todayRecord ? (
              <button
                className="btn btn-success"
                onClick={handleCheckIn}
                disabled={actionLoading}
                style={{ padding: '12px 24px' }}
              >
                <Clock size={18} />
                Clock In Now
              </button>
            ) : !todayRecord.checkOut ? (
              <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
                <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)', textAlign: 'right' }}>
                  <span>Clocked in at: </span>
                  <strong style={{ color: 'var(--text-main)', fontFamily: 'var(--font-mono)' }}>{todayRecord.checkIn}</strong>
                </div>
                <button
                  className="btn btn-danger"
                  onClick={handleCheckOut}
                  disabled={actionLoading}
                  style={{ padding: '12px 24px' }}
                >
                  <Clock size={18} />
                  Clock Out
                </button>
              </div>
            ) : (
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                <span className="badge badge-emerald" style={{ padding: '8px 16px', fontSize: '0.85rem' }}>
                  <CheckCircle size={16} /> Completed for Today (In: {todayRecord.checkIn} | Out: {todayRecord.checkOut})
                </span>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Admin / Manager Daily Sheet Overview */}
      {(isAdmin || isManager) && (
        <div style={{ marginBottom: '32px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px', flexWrap: 'wrap', gap: '12px' }}>
            <h3 style={{ fontSize: '1.2rem', fontWeight: 700 }}>Company Daily Attendance Sheet</h3>
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
              <label className="form-label" style={{ margin: 0, fontSize: '0.8125rem' }}>Select Date:</label>
              <input
                type="date"
                className="form-input"
                value={selectedDate}
                onChange={(e) => setSelectedDate(e.target.value)}
                style={{ width: 'auto', padding: '6px 12px' }}
              />
            </div>
          </div>

          <div className="table-container">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Employee Name</th>
                  <th>Date</th>
                  <th>Check In</th>
                  <th>Check Out</th>
                  <th>Daily Status</th>
                </tr>
              </thead>
              <tbody>
                {dailyLoading ? (
                  <tr>
                    <td colSpan={5} style={{ textAlign: 'center', padding: '24px', color: 'var(--text-muted)' }}>
                      Loading daily records...
                    </td>
                  </tr>
                ) : dailyAttendance.length === 0 ? (
                  <tr>
                    <td colSpan={5} style={{ textAlign: 'center', padding: '24px', color: 'var(--text-muted)' }}>
                      No attendance records logged for {selectedDate}.
                    </td>
                  </tr>
                ) : (
                  dailyAttendance.map((att) => (
                    <tr key={att.attendanceId}>
                      <td style={{ fontWeight: 600 }}>{att.employeeName || 'Staff Member'}</td>
                      <td style={{ fontFamily: 'var(--font-mono)' }}>{att.date}</td>
                      <td style={{ fontFamily: 'var(--font-mono)' }}>{att.checkIn || '-'}</td>
                      <td style={{ fontFamily: 'var(--font-mono)' }}>{att.checkOut || 'Active Shift'}</td>
                      <td><StatusBadge status={att.status} /></td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Personal Attendance History */}
      <div>
        <h3 style={{ fontSize: '1.2rem', fontWeight: 700, marginBottom: '16px' }}>My Attendance History</h3>
        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>Date</th>
                <th>Check In Time</th>
                <th>Check Out Time</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan={4} style={{ textAlign: 'center', padding: '24px', color: 'var(--text-muted)' }}>
                    Loading your records...
                  </td>
                </tr>
              ) : myAttendance.length === 0 ? (
                <tr>
                  <td colSpan={4} style={{ textAlign: 'center', padding: '24px', color: 'var(--text-muted)' }}>
                    No personal attendance logs found.
                  </td>
                </tr>
              ) : (
                myAttendance.map((rec) => (
                  <tr key={rec.attendanceId}>
                    <td style={{ fontFamily: 'var(--font-mono)', fontWeight: 600 }}>{rec.date}</td>
                    <td style={{ fontFamily: 'var(--font-mono)' }}>{rec.checkIn || '-'}</td>
                    <td style={{ fontFamily: 'var(--font-mono)' }}>{rec.checkOut || 'Incomplete'}</td>
                    <td><StatusBadge status={rec.status} /></td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default AttendanceView;
