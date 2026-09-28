package com.payroll.employeemanagement.service;

import com.payroll.employeemanagement.entity.Department;
import com.payroll.employeemanagement.entity.Employee;
import com.payroll.employeemanagement.entity.Role;
import com.payroll.employeemanagement.exception.EmailAlreadyExistsException;
import com.payroll.employeemanagement.exception.ResourceNotFoundException;
import com.payroll.employeemanagement.repository.DepartmentRepository;
import com.payroll.employeemanagement.repository.EmployeeRepository;
import com.payroll.employeemanagement.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import com.payroll.employeemanagement.specification.EmployeeSpecification;
import java.util.List;

/**
 * Service implementation for managing Employee operations.
 */
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Employee createEmployee(Employee employee) {
        // 1. Verify email uniqueness
        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new EmailAlreadyExistsException("Employee already exists with email: " + employee.getEmail());
        }

        // 2. Resolve and attach relationships if present
        resolveRelationships(employee);

        // 3. Encrypt Password
        if (employee.getPassword() != null && !employee.getPassword().isEmpty()) {
            employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        }

        // 4. Save and return
        return employeeRepository.save(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Employee> getFilteredEmployees(String name, String email, Long departmentId, Long roleId, Pageable pageable) {
        Specification<Employee> spec = Specification.where(EmployeeSpecification.hasName(name))
                .and(EmployeeSpecification.hasEmail(email))
                .and(EmployeeSpecification.hasDepartmentId(departmentId))
                .and(EmployeeSpecification.hasRoleId(roleId));
        return employeeRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
    }

    @Override
    @Transactional
    public Employee updateEmployee(Long id, Employee employeeDetails) {
        // 1. Retrieve the existing Employee
        Employee existingEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));

        // 2. Verify email uniqueness (must not be in use by any other employee)
        if (!existingEmployee.getEmail().equals(employeeDetails.getEmail()) &&
                employeeRepository.existsByEmail(employeeDetails.getEmail())) {
            throw new EmailAlreadyExistsException("Email is already in use by another employee: " + employeeDetails.getEmail());
        }

        // 3. Update fields
        existingEmployee.setName(employeeDetails.getName());
        existingEmployee.setEmail(employeeDetails.getEmail());
        existingEmployee.setPhone(employeeDetails.getPhone());
        existingEmployee.setHireDate(employeeDetails.getHireDate());
        
        // Encrypt and update password if a new password is provided
        if (employeeDetails.getPassword() != null && !employeeDetails.getPassword().isEmpty()) {
            existingEmployee.setPassword(passwordEncoder.encode(employeeDetails.getPassword()));
        }

        // 4. Resolve and attach relationships
        existingEmployee.setDepartment(employeeDetails.getDepartment());
        existingEmployee.setRole(employeeDetails.getRole());
        existingEmployee.setManager(employeeDetails.getManager());
        resolveRelationships(existingEmployee);

        // 5. Save and return
        return employeeRepository.save(existingEmployee);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
        
        // Break references in Department before deleting (if the employee is a department head)
        // We will add specific business rules for department deletion later.
        
        employeeRepository.delete(employee);
    }

    /**
     * Resolves and verifies that the references to Department, Role, and Manager
     * actually exist in the database. Throws ResourceNotFoundException if they are missing.
     */
    private void resolveRelationships(Employee employee) {
        // Resolve Department
        if (employee.getDepartment() != null && employee.getDepartment().getDepartmentId() != null) {
            Long deptId = employee.getDepartment().getDepartmentId();
            Department dept = departmentRepository.findById(deptId)
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + deptId));
            employee.setDepartment(dept);
        }

        // Resolve Role
        if (employee.getRole() != null && employee.getRole().getRoleId() != null) {
            Long roleId = employee.getRole().getRoleId();
            Role role = roleRepository.findById(roleId)
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + roleId));
            employee.setRole(role);
        }

        // Resolve Manager (Self-referencing relationship)
        if (employee.getManager() != null && employee.getManager().getEmployeeId() != null) {
            Long mgrId = employee.getManager().getEmployeeId();
            Employee manager = employeeRepository.findById(mgrId)
                    .orElseThrow(() -> new ResourceNotFoundException("Manager employee not found with ID: " + mgrId));
            employee.setManager(manager);
        }
    }
}
