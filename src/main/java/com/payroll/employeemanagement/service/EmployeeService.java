package com.payroll.employeemanagement.service;

import com.payroll.employeemanagement.entity.Employee;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Service interface defining the business contract for Employee CRUD operations.
 */
public interface EmployeeService {

    /**
     * Creates and saves a new employee.
     * 
     * @param employee The employee details to save
     * @return The saved employee entity
     */
    Employee createEmployee(Employee employee);

    /**
     * Retrieves all employees from the database.
     * 
     * @return List of all employees
     */
    List<Employee> getAllEmployees();

    /**
     * Retrieves a paginated and filtered list of employees based on search criteria.
     * 
     * @param name Optional search string for name matching
     * @param email Optional search string for email matching
     * @param departmentId Optional department filter
     * @param roleId Optional role filter
     * @param pageable Pagination and sorting parameters
     * @return Page of matching Employee entities
     */
    Page<Employee> getFilteredEmployees(String name, String email, Long departmentId, Long roleId, Pageable pageable);

    /**
     * Retrieves a single employee by their ID.
     * 
     * @param id Unique ID of the employee
     * @return The employee entity
     */
    Employee getEmployeeById(Long id);

    /**
     * Updates an existing employee profile.
     * 
     * @param id Unique ID of the employee to update
     * @param employeeDetails Updated employee details
     * @return The updated employee entity
     */
    Employee updateEmployee(Long id, Employee employeeDetails);

    /**
     * Deletes an employee by their ID.
     * 
     * @param id Unique ID of the employee to delete
     */
    void deleteEmployee(Long id);
}
