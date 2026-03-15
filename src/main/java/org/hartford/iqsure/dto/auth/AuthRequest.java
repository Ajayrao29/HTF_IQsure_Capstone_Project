/*
 * ============================================================================
 * FILE: AuthRequest.java
 * LOCATION: src/main/java/org/hartford/iqsure/dto/auth/
 * PURPOSE: DTO (Data Transfer Object) for LOGIN requests.
 *          The frontend sends this JSON when a user clicks "Login".
 *          Contains just email and password.
 *
 * SENT FROM: LoginComponent (frontend: pages/login/login.ts)
 *            → api.service.ts → login({ email, password })
 *            → POST /api/auth/login
 *
 * RECEIVED BY: AuthController.java → login() method
 *              → passed to UserService.java → login()
 *
 * VALIDATION: @NotBlank ensures fields are not empty; @Email checks format.
 *             If validation fails → GlobalExceptionHandler returns 400 error.
 * ============================================================================
 */
package org.hartford.iqsure.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data  // Lombok: auto-generates getters/setters
public class AuthRequest {

    @NotBlank(message = "Email is required")    // Cannot be null or empty string
    @Email(message = "Invalid email format")     // Must be valid email format (has @)
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
