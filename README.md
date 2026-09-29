# PayPulse - Employee Management & Payroll System

An enterprise-grade, full-stack Employee Management and Payroll System built with **Spring Boot 3.3 (Java 21)** and **React 19 (Vite)**.

---

## 🚀 Key Features

- **Role-Based Access Control (RBAC)**: Secure authentication and authorization with Spring Security 6 & JWT for `ROLE_ADMIN`, `ROLE_MANAGER`, and `ROLE_EMPLOYEE`. Self-registration defaults strictly to `ROLE_EMPLOYEE`.
- **Employee Directory**: Paginated, filterable employee registry with department assignments and reporting manager hierarchies.
- **Department Management**: Department CRUD with designated department heads and referential integrity protection.
- **Leave Management**: Leave application submission, status tracking, and manager approval/rejection workflows with self-approval prevention.
- **Payroll & Salaries**: Salary computation (net pay calculation: `basic + allowances - deductions`), duplicate payout protection, and monthly slip lookups.
- **Attendance Station**: Daily check-in and check-out tracking with employee attendance history logs.
- **Modern Glassmorphic UI**: Fast, responsive React 19 frontend powered by Vite and Lucide icons.

---

## 🛠️ Technology Stack

### Backend
- **Java 21 (LTS)**
- **Spring Boot 3.3.2** (Spring Data JPA, Spring Security, Spring Web, Validation)
- **JWT (JSON Web Tokens - JJWT 0.12.6)**
- **MySQL 8.0**
- **Flyway Database Migration (V1 to V7)**
- **Lombok**
- **JUnit 5 & Mockito** (Unit & Service Layer Tests)

### Frontend
- **React 19**
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

### 2. Environment Configuration
Copy `.env.example` to `.env` or export `JWT_SECRET` in your local environment:
```bash
# Set a 256-bit Base64-encoded signing key (e.g. for PowerShell)
$env:JWT_SECRET="cE11TGpLdjlYN2ZkV1M0eUZiOWlRd2FNZ1YyY0Jwb1JSdlZ4Z055cz0="
```

### 3. Database Setup
Create the MySQL database (or let Spring Boot create it automatically via JDBC configuration):
```sql
CREATE DATABASE employee_payroll_db;
```
Configure database credentials in `src/main/resources/application.properties` if different from default (`root` / `root`).

### 4. Running the Backend
```bash
mvn spring-boot:run
```
The server will start on `http://localhost:8080` and automatically run Flyway migrations (`V1` to `V7`) and seed default demo accounts.

### 5. Running the Frontend
```bash
cd frontend
npm install
npm run dev
```
The client application will be available at `http://localhost:5173`.

### 6. Running Automated Tests
```bash
mvn test
```

---

## 👥 Demo Credentials

| Role | Email | Password |
| :--- | :--- | :--- |
| **Admin** | `admin@company.com` | `admin123` |
| **Manager** | `manager@company.com` | `manager123` |
| **Employee** | `employee@company.com` | `employee123` |

## Author 
Mohammed Furqaan Annigeri
