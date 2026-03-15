/*
 * FILE: BadgeResponseDTO.java | LOCATION: dto/response/
 * PURPOSE: DTO for badge data sent to frontend. Maps to "Badge" interface in models/models.ts.
 * RETURNED BY: BadgeController endpoints → BadgeService.toDTO()
 * USED IN FRONTEND: BadgesComponent (pages/badges/), BadgeMgmtComponent (pages/admin/badge-mgmt/)
 */
package org.hartford.iqsure.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BadgeResponseDTO {

    private Long badgeId;
    private String name;
    private String description;
    private Integer reqPoints;
}
