/*
 * FILE: LeaderboardEntryDTO.java | LOCATION: dto/response/
 * PURPOSE: DTO for each row in the leaderboard. Maps to "LeaderboardEntry" in models/models.ts.
 * RETURNED BY: UserController → leaderboard() → UserService.getLeaderboard()
 * USED IN FRONTEND: LeaderboardComponent (pages/leaderboard/)
 */
package org.hartford.iqsure.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaderboardEntryDTO {

    private Integer rank;
    private Long userId;
    private String name;
    private Integer userPoints;
    private long quizzesAttempted;
}
