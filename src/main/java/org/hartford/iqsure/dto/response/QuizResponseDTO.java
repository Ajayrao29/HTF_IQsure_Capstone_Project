/*
 * FILE: QuizResponseDTO.java | LOCATION: dto/response/
 * PURPOSE: DTO sent back to frontend with quiz info. Maps to "Quiz" interface in models/models.ts.
 * RETURNED BY: QuizController endpoints → QuizService.toDTO()
 * USED IN FRONTEND: QuizzesComponent (pages/quizzes/), QuizMgmtComponent (pages/admin/quiz-mgmt/)
 */
package org.hartford.iqsure.dto.response;

import lombok.Builder;
import lombok.Data;
import org.hartford.iqsure.entity.Quiz;

@Data
@Builder
public class QuizResponseDTO {

    private Long quizId;
    private String title;
    private String category;
    private Quiz.Difficulty difficulty;
    private int totalQuestions;
}
