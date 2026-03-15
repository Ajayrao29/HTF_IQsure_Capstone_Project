/*
 * ============================================================================
 * FILE: UserRequestDTO.java  |  LOCATION: dto/request/
 * PURPOSE: DTO for user registration. Sent from RegisterComponent (frontend).
 * FLOW: RegisterComponent → api.service.ts → POST /api/auth/register → AuthController → UserService
 * MAPS TO FRONTEND: register() method in pages/register/register.ts
 * ============================================================================
 */
package org.hartford.iqsure.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;          // User's full name

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;         // Must be unique — checked in UserService.register()

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;      // Will be hashed with BCrypt before saving

    private String phone;         // Optional phone number

    // Optional — defaults to ROLE_USER if not provided
    // Only used internally; regular users can't set themselves as admin
    private String role;

    // Extended fields for creation
    private String licenseNumber;
    private String specialization;
    private java.math.BigDecimal commissionPercentage;

    private String employeeId;
    private String department;
    private java.math.BigDecimal approvalLimit;

    private String city;
    private String state;
    private String zipCode;
}
