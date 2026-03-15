/*
 * FILE: QuizSubmissionDTO.java | LOCATION: dto/request/
 * PURPOSE: DTO sent when a user SUBMITS a quiz with their answers.
 *          Contains the quizId and a map of questionId → selected option index.
 * FLOW: TakeQuizComponent → api.service.ts → POST /api/v1/attempts?userId=X
 *       → AttemptController → QuizAttemptService.submitQuiz()
 * EXAMPLE JSON: { "quizId": 1, "answers": { "1": 2, "2": 0, "3": 3 } }
 *   means: for question 1, user picked option index 2; for question 2, option index 0; etc.
 */
package org.hartford.iqsure.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class QuizSubmissionDTO {

    @NotNull(message = "Quiz ID is required")
    private Long quizId;

    /**
     * Map of questionId → selected option index (0-based)
     * Example:
     * {
     *   "1": 2,
     *   "2": 0,
     *   "3": 3
     * }
     */
    @NotEmpty(message = "Answers cannot be empty")
    private Map<Long, Integer> answers;

    private Integer speedBonus;
}
