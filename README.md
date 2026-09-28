# PayPulse - Employee Management & Payroll System

An enterprise-grade, full-stack Employee Management and Payroll System built with **Spring Boot 3.3 (Java 21)** and **React 18 (Vite)**.

---

## 🚀 Key Features

- **Role-Based Access Control (RBAC)**: Secure authentication and authorization with Spring Security 6 & JWT for `ROLE_ADMIN`, `ROLE_MANAGER`, and `ROLE_EMPLOYEE`.
- **Employee Directory**: Paginated, filterable employee registry with department assignments and reporting manager hierarchies.
- **Department Management**: Department CRUD with designated department heads.
- **Leave Management**: Leave application submission, tracking, and approval/rejection workflows.
- **Payroll & Salaries**: Automated salary generation (basic pay, allowances, deductions, net pay) and monthly slip lookups.
- **Attendance Station**: Daily check-in, check-out tracking, and employee attendance logs.
- **Modern Glassmorphic UI**: Fast, responsive React frontend powered by Vite and Lucide icons.

---

## 🛠️ Technology Stack

### Backend
- **Java 21**
- **Spring Boot 3.3.2** (Spring Data JPA, Spring Security, Spring Web, Validation)
- **JWT (JSON Web Tokens - jjwt 0.12.6)**
- **MySQL 8.0**
- **Flyway Database Migration**
- **Lombok & MapStruct**

### Frontend
- **React 18**
- **Vite**
- **Axios**
- **Lucide React** (Modern Icons)
- **Vanilla CSS** (Glassmorphism & CSS Variables)

---

## 🏁 Getting Started

### 1. Prerequisites
- **Java 21 JDK** installed
- **Maven 3.9+** installed
- **Node.js 18+** & **npm** installed
- **MySQL Server** running on `localhost:3306`

### 2. Database Setup
Create MySQL database (or let Spring Boot create it automatically via JDBC configuration):
```sql
CREATE DATABASE employee_payroll_db;
```
Configure credentials in `src/main/resources/application.properties` if different from default (`root` / `root`).

### 3. Running the Backend
```bash
mvn spring-boot:run
```
The server will start on `http://localhost:8080` and automatically run Flyway migrations and seed demo data.

### 4. Running the Frontend
```bash
cd frontend
npm install
npm run dev
```
The client app will be available at `http://localhost:5173`.

---

## 👥 Demo Credentials

| Role | Email | Password |
| :--- | :--- | :--- |
| **Admin** | `admin@company.com` | `admin123` |
| **Manager** | `manager@company.com` | `manager123` |
| **Employee** | `employee@company.com` | `employee123` |
