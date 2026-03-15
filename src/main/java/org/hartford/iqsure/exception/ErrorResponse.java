/*
 * FILE: ErrorResponse.java | LOCATION: exception/
 * PURPOSE: Standard error JSON structure returned to the frontend when something goes wrong.
 *          Example: { "status": 404, "error": "Not Found", "message": "User not found", "timestamp": "..." }
 * USED BY: GlobalExceptionHandler.java creates these and sends them back
 */
package org.hartford.iqsure.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;
}
