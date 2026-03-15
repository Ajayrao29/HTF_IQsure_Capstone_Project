/*
 * FILE: BadRequestException.java | LOCATION: exception/
 * PURPOSE: Custom exception thrown when the user sends invalid data.
 *          Returns HTTP 400 (Bad Request) to the frontend.
 * EXAMPLES: duplicate email, wrong password, expired reward, inactive policy
 * CAUGHT BY: GlobalExceptionHandler.java → handleBadRequest() method
 * THROWN BY: UserService, RewardService, PolicyService, UserPolicyService, DiscountRuleService
 */

package org.hartford.iqsure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
