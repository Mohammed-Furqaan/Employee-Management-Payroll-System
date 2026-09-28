package com.payroll.employeemanagement.service;

import com.payroll.employeemanagement.entity.Salary;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface defining operations for the Salary and Payroll module.
 */
public interface SalaryService {

    /**
     * Calculates net pay and creates a new salary record for an employee.
     * Enforces calculations, negative values prevention, and double payout guards.
     * 
     * @param employeeId ID of the employee
     * @param basicPay Basic base pay amount
     * @param allowances Total bonus/allowances amount
     * @param deductions Total tax/deductions amount
     * @param month Pay slip month (1-12)
     * @param year Pay slip year (e.g. 2026)
     * @return The generated and saved Salary entity
     */
    Salary generateSalary(Long employeeId, BigDecimal basicPay, BigDecimal allowances, BigDecimal deductions, Integer month, Integer year);

    /**
     * Retrieves all salary records (pay slips) generated for a specific employee.
     * 
     * @param employeeId Unique ID of the employee
     * @return List of salary records
     */
    List<Salary> getSalaryHistoryByEmployeeId(Long employeeId);

    /**
     * Retrieves a single salary slip by its ID.
     * 
     * @param salaryId Unique ID of the salary slip
     * @return The salary details
     */
    Salary getSalaryById(Long salaryId);

    /**
     * Retrieves all salary logs generated in the system.
     * 
     * @return List of all salary slips
     */
    List<Salary> getAllSalaries();
}
