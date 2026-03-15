/*
 * FILE: QuizRepository.java | LOCATION: repository/
 * PURPOSE: Database access for "quizzes" table. Used by QuizService.java.
 * ENTITY: Quiz.java (entity/)
 * Spring auto-generates SQL from method names (e.g., findByCategory → WHERE category = ?)
 */
package org.hartford.iqsure.repository;

import org.hartford.iqsure.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    // Filter quizzes by category (e.g. "Life Insurance")
    List<Quiz> findByCategory(String category);

    // Filter quizzes by difficulty level
    List<Quiz> findByDifficulty(Quiz.Difficulty difficulty);

    // Filter by both category and difficulty
    List<Quiz> findByCategoryAndDifficulty(String category, Quiz.Difficulty difficulty);
}
