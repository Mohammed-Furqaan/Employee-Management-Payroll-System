package com.payroll.employeemanagement.dto;

import lombok.*;

import java.math.BigDecimal;

/**
 * Response DTO returning Salary and Payroll details with flattened employee info.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryResponse {
    private Long salaryId;
    private Long employeeId;
    private String employeeName;
    private BigDecimal basicPay;
    private BigDecimal allowances;
    private BigDecimal deductions;
    private BigDecimal netPay;
    private Integer month;
    private Integer year;
}
