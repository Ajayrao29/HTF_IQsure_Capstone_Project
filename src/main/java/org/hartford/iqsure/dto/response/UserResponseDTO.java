/*
 * FILE: UserResponseDTO.java | LOCATION: dto/response/
 * PURPOSE: DTO sent back to frontend with user profile data.
 *          Does NOT include password (security). Maps to "User" interface in frontend models/models.ts.
 * RETURNED BY: UserController → getProfile(), getAllUsers() → UserService.toDTO()
 */
package org.hartford.iqsure.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDTO {

    private Long userId;
    private String name;
    private String email;
    private String phone;
    private Integer userPoints;
    private String role;

    // Extended fields
    private String licenseNumber;
    private String specialization;
    private java.math.BigDecimal commissionPercentage;
    private Integer totalQuotesSent;

    private String employeeId;
    private String department;
    private java.math.BigDecimal approvalLimit;
    private Integer totalClaimsProcessed;
    private Integer totalClaimsApproved;
    private Integer totalClaimsRejected;

    private String city;
    private String state;
    private String zipCode;
    private String status;
}
