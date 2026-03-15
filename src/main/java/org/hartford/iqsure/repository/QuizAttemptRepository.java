/*
 * FILE: QuizAttemptRepository.java | LOCATION: repository/
 * PURPOSE: Database access for "quiz_attempts" table. Used by QuizAttemptService.java, UserService.java.
 * ENTITY: QuizAttempt.java (entity/)
 * KEY METHOD: findBestScorePercentByUserId → used by PremiumCalculationService for discount rules
 */
package org.hartford.iqsure.repository;

import org.hartford.iqsure.entity.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    // All attempts by a user (for attempt history)
    List<QuizAttempt> findByUser_UserIdOrderByAttemptDateDesc(Long userId);

    // All attempts on a specific quiz (for analytics)
    List<QuizAttempt> findByQuiz_QuizId(Long quizId);

    // Check if user already attempted a quiz (for first-attempt points logic)
    Optional<QuizAttempt> findFirstByUser_UserIdAndQuiz_QuizId(Long userId, Long quizId);

    // Count how many quizzes a user has attempted (gamification stat)
    long countByUser_UserId(Long userId);

    // Best score percentage across all attempts for a user (used by premium calculation engine)
    @Query("SELECT COALESCE(MAX(CAST(a.score AS double) / NULLIF(a.totalQuestions, 0) * 100), 0.0) " +
           "FROM QuizAttempt a WHERE a.user.userId = :userId")
    Double findBestScorePercentByUserId(@Param("userId") Long userId);
}
