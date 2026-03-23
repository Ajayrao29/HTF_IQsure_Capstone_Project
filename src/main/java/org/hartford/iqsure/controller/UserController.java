/*
 * ============================================================================
 * FILE: UserController.java | LOCATION: controller/
 * PURPOSE: User profile and leaderboard API endpoints.
 *
 * API ENDPOINTS:
 *   GET    /api/v1/users             → Get all users (Admin)
 *   GET    /api/v1/users/{userId}    → Get user profile by ID
 *   DELETE /api/v1/users/{userId}    → Delete a user (Admin)
 *   GET    /api/v1/users/leaderboard → Get leaderboard (top users by points)
 *
 * FLOW: DashboardComponent / AdminUsersComponent / LeaderboardComponent
 *       → api.service.ts → THIS → UserService → UserRepository
 * ============================================================================
 */
package org.hartford.iqsure.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.hartford.iqsure.dto.response.LeaderboardEntryDTO;
import org.hartford.iqsure.dto.response.UserResponseDTO;
import org.hartford.iqsure.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profiles and leaderboard")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all users (Admin)")
    public ResponseEntity<List<UserResponseDTO>> getAll() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user profile by ID")
    public ResponseEntity<UserResponseDTO> getProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getProfile(userId));
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "Delete user (Admin)")
    public ResponseEntity<Void> delete(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/leaderboard")
    @Operation(summary = "Get leaderboard — top users by points")
    public ResponseEntity<List<LeaderboardEntryDTO>> leaderboard() {
        return ResponseEntity.ok(userService.getLeaderboard());
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponseDTO>> getUsersByRole(@PathVariable org.hartford.iqsure.entity.User.Role role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    @PutMapping("/{userId}/status")
    public ResponseEntity<UserResponseDTO> updateStatus(@PathVariable Long userId, @RequestParam String status) {
        return ResponseEntity.ok(userService.updateStatus(userId, status));
    }

    @PostMapping("/underwriter")
    public ResponseEntity<UserResponseDTO> createUnderwriter(@RequestBody org.hartford.iqsure.dto.request.UserRequestDTO dto) {
        return ResponseEntity.ok(userService.createUnderwriter(dto));
    }

    @PostMapping("/claims-officer")
    public ResponseEntity<UserResponseDTO> createClaimsOfficer(@RequestBody org.hartford.iqsure.dto.request.UserRequestDTO dto) {
        return ResponseEntity.ok(userService.createClaimsOfficer(dto));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> updateProfile(@PathVariable Long userId, @RequestBody org.hartford.iqsure.dto.request.UserRequestDTO dto) {
        return ResponseEntity.ok(userService.updateProfile(userId, dto));
    }
}
