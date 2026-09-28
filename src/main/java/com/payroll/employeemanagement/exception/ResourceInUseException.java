package com.payroll.employeemanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception thrown when trying to delete or alter a resource that is currently
 * referenced by other records (e.g. deleting a department that still has employees assigned).
 * Maps to HTTP 400 Bad Request.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class ResourceInUseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ResourceInUseException(String message) {
        super(message);
    }
}
