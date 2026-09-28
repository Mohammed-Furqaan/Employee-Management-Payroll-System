package com.payroll.employeemanagement.dto;

import lombok.*;

import java.time.LocalDate;

/**
 * Response DTO returning safe Employee details without exposing passwords or circular structures.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeResponse {
    private Long employeeId;
    private String name;
    private String email;
    private String phone;
    private LocalDate hireDate;
    private Long departmentId;
    private String departmentName;
    private Long roleId;
    private String roleName;
    private Long managerId;
    private String managerName;
}
