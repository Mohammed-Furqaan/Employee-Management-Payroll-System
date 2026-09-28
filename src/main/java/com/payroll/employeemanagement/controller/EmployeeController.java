package com.payroll.employeemanagement.controller;

import com.payroll.employeemanagement.dto.EmployeeRequest;
import com.payroll.employeemanagement.dto.EmployeeResponse;
import com.payroll.employeemanagement.entity.Department;
import com.payroll.employeemanagement.entity.Employee;
import com.payroll.employeemanagement.entity.Role;
import com.payroll.employeemanagement.mapper.DtoMapper;
import com.payroll.employeemanagement.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for exposing Employee CRUD API endpoints using DTOs.
 */
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;
    private final DtoMapper dtoMapper;

    /**
     * POST /api/employees : Create a new employee.
     * Restricted to ADMIN role.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeRequest request) {
        Employee employee = mapRequestToEntity(request);
        Employee createdEmployee = employeeService.createEmployee(employee);
        return new ResponseEntity<>(dtoMapper.toEmployeeResponse(createdEmployee), HttpStatus.CREATED);
    }

    /**
     * GET /api/employees : Paginated and filtered employee list.
     */
    @GetMapping
    public ResponseEntity<Page<EmployeeResponse>> getAllEmployees(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) Long roleId,
            Pageable pageable) {
        Page<Employee> employees = employeeService.getFilteredEmployees(name, email, departmentId, roleId, pageable);
        return ResponseEntity.ok(employees.map(dtoMapper::toEmployeeResponse));
    }

    /**
     * GET /api/employees/{id} : Retrieve a single employee by their ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        Employee employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(dtoMapper.toEmployeeResponse(employee));
    }

    /**
     * PUT /api/employees/{id} : Update an existing employee.
     * Restricted to ADMIN role.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id, 
            @Valid @RequestBody EmployeeRequest request) {
        Employee employee = mapRequestToEntity(request);
        Employee updatedEmployee = employeeService.updateEmployee(id, employee);
        return ResponseEntity.ok(dtoMapper.toEmployeeResponse(updatedEmployee));
    }

    /**
     * DELETE /api/employees/{id} : Delete an employee.
     * Restricted to ADMIN role.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    private Employee mapRequestToEntity(EmployeeRequest request) {
        return Employee.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .hireDate(request.getHireDate())
                .password(request.getPassword())
                .department(request.getDepartmentId() != null ? Department.builder().departmentId(request.getDepartmentId()).build() : null)
                .role(request.getRoleId() != null ? Role.builder().roleId(request.getRoleId()).build() : null)
                .manager(request.getManagerId() != null ? Employee.builder().employeeId(request.getManagerId()).build() : null)
                .build();
    }
}
