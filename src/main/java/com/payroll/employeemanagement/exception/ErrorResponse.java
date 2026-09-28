package com.payroll.employeemanagement.exception;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Standard API error response payload returned by the Global Exception Handler.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private Object details; // Can be a string message or Map<String, String> of validation field errors
}
