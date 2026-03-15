package org.hartford.iqsure.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QuestionReportDTO {
    private String questionText;
    private String selectedAnswer;
    private String correctAnswer;
    private String explanation;
    private boolean isCorrect;
}
