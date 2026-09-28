package com.payroll.employeemanagement.repository;

import com.payroll.employeemanagement.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing Role entities.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Finds a security role by its name (e.g. ROLE_ADMIN).
     * 
     * @param roleName Name of the role to search for
     * @return An Optional containing the Role if found, or empty otherwise
     */
    Optional<Role> findByRoleName(String roleName);
}
