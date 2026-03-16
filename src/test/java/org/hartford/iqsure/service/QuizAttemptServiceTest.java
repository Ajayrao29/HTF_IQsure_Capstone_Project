package org.hartford.iqsure.service;

import org.hartford.iqsure.dto.request.QuizSubmissionDTO;
import org.hartford.iqsure.dto.response.AttemptResponseDTO;
import org.hartford.iqsure.entity.*;
import org.hartford.iqsure.exception.ResourceNotFoundException;
import org.hartford.iqsure.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuizAttemptServiceTest {

    @Mock
    private QuizAttemptRepository attemptRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private QuizRepository quizRepository;
    @Mock
    private AnswerRepository answerRepository;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private BadgeService badgeService;
    @Mock
    private DiscountRuleRepository discountRuleRepository;
    @Mock
    private RewardRepository rewardRepository;
    @Mock
    private UserRewardRepository userRewardRepository;
    @Mock
    private UserBadgeRepository userBadgeRepository;

    @InjectMocks
    private QuizAttemptService attemptService;

    @Test
    public void testSubmitQuiz_Successful() {
        // --- ARRANGE ---
        User user = User.builder().userId(1L).userPoints(0).build();
        Quiz quiz = Quiz.builder().quizId(1L).title("Test Quiz").build();
        Question q1 = Question.builder().questionId(1L).text("Q1").options("A|B|C").build();
        Answer a1 = Answer.builder().question(q1).rightOption(0).build();

        QuizSubmissionDTO dto = new QuizSubmissionDTO();
        dto.setQuizId(1L);
        dto.setAnswers(Map.of(1L, 0));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz));
        when(answerRepository.findByQuestion_Quiz_QuizId(1L)).thenReturn(List.of(a1));
        when(questionRepository.findByQuiz_QuizId(1L)).thenReturn(List.of(q1));
        when(attemptRepository.findByQuiz_QuizId(1L)).thenReturn(List.of());
        when(attemptRepository.save(any(QuizAttempt.class))).thenAnswer(i -> i.getArguments()[0]);

        // --- ACT ---
        AttemptResponseDTO result = attemptService.submitQuiz(1L, dto);

        // --- ASSERT ---
        assertNotNull(result);
        assertEquals(1, result.getScore());
        assertEquals(10, result.getPointsEarned());
        verify(userRepository).save(user);
        verify(badgeService).checkAndAwardBadges(1L);
    }

    @Test
    public void testGetAttemptById_NotFound() {
        // --- ARRANGE ---
        when(attemptRepository.findById(99L)).thenReturn(Optional.empty());

        // --- ACT & ASSERT ---
        assertThrows(ResourceNotFoundException.class, () -> attemptService.getAttemptById(99L));
    }

    @Test
    public void testGetAttemptsByUser_Empty() {
        // --- ARRANGE ---
        when(userRepository.existsById(1L)).thenReturn(true);
        when(attemptRepository.findByUser_UserIdOrderByAttemptDateDesc(1L)).thenReturn(Collections.emptyList());

        // --- ACT ---
        List<AttemptResponseDTO> results = attemptService.getAttemptsByUser(1L);

        // --- ASSERT ---
        assertTrue(results.isEmpty());
    }
}
