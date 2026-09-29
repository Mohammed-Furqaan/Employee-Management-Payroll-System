package com.payroll.employeemanagement.controller;

import com.payroll.employeemanagement.dto.AuthResponse;
import com.payroll.employeemanagement.dto.LoginRequest;
import com.payroll.employeemanagement.dto.RegisterRequest;
import com.payroll.employeemanagement.entity.Department;
import com.payroll.employeemanagement.entity.Employee;
import com.payroll.employeemanagement.entity.Role;
import com.payroll.employeemanagement.exception.EmailAlreadyExistsException;
import com.payroll.employeemanagement.exception.ResourceNotFoundException;
import com.payroll.employeemanagement.repository.DepartmentRepository;
import com.payroll.employeemanagement.repository.EmployeeRepository;
import com.payroll.employeemanagement.repository.RoleRepository;
import com.payroll.employeemanagement.security.CustomUserDetails;
import com.payroll.employeemanagement.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller handling authentication endpoints (login, registration).
 * Open to public access via SecurityConfig rules.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    /**
     * POST /api/auth/login : Authenticates user credentials and returns a JWT token.
     * 
     * @param loginRequest Login credentials (email, password)
     * @return ResponseEntity with AuthResponse containing JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // 1. Authenticate credentials
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // 2. Set security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Generate token
        String jwt = tokenProvider.generateToken(authentication);

        // 4. Extract principal details
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String role = userDetails.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .findFirst()
                .orElse("ROLE_EMPLOYEE");

        AuthResponse authResponse = AuthResponse.builder()
                .token(jwt)
                .email(userDetails.getUsername())
                .role(role)
                .build();

        return ResponseEntity.ok(authResponse);
    }

    /**
     * POST /api/auth/register : Registers a new employee, hashes password, and returns AuthResponse with JWT.
     * 
     * @param registerRequest Employee details for registration
     * @return ResponseEntity with AuthResponse containing JWT token
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        // 1. Check if email is already registered
        if (employeeRepository.existsByEmail(registerRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Email address already in use: " + registerRequest.getEmail());
        }

        // 2. Always assign standard ROLE_EMPLOYEE for self-registration
        Role role = roleRepository.findByRoleName("ROLE_EMPLOYEE")
                .orElseGet(() -> roleRepository.save(Role.builder().roleName("ROLE_EMPLOYEE").build()));

        // 3. Resolve Department (optional during registration)
        Department department = null;
        if (registerRequest.getDepartmentId() != null) {
            department = departmentRepository.findById(registerRequest.getDepartmentId())
                    .orElse(null);
        }

        // 4. Resolve Manager (optional during registration)
        Employee manager = null;
        if (registerRequest.getManagerId() != null) {
            manager = employeeRepository.findById(registerRequest.getManagerId())
                    .orElse(null);
        }

        // 5. Build and hash the Employee object
        Employee employee = Employee.builder()
                .name(registerRequest.getName())
                .email(registerRequest.getEmail())
                .phone(registerRequest.getPhone())
                .hireDate(registerRequest.getHireDate() != null ? registerRequest.getHireDate() : java.time.LocalDate.now())
                .password(passwordEncoder.encode(registerRequest.getPassword())) // Password Hashing
                .role(role)
                .department(department)
                .manager(manager)
                .build();

        // 6. Save employee
        employeeRepository.save(employee);

        // 7. Authenticate user to generate JWT token immediately
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        registerRequest.getEmail(),
                        registerRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);

        AuthResponse authResponse = AuthResponse.builder()
                .token(jwt)
                .email(employee.getEmail())
                .role(role.getRoleName())
                .build();

        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }
}
