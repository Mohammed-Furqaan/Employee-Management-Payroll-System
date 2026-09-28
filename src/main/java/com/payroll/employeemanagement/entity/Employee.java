package com.payroll.employeemanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Entity representing an employee in the system.
 */
@Entity
@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    /**
     * Password is used for Spring Security authentication.
     * We define it here so we don't need to perform database schema refactoring in Step 6.
     */
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    /**
     * Department to which the employee belongs.
     * Mapped as ManyToOne. Multiple employees can belong to the same department.
     * LAZY fetch used to optimize queries.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    /**
     * Role of the employee for access control (e.g. ADMIN, EMPLOYEE).
     * Mapped as ManyToOne with EAGER fetching for security authority resolution.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;

    /**
     * Self-referencing relationship for managers.
     * An employee can have a manager who is also an employee.
     * This manager field points back to the Employee table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private Employee manager;
}
