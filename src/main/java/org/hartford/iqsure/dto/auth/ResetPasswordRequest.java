package org.hartford.iqsure.dto.auth;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    private String otp;
    private String newPassword;
}
