package com.payroll.employeemanagement.controller;

import com.payroll.employeemanagement.dto.DepartmentRequest;
import com.payroll.employeemanagement.dto.DepartmentResponse;
import com.payroll.employeemanagement.entity.Department;
import com.payroll.employeemanagement.entity.Employee;
import com.payroll.employeemanagement.mapper.DtoMapper;
import com.payroll.employeemanagement.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller exposing Department API endpoints using DTOs.
 * Enforces role-based permissions using Method Security annotations.
 */
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;
    private final DtoMapper dtoMapper;

    /**
     * POST /api/departments : Create a new department.
     * Accessible by ADMIN only.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DepartmentResponse> createDepartment(@Valid @RequestBody DepartmentRequest request) {
        Department department = Department.builder()
                .name(request.getName())
                .headEmployee(request.getHeadEmployeeId() != null ? Employee.builder().employeeId(request.getHeadEmployeeId()).build() : null)
                .build();

        Department createdDepartment = departmentService.createDepartment(department);
        return new ResponseEntity<>(dtoMapper.toDepartmentResponse(createdDepartment), HttpStatus.CREATED);
    }

    /**
     * GET /api/departments : Retrieve all departments.
     * Accessible by all authenticated employees.
     */
    @GetMapping
    public ResponseEntity<List<DepartmentResponse>> getAllDepartments() {
        List<DepartmentResponse> departments = departmentService.getAllDepartments().stream()
                .map(dtoMapper::toDepartmentResponse)
                .toList();
        return ResponseEntity.ok(departments);
    }

    /**
     * GET /api/departments/{id} : Retrieve a single department by its ID.
     * Accessible by all authenticated employees.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponse> getDepartmentById(@PathVariable Long id) {
        Department department = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(dtoMapper.toDepartmentResponse(department));
    }

    /**
     * PUT /api/departments/{id} : Update an existing department.
     * Accessible by ADMIN only.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DepartmentResponse> updateDepartment(
            @PathVariable Long id, 
            @Valid @RequestBody DepartmentRequest request) {
        Department department = Department.builder()
                .name(request.getName())
                .headEmployee(request.getHeadEmployeeId() != null ? Employee.builder().employeeId(request.getHeadEmployeeId()).build() : null)
                .build();

        Department updatedDepartment = departmentService.updateDepartment(id, department);
        return ResponseEntity.ok(dtoMapper.toDepartmentResponse(updatedDepartment));
    }

    /**
     * DELETE /api/departments/{id} : Delete a department.
     * Accessible by ADMIN only.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }
}
