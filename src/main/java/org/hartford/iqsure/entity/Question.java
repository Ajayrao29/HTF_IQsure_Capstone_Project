/*
 * ============================================================================
 * FILE: Question.java
 * LOCATION: src/main/java/org/hartford/iqsure/entity/
 * PURPOSE: Represents the "questions" table in the database.
 *          Each question belongs to ONE quiz and has multiple-choice options.
 *          Options are stored as a comma-separated string.
 *
 * DATABASE TABLE: questions
 *   - questionId (primary key)
 *   - quiz_id (foreign key → quizzes table)
 *   - text (the question text)
 *   - options (comma-separated, e.g., "Term Life,Whole Life,Universal Life")
 *
 * RELATIONSHIPS:
 *   - MANY questions belong to ONE quiz → see entity/Quiz.java
 *   - ONE question has MANY answers → see entity/Answer.java
 *     (typically just one correct answer per question)
 *
 * USED BY:
 *   - QuestionController.java (controller/) → API for adding/getting questions
 *   - QuestionService.java (service/) → business logic
 *   - QuestionRepository.java (repository/) → database queries
 *   - TakeQuizComponent (frontend: pages/take-quiz/) → shows questions to user
 * ============================================================================
 */
package org.hartford.iqsure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    // MANY questions belong to ONE quiz
    // FetchType.LAZY → the quiz object is loaded only when we access it
    // JoinColumn → this creates a "quiz_id" column in the questions table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;  // → entity/Quiz.java

    // The actual question text, e.g., "Which type of life insurance provides coverage for a set period?"
    @Column(nullable = false, columnDefinition = "TEXT") // TEXT allows longer strings than VARCHAR
    private String text;

    /**
     * Stores comma-separated options, e.g.:
     * "Term Life,Whole Life,Universal Life,Variable Life"
     *
     * In the frontend and QuestionResponseDTO, these are split into a List:
     * ["Term Life", "Whole Life", "Universal Life", "Variable Life"]
     * (see: QuestionService.java → toDTO() method)
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String options;

    // ONE question can have answers (correct answer info)
    // Typically one Answer per question defining which option is correct
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Answer> answers = new ArrayList<>(); // → entity/Answer.java

    // The explanation to show after the user answers the question (for feature 2)
    @Column(columnDefinition = "TEXT")
    private String explanation;
}
