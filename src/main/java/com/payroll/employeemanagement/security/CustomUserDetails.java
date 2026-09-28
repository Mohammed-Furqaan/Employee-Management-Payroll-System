package com.payroll.employeemanagement.security;

import com.payroll.employeemanagement.entity.Employee;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom UserDetails implementation wrapping the Employee entity.
 * This class exposes user authentication and role authority information to Spring Security.
 */
public class CustomUserDetails implements UserDetails {

    private final Employee employee;

    public CustomUserDetails(Employee employee) {
        this.employee = employee;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = employee.getRole() != null ? employee.getRole().getRoleName() : "ROLE_EMPLOYEE";
        // If roles in DB do not contain "ROLE_" prefix, we would prepend it here.
        // By design, our DB role names are "ROLE_ADMIN", "ROLE_MANAGER", "ROLE_EMPLOYEE".
        return Collections.singletonList(new SimpleGrantedAuthority(roleName));
    }

    @Override
    public String getPassword() {
        return employee.getPassword();
    }

    @Override
    public String getUsername() {
        return employee.getEmail(); // Using email as the login username
    }

    /**
     * Helper method to retrieve the underlying Employee entity object.
     * Useful for extracting employee ID or metadata inside service layers.
     */
    public Employee getEmployee() {
        return employee;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
