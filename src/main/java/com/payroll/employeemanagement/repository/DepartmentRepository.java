package com.payroll.employeemanagement.repository;

import com.payroll.employeemanagement.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing Department entities.
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    /**
     * Finds a department by its unique name.
     * 
     * @param name Name of the department
     * @return An Optional containing the Department if found
     */
    Optional<Department> findByName(String name);
}
