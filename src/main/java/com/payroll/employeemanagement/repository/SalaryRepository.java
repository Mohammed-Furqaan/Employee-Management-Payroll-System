package com.payroll.employeemanagement.repository;

import com.payroll.employeemanagement.entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Salary entities.
 */
@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {

    /**
     * Retrieves all salary records associated with a specific employee.
     * 
     * @param employeeId Unique ID of the employee
     * @return List of salary records
     */
    List<Salary> findByEmployeeEmployeeId(Long employeeId);

    /**
     * Finds a salary record for a specific employee, month, and year.
     * Helps check if a salary slip has already been generated.
     * 
     * @param employeeId Unique ID of the employee
     * @param month Month index (1-12)
     * @param year Year index (e.g. 2026)
     * @return An Optional containing the matching Salary record
     */
    Optional<Salary> findByEmployeeEmployeeIdAndMonthAndYear(Long employeeId, Integer month, Integer year);
}
