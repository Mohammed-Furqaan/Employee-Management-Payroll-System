import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to attach JWT Bearer token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor to handle session expiration
api.interceptors.response.use(
  (response) => response,
  (error) => {
    // If receiving 401 Unauthorized or 403 from protected endpoints (stale token), clear session
    if (error.response && error.response.status === 401 && !error.config?.url?.includes('/auth/login')) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      window.dispatchEvent(new Event('auth-logout'));
    }
    return Promise.reject(error);
  }
);

// Auth endpoints
export const authApi = {
  login: (credentials) => api.post('/auth/login', credentials),
  register: (userData) => api.post('/auth/register', userData),
};

// Employee endpoints
export const employeeApi = {
  getAll: (params) => api.get('/employees', { params }),
  getById: (id) => api.get(`/employees/${id}`),
  create: (data) => api.post('/employees', data),
  update: (id, data) => api.put(`/employees/${id}`, data),
  delete: (id) => api.delete(`/employees/${id}`),
};

// Department endpoints
export const departmentApi = {
  getAll: () => api.get('/departments'),
  getById: (id) => api.get(`/departments/${id}`),
  create: (data) => api.post('/departments', data),
  update: (id, data) => api.put(`/departments/${id}`, data),
  delete: (id) => api.delete(`/departments/${id}`),
};

// Role endpoints
export const roleApi = {
  getAll: () => api.get('/roles'),
};

// Leave Request endpoints
export const leaveApi = {
  apply: (data) => api.post('/leaves', data),
  getAll: (params) => api.get('/leaves', { params }),
  getById: (id) => api.get(`/leaves/${id}`),
  getByEmployee: (employeeId, params) => api.get(`/leaves/employee/${employeeId}`, { params }),
  updateStatus: (id, status) => api.put(`/leaves/${id}/status?status=${status}`),
};

// Salary & Payroll endpoints
export const salaryApi = {
  generate: (data) => api.post('/salaries', data),
  getAll: () => api.get('/salaries'),
  getById: (id) => api.get(`/salaries/${id}`),
  getByEmployee: (employeeId) => api.get(`/salaries/employee/${employeeId}`),
};

// Attendance endpoints
export const attendanceApi = {
  checkIn: () => api.post('/attendance/check-in'),
  checkOut: () => api.post('/attendance/check-out'),
  getMyAttendance: () => api.get('/attendance/my-attendance'),
  getByEmployee: (employeeId) => api.get(`/attendance/employee/${employeeId}`),
  getByDate: (dateStr) => api.get(`/attendance/date/${dateStr}`),
};

export default api;
