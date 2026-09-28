package com.payroll.employeemanagement.service;

import com.payroll.employeemanagement.entity.Department;
import com.payroll.employeemanagement.entity.Employee;
import com.payroll.employeemanagement.exception.ResourceAlreadyExistsException;
import com.payroll.employeemanagement.exception.ResourceInUseException;
import com.payroll.employeemanagement.exception.ResourceNotFoundException;
import com.payroll.employeemanagement.repository.DepartmentRepository;
import com.payroll.employeemanagement.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for managing Department operations.
 */
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public Department createDepartment(Department department) {
        // 1. Verify name uniqueness
        if (departmentRepository.findByName(department.getName()).isPresent()) {
            throw new ResourceAlreadyExistsException("Department already exists with name: " + department.getName());
        }

        // 2. Resolve Head Employee if assigned
        resolveHeadEmployee(department);

        // 3. Save and return
        return departmentRepository.save(department);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));
    }

    @Override
    @Transactional
    public Department updateDepartment(Long id, Department departmentDetails) {
        // 1. Retrieve the existing Department
        Department existingDepartment = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));

        // 2. Verify name uniqueness if it has changed
        if (!existingDepartment.getName().equalsIgnoreCase(departmentDetails.getName()) &&
                departmentRepository.findByName(departmentDetails.getName()).isPresent()) {
            throw new ResourceAlreadyExistsException("Department name is already in use: " + departmentDetails.getName());
        }

        // 3. Update basic fields
        existingDepartment.setName(departmentDetails.getName());

        // 4. Resolve and update Head Employee relationship
        existingDepartment.setHeadEmployee(departmentDetails.getHeadEmployee());
        resolveHeadEmployee(existingDepartment);

        // 5. Save and return
        return departmentRepository.save(existingDepartment);
    }

    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        // 1. Check if department exists
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));

        // 2. Check if any employee is currently assigned to this department
        if (employeeRepository.existsByDepartmentDepartmentId(id)) {
            throw new ResourceInUseException("Cannot delete department because it still has employees assigned to it.");
        }

        // 3. Clear reference to this department in any headEmployee reference
        // (If the department has a head employee, they belong to this department as well,
        // and we already blocked deletion since that employee is assigned to the department).
        
        // 4. Delete the department
        departmentRepository.delete(department);
    }

    /**
     * Resolves and verifies that the head employee exists in the database.
     */
    private void resolveHeadEmployee(Department department) {
        if (department.getHeadEmployee() != null && department.getHeadEmployee().getEmployeeId() != null) {
            Long headId = department.getHeadEmployee().getEmployeeId();
            Employee head = employeeRepository.findById(headId)
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found to set as department head with ID: " + headId));
            department.setHeadEmployee(head);
        }
    }
}
