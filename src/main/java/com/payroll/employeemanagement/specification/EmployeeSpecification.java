package com.payroll.employeemanagement.specification;

import com.payroll.employeemanagement.entity.Employee;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specifications for dynamic filtering of Employee entities using the JPA Criteria API.
 */
public class EmployeeSpecification {

    /**
     * Filters employees whose name contains the search string (case-insensitive).
     */
    public static Specification<Employee> hasName(String name) {
        return (root, query, cb) -> {
            if (name == null || name.trim().isEmpty()) {
                return cb.conjunction(); // Return an empty predicate (always true)
            }
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    /**
     * Filters employees whose email contains the search string (case-insensitive).
     */
    public static Specification<Employee> hasEmail(String email) {
        return (root, query, cb) -> {
            if (email == null || email.trim().isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%");
        };
    }

    /**
     * Filters employees belonging to a specific department.
     */
    public static Specification<Employee> hasDepartmentId(Long departmentId) {
        return (root, query, cb) -> {
            if (departmentId == null) {
                return cb.conjunction();
            }
            // Joins to department entity and checks departmentId
            return cb.equal(root.get("department").get("departmentId"), departmentId);
        };
    }

    /**
     * Filters employees matching a specific role.
     */
    public static Specification<Employee> hasRoleId(Long roleId) {
        return (root, query, cb) -> {
            if (roleId == null) {
                return cb.conjunction();
            }
            // Joins to role entity and checks roleId
            return cb.equal(root.get("role").get("roleId"), roleId);
        };
    }
}
