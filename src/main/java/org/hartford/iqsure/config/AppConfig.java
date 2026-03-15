/*
 * ============================================================================
 * FILE: AppConfig.java
 * LOCATION: src/main/java/org/hartford/iqsure/config/
 * PURPOSE: Holds application-wide configuration values (settings).
 *          These values can be changed in application.properties file
 *          (see: src/main/resources/application.properties)
 *          without modifying Java code.
 *
 * HOW IT WORKS:
 *   - @ConfigurationProperties(prefix = "iqsure") means Spring will look for
 *     properties starting with "iqsure." in application.properties
 *   - Example: setting "iqsure.max-discount-cap=40" in application.properties
 *     would change maxDiscountCap to 40.0
 *
 * USED BY:
 *   - PremiumCalculationService.java → uses maxDiscountCap to limit total discount
 *   - QuizAttemptService.java → uses pointsPerCorrectAnswer to calculate points
 *
 * ANNOTATIONS EXPLAINED:
 *   - @Data (Lombok) → Auto-generates getters, setters, toString, equals, hashCode
 *   - @Configuration → Tells Spring: "this is a config class, create a bean from it"
 *   - @ConfigurationProperties → Binds properties from application.properties to fields
 * ============================================================================
 */
package org.hartford.iqsure.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data           // Lombok: auto-generates getters/setters so we don't write them manually
@Configuration  // Tells Spring to treat this as a configuration bean
@ConfigurationProperties(prefix = "iqsure") // Maps "iqsure.*" properties from application.properties
public class AppConfig {

    // Maximum total discount a user can get on any policy (in %).
    // Even if rules add up to 80%, the user only gets 50% max discount.
    private double maxDiscountCap = 50.0;

    // How many points a user earns for each correct quiz answer.
    // Example: 3 correct answers × 10 = 30 points earned.
    private int pointsPerCorrectAnswer = 10;

    // How many points are needed to go up one level.
    // Example: 250 points → Level 3 (250 / 100 = 2.5, rounded = Level 3)
    private int pointsPerLevel = 100;
}


