/*
 * ============================================================================
 * FILE: QuizAttempt.java
 * LOCATION: src/main/java/org/hartford/iqsure/entity/
 * PURPOSE: Represents the "quiz_attempts" table. Records every time a user
 *          submits a quiz. Stores their score, points earned, and timestamp.
 *
 * DATABASE TABLE: quiz_attempts
 *   - attemptId, user_id (FK), quiz_id (FK), score, totalQuestions,
 *     pointsEarned, attemptDate
 *
 * KEY RULE: Points are only awarded on the FIRST attempt per quiz.
 *           If a user retakes a quiz, they get 0 new points.
 *           (see: QuizAttemptService.java → submitQuiz() method)
 *
 * RELATIONSHIPS:
 *   - MANY attempts belong to ONE user → see entity/User.java
 *   - MANY attempts belong to ONE quiz → see entity/Quiz.java
 *
 * USED BY:
 *   - AttemptController.java (controller/) → submit quiz, get history
 *   - QuizAttemptService.java (service/) → scoring logic
 *   - QuizAttemptRepository.java (repository/) → database queries
 *   - TakeQuizComponent (frontend: pages/take-quiz/) → submits answers
 *   - QuizResultComponent (frontend: pages/quiz-result/) → shows results
 *   - DashboardComponent (frontend: pages/dashboard/) → recent attempts
 * ============================================================================
 */
package org.hartford.iqsure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_attempts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long attemptId;

    // Which user made this attempt
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;   // → entity/User.java

    // Which quiz was attempted
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;   // → entity/Quiz.java

    @Column(nullable = false)
    private Integer score;  // Number of correct answers (e.g., 4 out of 5)

    /**
     * Total questions in the quiz at time of attempt.
     * Used to calculate percentage: (score / totalQuestions) * 100
     */
    @Column(nullable = false)
    private Integer totalQuestions;

    /**
     * Points awarded for this attempt.
     * Points = score × 10 (only on FIRST attempt per quiz, else 0)
     * (see: QuizAttemptService.java → submitQuiz() → isFirstAttempt check)
     */
    @Builder.Default
    private Integer pointsEarned = 0;

    // When the quiz was submitted
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime attemptDate = LocalDateTime.now();
}
