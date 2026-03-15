/*
 * FILE: AttemptResponseDTO.java | LOCATION: dto/response/
 * PURPOSE: DTO returned after a quiz is submitted OR when viewing attempt history.
 *          Contains score, percentage, points earned, and any new badges unlocked.
 *          Maps to "AttemptResponse" interface in models/models.ts.
 * RETURNED BY: AttemptController → submit(), getByUser() → QuizAttemptService
 * USED IN FRONTEND: QuizResultComponent (pages/quiz-result/), DashboardComponent (pages/dashboard/)
 */
package org.hartford.iqsure.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AttemptResponseDTO {

    private Long attemptId;
    private Long userId;
    private Long quizId;
    private String quizTitle;
    private Integer score;
    private Integer totalQuestions;
    private Double percentage;
    private Integer pointsEarned;
    private LocalDateTime attemptDate;

    /**
     * Newly unlocked badges after this attempt (if any)
     */
    private java.util.List<BadgeResponseDTO> newBadgesUnlocked;

    private java.util.List<QuestionReportDTO> questions;
}
