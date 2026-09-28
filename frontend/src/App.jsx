import React, { useState } from 'react';
import { AuthProvider, useAuth } from './context/AuthContext';
import { ToastProvider } from './components/Toast';
import Sidebar from './components/Sidebar';
import Navbar from './components/Navbar';
import AuthView from './views/AuthView';
import DashboardView from './views/DashboardView';
import EmployeesView from './views/EmployeesView';
import DepartmentsView from './views/DepartmentsView';
import LeavesView from './views/LeavesView';
import SalariesView from './views/SalariesView';
import AttendanceView from './views/AttendanceView';

const MainLayout = () => {
  const { isAuthenticated } = useAuth();
  const [activeTab, setActiveTab] = useState('dashboard');

  if (!isAuthenticated) {
    return <AuthView />;
  }

  const renderActiveView = () => {
    switch (activeTab) {
      case 'dashboard':
        return <DashboardView setActiveTab={setActiveTab} />;
      case 'employees':
        return <EmployeesView />;
      case 'departments':
        return <DepartmentsView />;
      case 'leaves':
        return <LeavesView />;
      case 'salaries':
        return <SalariesView />;
      case 'attendance':
        return <AttendanceView />;
      default:
        return <DashboardView setActiveTab={setActiveTab} />;
    }
  };

  return (
    <div className="app-layout">
      <Sidebar activeTab={activeTab} setActiveTab={setActiveTab} />
      <div className="main-content">
        <Navbar activeTab={activeTab} />
        {renderActiveView()}
      </div>
    </div>
  );
};

function App() {
  return (
    <AuthProvider>
      <ToastProvider>
        <MainLayout />
      </ToastProvider>
    </AuthProvider>
  );
}

export default App;
