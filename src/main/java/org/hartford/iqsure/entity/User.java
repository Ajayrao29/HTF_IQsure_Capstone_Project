/*
 * ============================================================================
 * FILE: User.java
 * LOCATION: src/main/java/org/hartford/iqsure/entity/
 * PURPOSE: Represents the "users" table in the database.
 *          Each instance of this class = one row in the "users" table.
 *          Stores user profile info: name, email, password, points, role.
 *
 * DATABASE TABLE: users
 *   - userId (auto-generated ID, primary key)
 *   - name, email (unique), password (hashed), phone
 *   - userPoints (gamification points earned from quizzes)
 *   - role (ROLE_USER or ROLE_ADMIN)
 *
 * RELATIONSHIPS (this user has many...):
 *   - QuizAttempts → see entity/QuizAttempt.java (user's quiz history)
 *   - UserBadges → see entity/UserBadge.java (badges the user earned)
 *   - UserRewards → see entity/UserReward.java (rewards the user redeemed)
 *
 * USED BY:
 *   - AuthController.java (controller/) → for login/register
 *   - UserController.java (controller/) → for profile/leaderboard
 *   - UserService.java (service/) → business logic for user operations
 *   - UserRepository.java (repository/) → database queries
 *   - DataSeeder.java (config/) → creates default admin
 *
 * ANNOTATIONS EXPLAINED:
 *   - @Entity → Tells JPA: "this class maps to a database table"
 *   - @Table(name = "users") → The actual table name in the database
 *   - @Data (Lombok) → Auto-generates getters, setters, toString, equals
 *   - @Builder → Allows creating objects like: User.builder().name("John").build()
 *   - @NoArgsConstructor / @AllArgsConstructor → Empty and full constructors
 * ============================================================================
 */
package org.hartford.iqsure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity                        // JPA: This class is a database entity (maps to a table)
@Table(name = "users")         // The database table is called "users"
@Data                          // Lombok: auto-generates getters, setters, toString
@NoArgsConstructor             // Lombok: creates empty constructor → new User()
@AllArgsConstructor            // Lombok: creates full constructor → new User(id, name, ...)
@Builder                       // Lombok: allows User.builder().name("John").email("...").build()
public class User {

    @Id                                              // This field is the PRIMARY KEY
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment: DB generates the ID
    private Long userId;

    @Column(nullable = false)   // This column CANNOT be null in the database
    private String name;

    @Column(nullable = false, unique = true) // Cannot be null AND must be unique (no duplicate emails)
    private String email;

    @Column(nullable = false)   // Password is stored as a BCrypt hash (see SecurityConfig.java)
    private String password;

    private String phone;       // Optional field — can be null

    @Builder.Default            // When using Builder pattern, default to 0 points
    private Integer userPoints = 0;  // Total gamification points earned from quizzes

    @Enumerated(EnumType.STRING)  // Store the role as a STRING in DB (not a number)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.ROLE_USER;  // Default role is regular user

    // ============================================================
    // EXTENDED FIELDS FOR UNDERWRITERS & CLAIMS OFFICERS
    // ============================================================

    // Underwriter fields
    private String licenseNumber;
    private String specialization;
    private java.math.BigDecimal commissionPercentage;

    @Builder.Default
    private Integer totalQuotesSent = 0;

    // Claims Officer fields
    private String employeeId;
    private String department;

    @Builder.Default
    private java.math.BigDecimal approvalLimit = new java.math.BigDecimal("500000.00");

    @Builder.Default
    private Integer totalClaimsProcessed = 0;

    @Builder.Default
    private Integer totalClaimsApproved = 0;

    @Builder.Default
    private Integer totalClaimsRejected = 0;

    // Address fields (shown in Manage Customers image)
    private String city;
    private String state;
    private String zipCode;

    // Status field (ACTIVE/INACTIVE)
    @Builder.Default
    private String status = "ACTIVE";

    // ONE user can have MANY quiz attempts
    // mappedBy = "user" → the QuizAttempt entity has a "user" field pointing back here
    // CascadeType.ALL → if user is deleted, their attempts are deleted too
    // FetchType.LAZY → don't load attempts until we actually need them (performance)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<QuizAttempt> quizAttempts = new ArrayList<>();  // → entity/QuizAttempt.java

    // ONE user can have MANY badges
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserBadge> userBadges = new ArrayList<>();      // → entity/UserBadge.java

    // ONE user can have MANY redeemed rewards
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserReward> userRewards = new ArrayList<>();    // → entity/UserReward.java

    // ONE user can have MANY purchased policies
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<UserPolicy> userPolicies = new ArrayList<>();   // → entity/UserPolicy.java

    // ONE user can have MANY premium calculation logs
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PremiumCalculationLog> premiumLogs = new ArrayList<>(); // → entity/PremiumCalculationLog.java

    // Enum defining the two possible roles in the system
    public enum Role {
        ROLE_USER,           // Regular user — can take quizzes, buy policies
        ROLE_ADMIN,          // Admin — can manage everything
        ROLE_UNDERWRITER,    // Underwriter — reviews applications, sends quotes
        ROLE_CLAIMS_OFFICER  // Claims Officer — reviews and settles claims
    }
}

