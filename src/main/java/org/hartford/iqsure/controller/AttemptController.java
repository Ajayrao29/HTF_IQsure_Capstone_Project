/*
 * ============================================================================
 * FILE: AttemptController.java | LOCATION: controller/
 * PURPOSE: Handles quiz submission and attempt history API endpoints.
 *
 * API ENDPOINTS:
 *   POST /api/v1/attempts?userId=X  → Submit a quiz (scores it, awards points & badges)
 *   GET  /api/v1/attempts?userId=X  → Get all quiz attempts by a user
 *   GET  /api/v1/attempts/{id}      → Get a specific attempt by ID
 *
 * FLOW: TakeQuizComponent → api.service.ts → THIS → QuizAttemptService → repositories
 * FRONTEND: TakeQuizComponent (submit), DashboardComponent (history), QuizResultComponent (result)
 * ============================================================================
 */
package org.hartford.iqsure.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hartford.iqsure.dto.request.QuizSubmissionDTO;
import org.hartford.iqsure.dto.response.AttemptResponseDTO;
import org.hartford.iqsure.service.QuizAttemptService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/attempts")
@RequiredArgsConstructor
@Tag(name = "Attempts", description = "Quiz submission and attempt history")
public class AttemptController {

    private final QuizAttemptService attemptService;

    // POST /api/v1/attempts?userId=1
    @PostMapping
    @Operation(summary = "Submit a quiz attempt")
    public ResponseEntity<AttemptResponseDTO> submit(
            @RequestParam Long userId,
            @Valid @RequestBody QuizSubmissionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(attemptService.submitQuiz(userId, dto));
    }

    // GET /api/v1/attempts?userId=1
    @GetMapping
    @Operation(summary = "Get all attempts by a user")
    public ResponseEntity<List<AttemptResponseDTO>> getByUser(@RequestParam Long userId) {
        return ResponseEntity.ok(attemptService.getAttemptsByUser(userId));
    }

    // GET /api/v1/attempts/{attemptId}
    @GetMapping("/{attemptId}")
    @Operation(summary = "Get attempt details by ID")
    public ResponseEntity<AttemptResponseDTO> getById(@PathVariable Long attemptId) {
        return ResponseEntity.ok(attemptService.getAttemptById(attemptId));
    }
}
