/*
 * FILE: QuestionRequestDTO.java | LOCATION: dto/request/
 * PURPOSE: DTO for adding a question to a quiz. Admin sends this.
 * FLOW: QuizMgmtComponent → api.service.ts → POST /api/v1/questions → QuestionController → QuestionService
 */
package org.hartford.iqsure.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuestionRequestDTO {

    @NotNull(message = "Quiz ID is required")
    private Long quizId;

    @NotBlank(message = "Question text is required")
    private String text;

    /**
     * Comma-separated options string.
     * e.g. "Term Life,Whole Life,Universal Life,Variable Life"
     */
    @NotBlank(message = "Options are required")
    private String options;

    private String explanation;
}
