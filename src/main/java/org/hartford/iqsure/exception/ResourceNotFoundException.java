/*
 * FILE: ResourceNotFoundException.java | LOCATION: exception/
 * PURPOSE: Custom exception thrown when a requested item doesn't exist in the database.
 *          Returns HTTP 404 (Not Found) to the frontend.
 * EXAMPLES: "User not found with id: 99", "Quiz not found with id: 5"
 * CAUGHT BY: GlobalExceptionHandler.java → handleNotFound() method
 * THROWN BY: All service classes when findById() returns empty
 */
package org.hartford.iqsure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
