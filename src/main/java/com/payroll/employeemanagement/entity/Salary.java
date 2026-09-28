package com.payroll.employeemanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Entity representing monthly salary details for an employee.
 */
@Entity
@Table(name = "salary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "salary_id")
    private Long salaryId;

    /**
     * The employee who receives this salary.
     * Mapped as ManyToOne, fetching lazily.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "basic_pay", nullable = false, precision = 12, scale = 2)
    private BigDecimal basicPay;

    @Column(name = "allowances", nullable = false, precision = 12, scale = 2)
    private BigDecimal allowances;

    @Column(name = "deductions", nullable = false, precision = 12, scale = 2)
    private BigDecimal deductions;

    @Column(name = "net_pay", nullable = false, precision = 12, scale = 2)
    private BigDecimal netPay;

    @Column(name = "month", nullable = false)
    private Integer month; // 1 = Jan, 12 = Dec

    @Column(name = "year", nullable = false)
    private Integer year;
}
