package com.payroll.employeemanagement.service;

import com.payroll.employeemanagement.entity.Attendance;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service interface defining operations for Employee Attendance tracking.
 */
public interface AttendanceService {

    /**
     * Records check-in for the current date.
     * Enforces late check-in threshold (09:30 AM) and duplicate checks.
     * 
     * @param employeeId Unique ID of the employee
     * @return The recorded Attendance record
     */
    Attendance checkIn(Long employeeId);

    /**
     * Records check-out for the current date.
     * Enforces that the employee must have checked in earlier today.
     * 
     * @param employeeId Unique ID of the employee
     * @return The updated Attendance record
     */
    Attendance checkOut(Long employeeId);

    /**
     * Retrieves attendance history logs for a specific employee.
     * 
     * @param employeeId Unique ID of the employee
     * @return List of attendance records
     */
    List<Attendance> getAttendanceHistoryByEmployeeId(Long employeeId);

    /**
     * Retrieves today's active attendance record for a specific employee (if present).
     * 
     * @param employeeId Unique ID of the employee
     * @return Optional containing today's attendance record
     */
    Optional<Attendance> getTodaysAttendance(Long employeeId);

    /**
     * Retrieves all attendance records for a specific calendar date (typically for HR/Admin reports).
     * 
     * @param date Calendar date
     * @return List of attendance records
     */
    List<Attendance> getAllAttendanceForDate(LocalDate date);
}
