/*
 * ============================================================================
 * FILE: GlobalExceptionHandler.java | LOCATION: exception/
 * PURPOSE: Catches ALL exceptions thrown anywhere in the backend and converts
 *          them into proper JSON error responses for the frontend.
 *          Without this, Spring would return ugly HTML error pages.
 *
 * HOW IT WORKS:
 *   - @RestControllerAdvice → Spring calls methods here when ANY controller throws an exception
 *   - @ExceptionHandler(XyzException.class) → catches that specific exception type
 *
 * EXCEPTION TYPES HANDLED:
 *   1. ResourceNotFoundException → 404 Not Found (e.g., user not found)
 *   2. BadRequestException → 400 Bad Request (e.g., duplicate email)
 *   3. MethodArgumentNotValidException → 400 (e.g., @NotBlank validation failed)
 *   4. Exception (any other) → 500 Internal Server Error
 *
 * FRONTEND READS: err.error.message in the Angular subscribe error handler
 * ============================================================================
 */
package org.hartford.iqsure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(new ErrorResponse(404, "Not Found", ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(new ErrorResponse(400, "Bad Request", ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            errors.put(field, error.getDefaultMessage());
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(new ErrorResponse(500, "Internal Server Error", ex.getMessage(), LocalDateTime.now()));
    }
}



