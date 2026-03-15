/*
 * FILE: AnswerRequestDTO.java | LOCATION: dto/request/
 * PURPOSE: DTO for setting the correct answer for a question. Admin sends this.
 * FLOW: QuizMgmtComponent → api.service.ts → POST /api/v1/questions/answers → QuestionController → QuestionService
 */
package org.hartford.iqsure.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnswerRequestDTO {

    @NotNull(message = "Question ID is required")
    private Long questionId;

    @NotBlank(message = "Answer text is required")
    private String text;

    /**
     * Zero-based index of the correct option.
     * e.g. 0 → first option is correct
     */
    @NotNull(message = "Right option index is required")
    @Min(value = 0, message = "Right option must be a valid index (0 or above)")
    private Integer rightOption;
}
