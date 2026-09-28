package com.payroll.employeemanagement.controller;

import com.payroll.employeemanagement.dto.AttendanceResponse;
import com.payroll.employeemanagement.entity.Attendance;
import com.payroll.employeemanagement.mapper.DtoMapper;
import com.payroll.employeemanagement.security.CustomUserDetails;
import com.payroll.employeemanagement.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller exposing Attendance Tracking API endpoints using DTOs.
 * Context-binds check-in/out logs to the authenticated user.
 */
@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final DtoMapper dtoMapper;

    /**
     * POST /api/attendance/check-in : Logs a check-in for the current authenticated employee.
     */
    @PostMapping("/check-in")
    public ResponseEntity<AttendanceResponse> checkIn(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long employeeId = userDetails.getEmployee().getEmployeeId();
        Attendance record = attendanceService.checkIn(employeeId);
        return new ResponseEntity<>(dtoMapper.toAttendanceResponse(record), HttpStatus.CREATED);
    }

    /**
     * POST /api/attendance/check-out : Logs a check-out for the current authenticated employee.
     */
    @PostMapping("/check-out")
    public ResponseEntity<AttendanceResponse> checkOut(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long employeeId = userDetails.getEmployee().getEmployeeId();
        Attendance record = attendanceService.checkOut(employeeId);
        return ResponseEntity.ok(dtoMapper.toAttendanceResponse(record));
    }

    /**
     * GET /api/attendance/my-attendance : Retrieves history logs for the current authenticated employee.
     */
    @GetMapping("/my-attendance")
    public ResponseEntity<List<AttendanceResponse>> getMyAttendance(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long employeeId = userDetails.getEmployee().getEmployeeId();
        List<AttendanceResponse> records = attendanceService.getAttendanceHistoryByEmployeeId(employeeId).stream()
                .map(dtoMapper::toAttendanceResponse)
                .toList();
        return ResponseEntity.ok(records);
    }

    /**
     * GET /api/attendance/employee/{employeeId} : Retrieves history logs for a specific employee.
     * Enforces that standard employees can only view their own log history.
     */
    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or #employeeId == principal.employee.employeeId")
    public ResponseEntity<List<AttendanceResponse>> getAttendanceByEmployeeId(@PathVariable Long employeeId) {
        List<AttendanceResponse> records = attendanceService.getAttendanceHistoryByEmployeeId(employeeId).stream()
                .map(dtoMapper::toAttendanceResponse)
                .toList();
        return ResponseEntity.ok(records);
    }

    /**
     * GET /api/attendance/date/{date} : Retrieves all attendance records for a specific date.
     * Restricted to ADMIN and MANAGER roles.
     * Expects date formatted as YYYY-MM-DD (e.g. /api/attendance/date/2026-08-29)
     */
    @GetMapping("/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<AttendanceResponse>> getAllAttendanceForDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<AttendanceResponse> records = attendanceService.getAllAttendanceForDate(date).stream()
                .map(dtoMapper::toAttendanceResponse)
                .toList();
        return ResponseEntity.ok(records);
    }
}
