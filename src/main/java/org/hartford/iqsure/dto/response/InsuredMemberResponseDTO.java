package org.hartford.iqsure.dto.response;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsuredMemberResponseDTO {
    private Long id;
    private String fullName;
    private String relationship;
    private LocalDate dateOfBirth;
    private String gender;
    private String preExistingConditions;
}
