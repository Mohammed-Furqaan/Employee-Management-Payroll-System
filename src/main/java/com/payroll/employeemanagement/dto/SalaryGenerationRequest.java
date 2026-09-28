package com.payroll.employeemanagement.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO representing a request to generate a monthly salary slip.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryGenerationRequest {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Basic pay is required")
    @Min(value = 0, message = "Basic pay must be greater than or equal to 0")
    private BigDecimal basicPay;

    @NotNull(message = "Allowances component is required")
    @Min(value = 0, message = "Allowances must be greater than or equal to 0")
    private BigDecimal allowances;

    @NotNull(message = "Deductions component is required")
    @Min(value = 0, message = "Deductions must be greater than or equal to 0")
    private BigDecimal deductions;

    @NotNull(message = "Month is required")
    @Min(value = 1, message = "Month must be between 1 and 12")
    @Max(value = 12, message = "Month must be between 1 and 12")
    private Integer month;

    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be valid")
    private Integer year;
}
