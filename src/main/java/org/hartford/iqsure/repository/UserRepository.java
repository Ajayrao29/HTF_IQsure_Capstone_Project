/*
 * ============================================================================
 * FILE: UserRepository.java | LOCATION: repository/
 * PURPOSE: Database access layer for the "users" table.
 *          Extends JpaRepository which gives us FREE methods like:
 *          - findAll(), findById(), save(), deleteById(), count(), existsById()
 *          We only need to define CUSTOM queries here.
 *
 * WHAT IS A REPOSITORY?
 *   - It's the layer that talks directly to the database
 *   - Spring Data JPA auto-generates the SQL queries from method names!
 *   - Example: findByEmail(email) → SELECT * FROM users WHERE email = ?
 *
 * USED BY: UserService.java, DataSeeder.java, QuizAttemptService.java
 * ENTITY: User.java (entity/) → maps to "users" database table
 * ============================================================================
 */
package org.hartford.iqsure.repository;

import org.hartford.iqsure.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository  // Marks this as a Spring Data repository (database access layer)
public interface UserRepository extends JpaRepository<User, Long> {
    // JpaRepository<User, Long> means: this repo manages "User" entities with "Long" type IDs
    // FREE methods we get: findAll(), findById(Long), save(User), deleteById(Long), count(), existsById(Long)

    // Find user by email — used during login (see: UserService.login())
    // Returns Optional because user might not exist
    Optional<User> findByEmail(String email);

    // Check if an email is already taken — used during registration (see: UserService.register())
    boolean existsByEmail(String email);

    // Custom JPQL query to get all non-admin users sorted by points (highest first)
    // Used for the leaderboard (see: UserService.getLeaderboard())
    @Query("SELECT u FROM User u WHERE u.role = 'ROLE_USER' ORDER BY u.userPoints DESC")
    List<User> findTopUsersByPoints();

    // Find all users with a specific role
    List<User> findByRole(User.Role role);
}
