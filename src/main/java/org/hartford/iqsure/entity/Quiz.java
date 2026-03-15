/*
 * ============================================================================
 * FILE: Quiz.java
 * LOCATION: src/main/java/org/hartford/iqsure/entity/
 * PURPOSE: Represents the "quizzes" table in the database.
 *          A quiz is a set of questions about insurance topics.
 *          Admin creates quizzes; users take them to earn points.
 *
 * DATABASE TABLE: quizzes
 *   - quizId (auto-generated primary key)
 *   - title (e.g., "Health Insurance Basics")
 *   - category (e.g., "Health", "Life", "Motor")
 *   - difficulty (EASY, MEDIUM, or HARD)
 *
 * RELATIONSHIPS:
 *   - ONE quiz has MANY questions → see entity/Question.java
 *   - ONE quiz has MANY attempts → see entity/QuizAttempt.java
 *
 * USED BY:
 *   - QuizController.java (controller/) → CRUD API endpoints for quizzes
 *   - QuizService.java (service/) → business logic
 *   - QuizRepository.java (repository/) → database queries
 *   - QuizMgmtComponent (frontend: pages/admin/quiz-mgmt/) → admin UI
 *   - QuizzesComponent (frontend: pages/quizzes/) → user sees quiz list
 * ============================================================================
 */
package org.hartford.iqsure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity                        // JPA: maps this class to a database table
@Table(name = "quizzes")       // Table name in database
@Data                          // Lombok: auto getters/setters
@NoArgsConstructor             // Lombok: empty constructor
@AllArgsConstructor            // Lombok: full constructor
@Builder                       // Lombok: builder pattern
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment ID
    private Long quizId;

    @Column(nullable = false)
    private String title;        // Quiz title, e.g., "Life Insurance Fundamentals"

    @Column(nullable = false)
    private String category;     // Category like "Health", "Life", "Motor"

    @Enumerated(EnumType.STRING) // Store as string (not number) in DB
    @Column(nullable = false)
    private Difficulty difficulty; // EASY, MEDIUM, or HARD

    // ONE quiz has MANY questions
    // mappedBy = "quiz" → Question entity has a "quiz" field pointing back here
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Question> questions = new ArrayList<>();  // → entity/Question.java

    // ONE quiz has MANY attempts (each time a user takes this quiz)
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<QuizAttempt> attempts = new ArrayList<>(); // → entity/QuizAttempt.java

    // Enum for the three difficulty levels
    public enum Difficulty {
        EASY, MEDIUM, HARD
    }
}
