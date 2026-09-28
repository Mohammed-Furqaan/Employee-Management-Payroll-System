package com.payroll.employeemanagement.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a company department.
 */
@Entity
@Table(name = "department")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id")
    private Long departmentId;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    /**
     * The employee who is the head of this department.
     * We use LAZY fetching here to prevent N+1 query loading issues.
     * Note that optional = true since a department might not have a head assigned yet.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "head_employee_id")
    private Employee headEmployee;
}
