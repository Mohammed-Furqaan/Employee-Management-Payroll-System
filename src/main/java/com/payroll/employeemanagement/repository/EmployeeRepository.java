package com.payroll.employeemanagement.repository;

import com.payroll.employeemanagement.entity.Employee;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing Employee entities.
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    /**
     * Finds an employee by email address with role and department eagerly loaded.
     * Useful for user authentication and authorization.
     * 
     * @param email Email to search for
     * @return An Optional containing the Employee if found
     */
    @EntityGraph(attributePaths = {"role", "department"})
    Optional<Employee> findByEmail(String email);

    /**
     * Checks if an employee with the given email exists in the database.
     * 
     * @param email Email to verify uniqueness
     * @return true if an employee exists with this email, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Checks if any employee is assigned to a specific department.
     * Useful for checking referential integrity before department deletion.
     * 
     * @param departmentId Unique ID of the department
     * @return true if at least one employee belongs to the department
     */
    boolean existsByDepartmentDepartmentId(Long departmentId);
}
