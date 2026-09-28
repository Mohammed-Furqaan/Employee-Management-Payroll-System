package com.payroll.employeemanagement.dto;

import com.payroll.employeemanagement.entity.AttendanceStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Response DTO returning daily Attendance records with employee details.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceResponse {
    private Long attendanceId;
    private Long employeeId;
    private String employeeName;
    private LocalDate date;
    private LocalTime checkIn;
    private LocalTime checkOut;
    private AttendanceStatus status;
}
