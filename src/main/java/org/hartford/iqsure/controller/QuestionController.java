/*
 * FILE: QuestionController.java | LOCATION: controller/
 * PURPOSE: Question and Answer management API. Admin adds questions to quizzes and sets correct answers.
 * ENDPOINTS: POST /api/v1/questions, POST /api/v1/questions/answers, GET/DELETE /api/v1/questions
 * FLOW: QuizMgmtComponent → api.service.ts → THIS → QuestionService
 */
package org.hartford.iqsure.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hartford.iqsure.dto.request.AnswerRequestDTO;
import org.hartford.iqsure.dto.request.QuestionRequestDTO;
import org.hartford.iqsure.dto.response.QuestionResponseDTO;
import org.hartford.iqsure.service.QuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
@Tag(name = "Questions", description = "Question and Answer management")
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    @Operation(summary = "Add a question to a quiz (Admin)")
    public ResponseEntity<QuestionResponseDTO> addQuestion(@Valid @RequestBody QuestionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(questionService.addQuestion(dto));
    }

    @PostMapping("/answers")
    @Operation(summary = "Add correct answer to a question (Admin)")
    public ResponseEntity<QuestionResponseDTO> addAnswer(@Valid @RequestBody AnswerRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(questionService.addAnswerToQuestion(dto));
    }

    @GetMapping("/quiz/{quizId}")
    @Operation(summary = "Get all questions for a quiz")
    public ResponseEntity<List<QuestionResponseDTO>> getByQuiz(@PathVariable Long quizId) {
        return ResponseEntity.ok(questionService.getQuestionsByQuiz(quizId));
    }

    @DeleteMapping("/{questionId}")
    @Operation(summary = "Delete a question (Admin)")
    public ResponseEntity<Void> delete(@PathVariable Long questionId) {
        questionService.deleteQuestion(questionId);
        return ResponseEntity.noContent().build();
    }
}
