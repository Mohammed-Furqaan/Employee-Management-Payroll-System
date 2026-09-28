package com.payroll.employeemanagement.controller;

import com.payroll.employeemanagement.dto.SalaryGenerationRequest;
import com.payroll.employeemanagement.dto.SalaryResponse;
import com.payroll.employeemanagement.entity.Salary;
import com.payroll.employeemanagement.mapper.DtoMapper;
import com.payroll.employeemanagement.security.CustomUserDetails;
import com.payroll.employeemanagement.service.SalaryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller exposing Salary and Payroll API endpoints using DTOs.
 * Controls access permissions for monthly pay slips.
 */
@RestController
@RequestMapping("/api/salaries")
@RequiredArgsConstructor
public class SalaryController {

    private final SalaryService salaryService;
    private final DtoMapper dtoMapper;

    /**
     * POST /api/salaries : Generates and registers a new monthly salary slip.
     * Restricted to ADMIN role.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SalaryResponse> generateSalary(@Valid @RequestBody SalaryGenerationRequest request) {
        Salary salary = salaryService.generateSalary(
                request.getEmployeeId(),
                request.getBasicPay(),
                request.getAllowances(),
                request.getDeductions(),
                request.getMonth(),
                request.getYear()
        );
        return new ResponseEntity<>(dtoMapper.toSalaryResponse(salary), HttpStatus.CREATED);
    }

    /**
     * GET /api/salaries : Retrieve all salary records.
     * Restricted to ADMIN and MANAGER roles.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<SalaryResponse>> getAllSalaries() {
        List<SalaryResponse> salaries = salaryService.getAllSalaries().stream()
                .map(dtoMapper::toSalaryResponse)
                .toList();
        return ResponseEntity.ok(salaries);
    }

    /**
     * GET /api/salaries/{id} : Retrieve details of a specific salary slip by ID.
     * Accessible by ADMIN, MANAGER, or the Employee who owns the salary slip.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SalaryResponse> getSalaryById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Salary salary = salaryService.getSalaryById(id);

        String roleName = userDetails.getEmployee().getRole().getRoleName();
        boolean isOwner = salary.getEmployee().getEmployeeId().equals(userDetails.getEmployee().getEmployeeId());

        if (!roleName.equals("ROLE_ADMIN") && !roleName.equals("ROLE_MANAGER") && !isOwner) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return ResponseEntity.ok(dtoMapper.toSalaryResponse(salary));
    }

    /**
     * GET /api/salaries/employee/{employeeId} : Retrieve payroll history for a specific employee.
     * Accessible by ADMIN, MANAGER, or the Employee if matching the path variable ID.
     */
    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or #employeeId == principal.employee.employeeId")
    public ResponseEntity<List<SalaryResponse>> getSalaryHistoryByEmployeeId(@PathVariable Long employeeId) {
        List<SalaryResponse> salaries = salaryService.getSalaryHistoryByEmployeeId(employeeId).stream()
                .map(dtoMapper::toSalaryResponse)
                .toList();
        return ResponseEntity.ok(salaries);
    }
}
