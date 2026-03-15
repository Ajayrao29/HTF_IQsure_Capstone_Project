/*
 * FILE: QuestionService.java | LOCATION: service/
 * PURPOSE: Business logic for adding questions and correct answers to quizzes.
 * CALLED BY: QuestionController.java
 * USES: QuestionRepository, QuizRepository, AnswerRepository
 */
package org.hartford.iqsure.service;

import lombok.RequiredArgsConstructor;
import org.hartford.iqsure.dto.request.AnswerRequestDTO;
import org.hartford.iqsure.dto.request.QuestionRequestDTO;
import org.hartford.iqsure.dto.response.QuestionResponseDTO;
import org.hartford.iqsure.entity.Answer;
import org.hartford.iqsure.entity.Question;
import org.hartford.iqsure.entity.Quiz;
import org.hartford.iqsure.exception.ResourceNotFoundException;
import org.hartford.iqsure.repository.AnswerRepository;
import org.hartford.iqsure.repository.QuestionRepository;
import org.hartford.iqsure.repository.QuizRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final AnswerRepository answerRepository;

    public QuestionResponseDTO addQuestion(QuestionRequestDTO dto) {
        Quiz quiz = quizRepository.findById(dto.getQuizId())
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with id: " + dto.getQuizId()));

        Question question = Question.builder()
                .quiz(quiz)
                .text(dto.getText())
                .options(dto.getOptions())
                .explanation(dto.getExplanation())
                .build();

        return toDTO(questionRepository.save(question));
    }

    public QuestionResponseDTO addAnswerToQuestion(AnswerRequestDTO dto) {
        Question question = questionRepository.findById(dto.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with id: " + dto.getQuestionId()));

        Answer answer = Answer.builder()
                .question(question)
                .text(dto.getText())
                .rightOption(dto.getRightOption())
                .build();

        answerRepository.save(answer);
        return toDTO(question);
    }

    public List<QuestionResponseDTO> getQuestionsByQuiz(Long quizId) {
        if (!quizRepository.existsById(quizId)) {
            throw new ResourceNotFoundException("Quiz not found with id: " + quizId);
        }
        return questionRepository.findByQuiz_QuizId(quizId)
                .stream().map(this::toDTO).toList();
    }

    public void deleteQuestion(Long questionId) {
        if (!questionRepository.existsById(questionId)) {
            throw new ResourceNotFoundException("Question not found with id: " + questionId);
        }
        questionRepository.deleteById(questionId);
    }

    private QuestionResponseDTO toDTO(Question q) {
        String[] optsArr = q.getOptions().contains("|") ? q.getOptions().split("\\|") : q.getOptions().split(",");
        List<String> cleanedOpts = Arrays.stream(optsArr)
                .map(opt -> opt.replaceFirst("^[A-Da-d][\\)\\.]\\s*", "").trim())
                .filter(opt -> !opt.isEmpty())
                .toList();
                
        return QuestionResponseDTO.builder()
                .questionId(q.getQuestionId())
                .quizId(q.getQuiz().getQuizId())
                .text(q.getText())
                .options(cleanedOpts)
                .build();
    }
}
