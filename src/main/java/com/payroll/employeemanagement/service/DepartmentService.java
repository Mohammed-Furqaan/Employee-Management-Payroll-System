package com.payroll.employeemanagement.service;

import com.payroll.employeemanagement.entity.Department;

import java.util.List;

/**
 * Service interface defining the business contract for Department CRUD operations.
 */
public interface DepartmentService {

    /**
     * Creates and saves a new department.
     * 
     * @param department The department details to save
     * @return The saved department entity
     */
    Department createDepartment(Department department);

    /**
     * Retrieves all departments from the database.
     * 
     * @return List of all departments
     */
    List<Department> getAllDepartments();

    /**
     * Retrieves a single department by its ID.
     * 
     * @param id Unique ID of the department
     * @return The department entity
     */
    Department getDepartmentById(Long id);

    /**
     * Updates an existing department.
     * 
     * @param id Unique ID of the department to update
     * @param departmentDetails Updated department details
     * @return The updated department entity
     */
    Department updateDepartment(Long id, Department departmentDetails);

    /**
     * Deletes a department by its ID.
     * 
     * @param id Unique ID of the department to delete
     */
    void deleteDepartment(Long id);
}
