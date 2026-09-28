package com.payroll.employeemanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception thrown when a user tries to register or update an employee profile
 * with an email that is already registered. Automatically maps to an HTTP 400 Bad Request.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class EmailAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
