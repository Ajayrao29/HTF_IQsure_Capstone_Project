/*
 * FILE: BadgeController.java | LOCATION: controller/
 * PURPOSE: Badge management API. Admin creates badges; users view earned badges.
 * ENDPOINTS: POST/GET/DELETE /api/v1/badges, GET /api/v1/badges/user/{userId}
 * FLOW: BadgeMgmtComponent / BadgesComponent → api.service.ts → THIS → BadgeService
 */
package org.hartford.iqsure.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hartford.iqsure.dto.request.BadgeRequestDTO;
import org.hartford.iqsure.dto.response.BadgeResponseDTO;
import org.hartford.iqsure.service.BadgeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/badges")
@RequiredArgsConstructor
@Tag(name = "Badges", description = "Badge management and user badges")
public class BadgeController {

    private final BadgeService badgeService;

    @PostMapping
    @Operation(summary = "Create a badge (Admin)")
    public ResponseEntity<BadgeResponseDTO> create(@Valid @RequestBody BadgeRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(badgeService.createBadge(dto));
    }

    @GetMapping
    @Operation(summary = "Get all badges")
    public ResponseEntity<List<BadgeResponseDTO>> getAll() {
        return ResponseEntity.ok(badgeService.getAllBadges());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get badges earned by a user")
    public ResponseEntity<List<BadgeResponseDTO>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(badgeService.getBadgesByUser(userId));
    }

    @PutMapping("/{badgeId}")
    @Operation(summary = "Update a badge (Admin)")
    public ResponseEntity<BadgeResponseDTO> update(@PathVariable Long badgeId, @Valid @RequestBody BadgeRequestDTO dto) {
        return ResponseEntity.ok(badgeService.updateBadge(badgeId, dto));
    }

    @DeleteMapping("/{badgeId}")
    @Operation(summary = "Delete a badge (Admin)")
    public ResponseEntity<Void> delete(@PathVariable Long badgeId) {
        badgeService.deleteBadge(badgeId);
        return ResponseEntity.noContent().build();
    }
}
