package com.payroll.employeemanagement.repository;

import com.payroll.employeemanagement.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Attendance entities.
 */
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    /**
     * Retrieves all attendance logs associated with a specific employee.
     * 
     * @param employeeId Unique ID of the employee
     * @return List of attendance logs
     */
    List<Attendance> findByEmployeeEmployeeId(Long employeeId);

    /**
     * Finds a single attendance log for a specific employee and date.
     * Essential for check-in validations and updating check-out timestamps.
     * 
     * @param employeeId Unique ID of the employee
     * @param date Date of interest
     * @return An Optional containing the matching attendance record
     */
    Optional<Attendance> findByEmployeeEmployeeIdAndDate(Long employeeId, LocalDate date);

    /**
     * Retrieves all attendance records for a specific calendar date.
     * Useful for daily attendance reports.
     * 
     * @param date Calendar date
     * @return List of attendance records for the date
     */
    List<Attendance> findByDate(LocalDate date);
}
