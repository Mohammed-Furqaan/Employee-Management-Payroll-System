package com.payroll.employeemanagement.dto;

import com.payroll.employeemanagement.entity.LeaveStatus;
import com.payroll.employeemanagement.entity.LeaveType;
import lombok.*;

import java.time.LocalDate;

/**
 * Response DTO returning Leave Request details with flattened employee and approver names.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveResponse {
    private Long leaveId;
    private Long employeeId;
    private String employeeName;
    private LeaveType leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private LeaveStatus status;
    private Long approvedById;
    private String approvedByName;
}
