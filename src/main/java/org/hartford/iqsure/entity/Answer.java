/*
 * ============================================================================
 * FILE: Answer.java
 * LOCATION: src/main/java/org/hartford/iqsure/entity/
 * PURPOSE: Represents the "answers" table in the database.
 *          Stores the CORRECT answer for each question.
 *          Each answer links to ONE question and tells which option is right.
 *
 * DATABASE TABLE: answers
 *   - answerId (primary key)
 *   - question_id (foreign key → questions table)
 *   - text (the correct answer text, e.g., "Term Life")
 *   - rightOption (0-based index of the correct option)
 *
 * EXAMPLE:
 *   If a question has options: "Term Life,Whole Life,Universal Life,Variable Life"
 *   And the correct answer is "Term Life", then:
 *     - text = "Term Life"
 *     - rightOption = 0 (because "Term Life" is the first option, index 0)
 *
 * RELATIONSHIPS:
 *   - MANY answers belong to ONE question → see entity/Question.java
 *
 * USED BY:
 *   - QuestionController.java → POST /api/v1/questions/answers (admin adds answer)
 *   - QuestionService.java → addAnswerToQuestion() method
 *   - QuizAttemptService.java → uses rightOption to score quiz submissions
 *   - AnswerRepository.java → database queries to find correct answers
 * ============================================================================
 */
package org.hartford.iqsure.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "answers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId;

    // MANY answers belong to ONE question (usually just 1 answer per question)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;  // → entity/Question.java

    /**
     * The display text of the correct answer.
     * e.g. "Term Life"
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String text;

    /**
     * Index of the correct option (0-based).
     * e.g. 0 → first option is correct, 1 → second option, etc.
     * This is compared with the user's selected option index during quiz scoring
     * (see: QuizAttemptService.java → submitQuiz() method)
     */
    @Column(nullable = false)
    private Integer rightOption;
}
