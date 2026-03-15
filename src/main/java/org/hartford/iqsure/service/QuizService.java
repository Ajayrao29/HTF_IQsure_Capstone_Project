/*
 * FILE: QuizService.java | LOCATION: service/
 * PURPOSE: Business logic for quiz CRUD. Admin creates/edits/deletes quizzes.
 * CALLED BY: QuizController.java
 * USES: QuizRepository, QuestionRepository
 */
package org.hartford.iqsure.service;

import lombok.RequiredArgsConstructor;
import org.hartford.iqsure.dto.request.QuizRequestDTO;
import org.hartford.iqsure.dto.response.QuizResponseDTO;
import org.hartford.iqsure.entity.Quiz;
import org.hartford.iqsure.exception.ResourceNotFoundException;
import org.hartford.iqsure.repository.QuestionRepository;
import org.hartford.iqsure.repository.QuizRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;

    public QuizResponseDTO createQuiz(QuizRequestDTO dto) {
        Quiz quiz = Quiz.builder()
                .title(dto.getTitle())
                .category(dto.getCategory())
                .difficulty(dto.getDifficulty())
                .build();
        return toDTO(quizRepository.save(quiz));
    }

    public QuizResponseDTO getQuizById(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));
        return toDTO(quiz);
    }

    public List<QuizResponseDTO> getAllQuizzes() {
        return quizRepository.findAll().stream().map(this::toDTO).toList();
    }

    public List<QuizResponseDTO> getQuizzesByCategory(String category) {
        return quizRepository.findByCategory(category).stream().map(this::toDTO).toList();
    }

    public QuizResponseDTO updateQuiz(Long quizId, QuizRequestDTO dto) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + quizId));
        quiz.setTitle(dto.getTitle());
        quiz.setCategory(dto.getCategory());
        quiz.setDifficulty(dto.getDifficulty());
        return toDTO(quizRepository.save(quiz));
    }

    public void deleteQuiz(Long quizId) {
        if (!quizRepository.existsById(quizId)) {
            throw new ResourceNotFoundException("Quiz not found with id: " + quizId);
        }
        quizRepository.deleteById(quizId);
    }

    private QuizResponseDTO toDTO(Quiz quiz) {
        return QuizResponseDTO.builder()
                .quizId(quiz.getQuizId())
                .title(quiz.getTitle())
                .category(quiz.getCategory())
                .difficulty(quiz.getDifficulty())
                .totalQuestions((int) questionRepository.countByQuiz_QuizId(quiz.getQuizId()))
                .build();
    }
}
