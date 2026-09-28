import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { Clock as ClockIcon, Activity, Sparkles } from 'lucide-react';

const Navbar = ({ activeTab }) => {
  const { user, role } = useAuth();
  const [time, setTime] = useState(new Date().toLocaleTimeString());

  useEffect(() => {
    const timer = setInterval(() => {
      setTime(new Date().toLocaleTimeString());
    }, 1000);
    return () => clearInterval(timer);
  }, []);

  const getPageTitle = (tab) => {
    switch (tab) {
      case 'dashboard': return 'Operational Dashboard';
      case 'employees': return 'Employee Directory & Roster';
      case 'departments': return 'Department Architecture';
      case 'leaves': return 'Leave Requests & Approvals';
      case 'salaries': return 'Salary & Payroll Processing';
      case 'attendance': return 'Daily Attendance & Check-Ins';
      default: return 'PayPulse HRMS';
    }
  };

  const getInitials = (email) => {
    if (!email) return 'U';
    return email.substring(0, 2).toUpperCase();
  };

  return (
    <header className="top-navbar">
      <div>
        <h1 style={{ fontSize: '1.25rem', fontWeight: 700, color: 'var(--text-main)' }}>
          {getPageTitle(activeTab)}
        </h1>
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
        {/* Backend status indicator */}
        <div style={{
          display: 'flex',
          alignItems: 'center',
          gap: '6px',
          padding: '6px 12px',
          borderRadius: 'var(--radius-full)',
          background: 'rgba(16, 185, 129, 0.08)',
          border: '1px solid rgba(16, 185, 129, 0.2)',
          fontSize: '0.75rem',
          color: 'var(--emerald)'
        }}>
          <span style={{ width: '7px', height: '7px', borderRadius: '50%', backgroundColor: 'var(--emerald)', display: 'inline-block' }} />
          <span>API Connected (Port 8080)</span>
        </div>

        {/* Live Clock */}
        <div style={{
          display: 'flex',
          alignItems: 'center',
          gap: '8px',
          color: 'var(--text-muted)',
          fontSize: '0.85rem',
          fontFamily: 'var(--font-mono)'
        }}>
          <ClockIcon size={16} />
          <span>{time}</span>
        </div>

        {/* User avatar badge */}
        <div style={{
          display: 'flex',
          alignItems: 'center',
          gap: '10px'
        }}>
          <div style={{
            width: '36px',
            height: '36px',
            borderRadius: '50%',
            background: 'linear-gradient(135deg, #6366f1 0%, #a855f7 100%)',
            color: '#fff',
            fontWeight: 700,
            fontSize: '0.875rem',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            boxShadow: '0 2px 8px var(--primary-glow)'
          }}>
            {getInitials(user?.email)}
          </div>
        </div>
      </div>
    </header>
  );
};

export default Navbar;
