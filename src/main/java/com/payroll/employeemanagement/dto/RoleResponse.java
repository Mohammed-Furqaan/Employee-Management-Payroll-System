package com.payroll.employeemanagement.dto;

import lombok.*;

/**
 * Response DTO returning Role details.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponse {
    private Long roleId;
    private String roleName;
}
