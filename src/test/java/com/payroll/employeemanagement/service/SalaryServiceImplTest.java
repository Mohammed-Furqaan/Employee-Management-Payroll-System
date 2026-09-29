package com.payroll.employeemanagement.service;

import com.payroll.employeemanagement.entity.Employee;
import com.payroll.employeemanagement.entity.Salary;
import com.payroll.employeemanagement.exception.ResourceAlreadyExistsException;
import com.payroll.employeemanagement.exception.ResourceNotFoundException;
import com.payroll.employeemanagement.repository.EmployeeRepository;
import com.payroll.employeemanagement.repository.SalaryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalaryServiceImplTest {

    @Mock
    private SalaryRepository salaryRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private SalaryServiceImpl salaryService;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = Employee.builder()
                .employeeId(1L)
                .name("Jane Doe")
                .email("jane@company.com")
                .hireDate(LocalDate.of(2023, 1, 1))
                .password("encoded_pass")
                .build();
    }

    @Test
    @DisplayName("Should successfully compute net pay and save salary record")
    void generateSalary_Success() {
        BigDecimal basicPay = new BigDecimal("5000.00");
        BigDecimal allowances = new BigDecimal("1000.00");
        BigDecimal deductions = new BigDecimal("500.00");
        Integer month = 9;
        Integer year = 2026;

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(salaryRepository.findByEmployeeEmployeeIdAndMonthAndYear(1L, month, year)).thenReturn(Optional.empty());
        when(salaryRepository.save(any(Salary.class))).thenAnswer(invocation -> {
            Salary s = invocation.getArgument(0);
            s.setSalaryId(100L);
            return s;
        });

        Salary result = salaryService.generateSalary(1L, basicPay, allowances, deductions, month, year);

        assertThat(result).isNotNull();
        assertThat(result.getSalaryId()).isEqualTo(100L);
        assertThat(result.getNetPay()).isEqualByComparingTo(new BigDecimal("5500.00")); // 5000 + 1000 - 500
        verify(salaryRepository, times(1)).save(any(Salary.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when employee does not exist")
    void generateSalary_EmployeeNotFound() {
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> salaryService.generateSalary(
                999L, new BigDecimal("5000"), BigDecimal.ZERO, BigDecimal.ZERO, 9, 2026))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Employee not found with ID: 999");

        verify(salaryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ResourceAlreadyExistsException when salary for month/year already exists")
    void generateSalary_DuplicatePeriod() {
        Salary existingSalary = Salary.builder().salaryId(50L).build();
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(salaryRepository.findByEmployeeEmployeeIdAndMonthAndYear(1L, 9, 2026))
                .thenReturn(Optional.of(existingSalary));

        assertThatThrownBy(() -> salaryService.generateSalary(
                1L, new BigDecimal("5000"), BigDecimal.ZERO, BigDecimal.ZERO, 9, 2026))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("Salary slip has already been generated");

        verify(salaryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should catch DataIntegrityViolationException on save and throw ResourceAlreadyExistsException")
    void generateSalary_DataIntegrityViolation() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(salaryRepository.findByEmployeeEmployeeIdAndMonthAndYear(1L, 9, 2026)).thenReturn(Optional.empty());
        when(salaryRepository.save(any(Salary.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate entry"));

        assertThatThrownBy(() -> salaryService.generateSalary(
                1L, new BigDecimal("5000"), BigDecimal.ZERO, BigDecimal.ZERO, 9, 2026))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("Salary slip has already been generated");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when negative amounts are supplied")
    void generateSalary_NegativeAmounts() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        assertThatThrownBy(() -> salaryService.generateSalary(
                1L, new BigDecimal("-100"), BigDecimal.ZERO, BigDecimal.ZERO, 9, 2026))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot be negative");
    }
}
