package com.payroll.employeemanagement.dto;

import lombok.*;

/**
 * DTO representing the authentication response containing the JWT token.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    @Builder.Default
    private String tokenType = "Bearer";
    private String email;
    private String role;
}
