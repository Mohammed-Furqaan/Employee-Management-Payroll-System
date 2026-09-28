package com.payroll.employeemanagement.dto;

import lombok.*;

/**
 * Response DTO returning Department details with flattened head employee info.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentResponse {
    private Long departmentId;
    private String name;
    private Long headEmployeeId;
    private String headEmployeeName;
}
