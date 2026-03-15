/*
 * FILE: QuizController.java | LOCATION: controller/
 * PURPOSE: Quiz CRUD API endpoints. Admin creates/edits/deletes quizzes; users browse them.
 * ENDPOINTS: GET/POST/PUT/DELETE /api/v1/quizzes
 * FLOW: QuizzesComponent / QuizMgmtComponent → api.service.ts → THIS → QuizService
 */
package org.hartford.iqsure.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hartford.iqsure.dto.request.QuizRequestDTO;
import org.hartford.iqsure.dto.response.QuizResponseDTO;
import org.hartford.iqsure.service.QuizService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/quizzes")
@RequiredArgsConstructor
@Tag(name = "Quizzes", description = "Quiz management")
public class QuizController {

    private final QuizService quizService;

    @PostMapping
    @Operation(summary = "Create a new quiz (Admin)")
    public ResponseEntity<QuizResponseDTO> create(@Valid @RequestBody QuizRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(quizService.createQuiz(dto));
    }

    @GetMapping
    @Operation(summary = "Get all quizzes")
    public ResponseEntity<List<QuizResponseDTO>> getAll(
            @RequestParam(required = false) String category) {
        if (category != null) {
            return ResponseEntity.ok(quizService.getQuizzesByCategory(category));
        }
        return ResponseEntity.ok(quizService.getAllQuizzes());
    }

    @GetMapping("/{quizId}")
    @Operation(summary = "Get quiz by ID")
    public ResponseEntity<QuizResponseDTO> getById(@PathVariable Long quizId) {
        return ResponseEntity.ok(quizService.getQuizById(quizId));
    }

    @PutMapping("/{quizId}")
    @Operation(summary = "Update quiz (Admin)")
    public ResponseEntity<QuizResponseDTO> update(
            @PathVariable Long quizId,
            @Valid @RequestBody QuizRequestDTO dto) {
        return ResponseEntity.ok(quizService.updateQuiz(quizId, dto));
    }

    @DeleteMapping("/{quizId}")
    @Operation(summary = "Delete quiz (Admin)")
    public ResponseEntity<Void> delete(@PathVariable Long quizId) {
        quizService.deleteQuiz(quizId);
        return ResponseEntity.noContent().build();
    }
}
