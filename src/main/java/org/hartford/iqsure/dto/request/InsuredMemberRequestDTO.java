package org.hartford.iqsure.dto.request;

import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InsuredMemberRequestDTO {
    private String fullName;
    private String relationship;
    private LocalDate dateOfBirth;
    private String gender;
    private String preExistingConditions;
}
