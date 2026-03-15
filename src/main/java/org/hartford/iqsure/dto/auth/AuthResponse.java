/*
 * ============================================================================
 * FILE: AuthResponse.java
 * LOCATION: src/main/java/org/hartford/iqsure/dto/auth/
 * PURPOSE: DTO sent BACK to the frontend after successful login or registration.
 *          Contains user info that the frontend stores in localStorage
 *          (see: AuthService.saveUser() in frontend/src/app/services/auth.service.ts)
 *
 * RETURNED BY: AuthController.java → login() and register() methods
 *              → UserService.java builds this response
 *
 * FRONTEND USAGE:
 *   - Stored in localStorage as "iqsure_user" (see auth.service.ts)
 *   - userId → used in all API calls (e.g., getProfile, submitQuiz)
 *   - role → determines if user sees admin panel or user dashboard
 *   - token → currently "NO-AUTH" (no JWT implemented yet)
 *
 * MAPS TO FRONTEND: AuthResponse interface in models/models.ts
 * ============================================================================
 */
package org.hartford.iqsure.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;                // Auth token (// real JWT token)
    @Builder.Default
    private String tokenType = "Bearer"; // Token type (standard for JWT)
    private Long userId;                 // User's database ID — used in all frontend API calls
    private String name;                 // User's display name
    private String email;                // User's email
    private String role;                 // "ROLE_USER" or "ROLE_ADMIN" — controls frontend routing
}
