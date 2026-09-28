package com.payroll.employeemanagement.config;

import com.payroll.employeemanagement.entity.*;
import com.payroll.employeemanagement.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Data initializer component that seeds essential roles, departments,
 * and demo accounts on application startup if the database is empty.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final SalaryRepository salaryRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Checking and seeding database initial data...");

        // 1. Seed Roles
        Role adminRole = roleRepository.findByRoleName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(Role.builder().roleName("ROLE_ADMIN").build()));

        Role managerRole = roleRepository.findByRoleName("ROLE_MANAGER")
                .orElseGet(() -> roleRepository.save(Role.builder().roleName("ROLE_MANAGER").build()));

        Role employeeRole = roleRepository.findByRoleName("ROLE_EMPLOYEE")
                .orElseGet(() -> roleRepository.save(Role.builder().roleName("ROLE_EMPLOYEE").build()));

        // 2. Seed Departments
        Department engineeringDept = departmentRepository.findByName("Engineering")
                .orElseGet(() -> departmentRepository.save(Department.builder().name("Engineering").build()));

        Department hrDept = departmentRepository.findByName("Human Resources")
                .orElseGet(() -> departmentRepository.save(Department.builder().name("Human Resources").build()));

        Department financeDept = departmentRepository.findByName("Finance")
                .orElseGet(() -> departmentRepository.save(Department.builder().name("Finance").build()));

        departmentRepository.findByName("Operations")
                .orElseGet(() -> departmentRepository.save(Department.builder().name("Operations").build()));

        // 3. Seed Demo Users
        Employee adminUser = employeeRepository.findByEmail("admin@company.com")
                .orElseGet(() -> employeeRepository.save(Employee.builder()
                        .name("Alex Rivera (Admin)")
                        .email("admin@company.com")
                        .phone("+1 555-0199")
                        .hireDate(LocalDate.of(2023, 1, 15))
                        .password(passwordEncoder.encode("admin123"))
                        .role(adminRole)
                        .department(engineeringDept)
                        .build()));

        Employee managerUser = employeeRepository.findByEmail("manager@company.com")
                .orElseGet(() -> employeeRepository.save(Employee.builder()
                        .name("Morgan Vance (Manager)")
                        .email("manager@company.com")
                        .phone("+1 555-0144")
                        .hireDate(LocalDate.of(2023, 6, 1))
                        .password(passwordEncoder.encode("manager123"))
                        .role(managerRole)
                        .department(engineeringDept)
                        .manager(adminUser)
                        .build()));

        Employee employeeUser = employeeRepository.findByEmail("employee@company.com")
                .orElseGet(() -> employeeRepository.save(Employee.builder()
                        .name("Evan Harper (Employee)")
                        .email("employee@company.com")
                        .phone("+1 555-0188")
                        .hireDate(LocalDate.of(2024, 2, 10))
                        .password(passwordEncoder.encode("employee123"))
                        .role(employeeRole)
                        .department(engineeringDept)
                        .manager(managerUser)
                        .build()));

        // 4. Seed Demo Salaries if not present
        if (salaryRepository.findByEmployeeEmployeeId(employeeUser.getEmployeeId()).isEmpty()) {
            salaryRepository.save(Salary.builder()
                    .employee(employeeUser)
                    .basicPay(new BigDecimal("6500.00"))
                    .allowances(new BigDecimal("800.00"))
                    .deductions(new BigDecimal("450.00"))
                    .netPay(new BigDecimal("6850.00"))
                    .month(LocalDate.now().getMonthValue())
                    .year(LocalDate.now().getYear())
                    .build());
        }

        // 5. Seed Demo Attendance if not present
        if (attendanceRepository.findByEmployeeEmployeeIdAndDate(employeeUser.getEmployeeId(), LocalDate.now()).isEmpty()) {
            attendanceRepository.save(Attendance.builder()
                    .employee(employeeUser)
                    .date(LocalDate.now())
                    .checkIn(LocalTime.of(9, 0))
                    .checkOut(LocalTime.of(17, 30))
                    .status(AttendanceStatus.PRESENT)
                    .build());
        }

        // 6. Seed Demo Leave Request if not present
        if (leaveRequestRepository.findByEmployeeEmployeeId(employeeUser.getEmployeeId()).isEmpty()) {
            leaveRequestRepository.save(LeaveRequest.builder()
                    .employee(employeeUser)
                    .leaveType(LeaveType.CASUAL)
                    .startDate(LocalDate.now().plusDays(5))
                    .endDate(LocalDate.now().plusDays(7))
                    .status(LeaveStatus.PENDING)
                    .build());
        }

        log.info("Database initial data check complete. Demo accounts ready (admin@company.com, manager@company.com, employee@company.com).");
    }
}
