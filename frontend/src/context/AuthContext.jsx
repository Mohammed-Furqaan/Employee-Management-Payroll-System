import React, { createContext, useContext, useState, useEffect } from 'react';
import { authApi } from '../api/client';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    const savedUser = localStorage.getItem('user');
    return savedUser ? JSON.parse(savedUser) : null;
  });
  const [token, setToken] = useState(() => localStorage.getItem('token') || null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const handleForceLogout = () => {
      setUser(null);
      setToken(null);
    };

    window.addEventListener('auth-logout', handleForceLogout);
    return () => window.removeEventListener('auth-logout', handleForceLogout);
  }, []);

  const extractErrorMessage = (error, defaultMsg) => {
    const resData = error.response?.data;
    if (resData?.details) {
      if (typeof resData.details === 'object' && !Array.isArray(resData.details)) {
        return Object.values(resData.details).join(', ');
      }
      if (typeof resData.details === 'string') {
        return `${resData.message || defaultMsg}: ${resData.details}`;
      }
    }
    return resData?.message || defaultMsg;
  };

  const login = async (email, password) => {
    setLoading(true);
    try {
      const response = await authApi.login({ email, password });
      const { token: jwtToken, email: userEmail, role } = response.data;

      const userData = { email: userEmail, role };
      setToken(jwtToken);
      setUser(userData);

      localStorage.setItem('token', jwtToken);
      localStorage.setItem('user', JSON.stringify(userData));

      return { success: true };
    } catch (error) {
      const message = extractErrorMessage(error, 'Login failed. Please verify credentials.');
      return { success: false, error: message };
    } finally {
      setLoading(false);
    }
  };

  const register = async (userData) => {
    setLoading(true);
    try {
      const response = await authApi.register(userData);
      const { token: jwtToken, email: userEmail, role } = response.data;

      const userObj = { email: userEmail, role };
      setToken(jwtToken);
      setUser(userObj);

      localStorage.setItem('token', jwtToken);
      localStorage.setItem('user', JSON.stringify(userObj));

      return { success: true };
    } catch (error) {
      const message = extractErrorMessage(error, 'Registration failed. Please check form inputs.');
      return { success: false, error: message };
    } finally {
      setLoading(false);
    }
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setToken(null);
    setUser(null);
  };

  const role = user?.role || '';
  const isAdmin = role === 'ROLE_ADMIN';
  const isManager = role === 'ROLE_MANAGER';
  const isEmployee = role === 'ROLE_EMPLOYEE';
  const isAuthenticated = !!token && !!user;

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        loading,
        login,
        register,
        logout,
        isAuthenticated,
        isAdmin,
        isManager,
        isEmployee,
        role,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
