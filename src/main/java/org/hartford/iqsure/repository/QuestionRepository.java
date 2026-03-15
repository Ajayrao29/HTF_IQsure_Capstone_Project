/*
 * FILE: QuestionRepository.java | LOCATION: repository/
 * PURPOSE: Database access for "questions" table. Used by QuestionService.java, QuizAttemptService.java.
 * ENTITY: Question.java (entity/)
 * NOTE: "findByQuiz_QuizId" means: follow the "quiz" field in Question, then match on "quizId"
 */
package org.hartford.iqsure.repository;

import org.hartford.iqsure.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    // Get all questions for a given quiz
    List<Question> findByQuiz_QuizId(Long quizId);

    // Count questions in a quiz (used to calculate score %)
    long countByQuiz_QuizId(Long quizId);
}
