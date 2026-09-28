import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../components/Toast';
import { ShieldCheck, Mail, Lock, User, Phone, Calendar, ArrowRight, Sparkles } from 'lucide-react';

const AuthView = () => {
  const { login, register, loading } = useAuth();
  const { showToast } = useToast();

  const [isLogin, setIsLogin] = useState(true);

  // Form State
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    hireDate: new Date().toISOString().split('T')[0],
    password: '',
    roleName: 'ROLE_EMPLOYEE',
  });

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (isLogin) {
      if (!formData.email || !formData.password) {
        showToast('Please enter your email and password.', 'warning');
        return;
      }
      const res = await login(formData.email, formData.password);
      if (res.success) {
        showToast('Login successful! Welcome back.', 'success');
      } else {
        showToast(res.error, 'error');
      }
    } else {
      if (!formData.name || !formData.email || !formData.password) {
        showToast('Please fill out all required fields.', 'warning');
        return;
      }
      const res = await register(formData);
      if (res.success) {
        showToast('Account registered successfully!', 'success');
      } else {
        showToast(res.error, 'error');
      }
    }
  };

  // Demo account filler
  const fillDemo = (email, password) => {
    setFormData((prev) => ({ ...prev, email, password }));
    setIsLogin(true);
    showToast(`Filled credentials for ${email}`, 'info');
  };

  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '24px',
      background: 'radial-gradient(circle at 15% 50%, rgba(99, 102, 241, 0.12) 0%, transparent 50%), radial-gradient(circle at 85% 30%, rgba(168, 85, 247, 0.12) 0%, transparent 50%), var(--bg-app)'
    }}>
      <div style={{
        width: '100%',
        maxWidth: '460px',
        background: 'var(--bg-card)',
        backdropFilter: 'blur(20px)',
        WebkitBackdropFilter: 'blur(20px)',
        border: '1px solid var(--border-glass)',
        borderRadius: 'var(--radius-lg)',
        padding: '40px 36px',
        boxShadow: 'var(--shadow-lg)'
      }}>
        {/* Brand Icon & Heading */}
        <div style={{ textAlign: 'center', marginBottom: '28px' }}>
          <div style={{
            width: '52px',
            height: '52px',
            borderRadius: '14px',
            background: 'linear-gradient(135deg, var(--primary) 0%, #4338ca 100%)',
            display: 'inline-flex',
            alignItems: 'center',
            justifyContent: 'center',
            boxShadow: '0 8px 20px var(--primary-glow)',
            marginBottom: '16px'
          }}>
            <ShieldCheck size={30} color="#ffffff" />
          </div>
          <h1 style={{ fontSize: '1.75rem', fontWeight: 800, color: 'var(--text-main)', letterSpacing: '-0.02em' }}>
            PayPulse
          </h1>
          <p style={{ fontSize: '0.875rem', color: 'var(--text-muted)', marginTop: '4px' }}>
            {isLogin ? 'Sign in to access your dashboard' : 'Register a new enterprise account'}
          </p>
        </div>

        {/* Tab Switcher */}
        <div style={{
          display: 'flex',
          background: 'var(--bg-input)',
          borderRadius: 'var(--radius-sm)',
          padding: '4px',
          marginBottom: '24px',
          border: '1px solid var(--border-glass)'
        }}>
          <button
            type="button"
            onClick={() => setIsLogin(true)}
            style={{
              flex: 1,
              padding: '8px',
              border: 'none',
              borderRadius: '6px',
              fontSize: '0.85rem',
              fontWeight: 600,
              cursor: 'pointer',
              background: isLogin ? 'var(--primary)' : 'transparent',
              color: isLogin ? '#fff' : 'var(--text-muted)',
              transition: 'var(--transition)'
            }}
          >
            Sign In
          </button>
          <button
            type="button"
            onClick={() => setIsLogin(false)}
            style={{
              flex: 1,
              padding: '8px',
              border: 'none',
              borderRadius: '6px',
              fontSize: '0.85rem',
              fontWeight: 600,
              cursor: 'pointer',
              background: !isLogin ? 'var(--primary)' : 'transparent',
              color: !isLogin ? '#fff' : 'var(--text-muted)',
              transition: 'var(--transition)'
            }}
          >
            Register
          </button>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit}>
          {!isLogin && (
            <>
              <div className="form-group">
                <label className="form-label">Full Name</label>
                <div style={{ position: 'relative' }}>
                  <input
                    type="text"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    placeholder="Jane Doe"
                    className="form-input"
                    required
                  />
                </div>
              </div>

              <div className="grid-2">
                <div className="form-group">
                  <label className="form-label">Phone Number</label>
                  <input
                    type="tel"
                    name="phone"
                    value={formData.phone}
                    onChange={handleChange}
                    placeholder="+1 (555) 000-0000"
                    className="form-input"
                  />
                </div>
                <div className="form-group">
                  <label className="form-label">Hire Date</label>
                  <input
                    type="date"
                    name="hireDate"
                    value={formData.hireDate}
                    onChange={handleChange}
                    className="form-input"
                    required
                  />
                </div>
              </div>

              <div className="form-group">
                <label className="form-label">Assign Role</label>
                <select
                  name="roleName"
                  value={formData.roleName}
                  onChange={handleChange}
                  className="form-select"
                >
                  <option value="ROLE_EMPLOYEE">Employee (ROLE_EMPLOYEE)</option>
                  <option value="ROLE_MANAGER">Manager (ROLE_MANAGER)</option>
                  <option value="ROLE_ADMIN">Administrator (ROLE_ADMIN)</option>
                </select>
              </div>
            </>
          )}

          <div className="form-group">
            <label className="form-label">Work Email</label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              placeholder="name@company.com"
              className="form-input"
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Password</label>
            <input
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              placeholder="••••••••"
              className="form-input"
              required
            />
          </div>

          <button
            type="submit"
            className="btn btn-primary"
            style={{ width: '100%', marginTop: '8px', padding: '12px' }}
            disabled={loading}
          >
            {loading ? 'Authenticating...' : isLogin ? 'Sign In to Account' : 'Create Account'}
            {!loading && <ArrowRight size={16} />}
          </button>
        </form>

        {/* Demo Quick Fill Buttons */}
        {isLogin && (
          <div style={{ marginTop: '24px', paddingTop: '20px', borderTop: '1px solid var(--border-glass)' }}>
            <span style={{ fontSize: '0.75rem', color: 'var(--text-subtle)', textTransform: 'uppercase', letterSpacing: '0.05em', fontWeight: 600, display: 'block', marginBottom: '10px' }}>
              Quick Demo Fill:
            </span>
            <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
              <button
                type="button"
                className="btn btn-secondary btn-sm"
                onClick={() => fillDemo('admin@company.com', 'admin123')}
              >
                Admin
              </button>
              <button
                type="button"
                className="btn btn-secondary btn-sm"
                onClick={() => fillDemo('manager@company.com', 'manager123')}
              >
                Manager
              </button>
              <button
                type="button"
                className="btn btn-secondary btn-sm"
                onClick={() => fillDemo('employee@company.com', 'employee123')}
              >
                Employee
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default AuthView;
