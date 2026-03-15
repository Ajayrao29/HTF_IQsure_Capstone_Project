/*
 * FILE: QuizRequestDTO.java | LOCATION: dto/request/
 * PURPOSE: DTO for creating/updating quizzes. Sent by admin from QuizMgmtComponent.
 * FLOW: QuizMgmtComponent → api.service.ts → POST/PUT /api/v1/quizzes → QuizController → QuizService
 */
package org.hartford.iqsure.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hartford.iqsure.entity.Quiz;

@Data
public class QuizRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Difficulty is required")
    private Quiz.Difficulty difficulty;
}
