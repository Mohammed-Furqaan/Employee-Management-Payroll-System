package com.payroll.employeemanagement.service;

import com.payroll.employeemanagement.entity.Employee;
import com.payroll.employeemanagement.entity.Salary;
import com.payroll.employeemanagement.exception.ResourceAlreadyExistsException;
import com.payroll.employeemanagement.exception.ResourceNotFoundException;
import com.payroll.employeemanagement.repository.EmployeeRepository;
import com.payroll.employeemanagement.repository.SalaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service implementation managing Salary and Payroll calculations.
 */
@Service
@RequiredArgsConstructor
public class SalaryServiceImpl implements SalaryService {

    private final SalaryRepository salaryRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public Salary generateSalary(Long employeeId, BigDecimal basicPay, BigDecimal allowances, BigDecimal deductions, Integer month, Integer year) {
        // 1. Verify employee exists
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        // 2. Validate month and year range
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month must be between 1 (January) and 12 (December)");
        }
        if (year < 1900) {
            throw new IllegalArgumentException("Please provide a valid year");
        }

        // 3. Verify values are non-negative
        if (basicPay.compareTo(BigDecimal.ZERO) < 0 ||
                allowances.compareTo(BigDecimal.ZERO) < 0 ||
                deductions.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Base salary components (basic, allowances, deductions) cannot be negative");
        }

        // 4. Double-payout guard: Check if salary already exists for this period
        if (salaryRepository.findByEmployeeEmployeeIdAndMonthAndYear(employeeId, month, year).isPresent()) {
            throw new ResourceAlreadyExistsException("Salary slip has already been generated for Employee ID " 
                    + employeeId + " for the period: " + month + "/" + year);
        }

        // 5. Calculate net pay: net = basic + allowances - deductions
        BigDecimal netPay = basicPay.add(allowances).subtract(deductions);

        // 6. Validate net pay is not negative
        if (netPay.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Calculated Net Pay cannot be negative. Please verify deductions do not exceed total earnings.");
        }

        // 7. Map to entity and save
        Salary salary = Salary.builder()
                .employee(employee)
                .basicPay(basicPay)
                .allowances(allowances)
                .deductions(deductions)
                .netPay(netPay)
                .month(month)
                .year(year)
                .build();

        return salaryRepository.save(salary);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Salary> getSalaryHistoryByEmployeeId(Long employeeId) {
        // Confirm employee exists
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Employee not found with ID: " + employeeId);
        }
        return salaryRepository.findByEmployeeEmployeeId(employeeId);
    }

    @Override
    @Transactional(readOnly = true)
    public Salary getSalaryById(Long salaryId) {
        return salaryRepository.findById(salaryId)
                .orElseThrow(() -> new ResourceNotFoundException("Salary record not found with ID: " + salaryId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Salary> getAllSalaries() {
        return salaryRepository.findAll();
    }
}
