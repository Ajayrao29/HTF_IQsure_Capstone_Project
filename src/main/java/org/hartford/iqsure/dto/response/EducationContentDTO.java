package org.hartford.iqsure.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EducationContentDTO {
    private Long id;
    private String topic;
    private String language;
    private String title;
    private String content;
}
