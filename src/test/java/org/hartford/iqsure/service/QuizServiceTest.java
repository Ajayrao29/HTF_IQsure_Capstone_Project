package org.hartford.iqsure.service;

import org.hartford.iqsure.dto.request.QuizRequestDTO;
import org.hartford.iqsure.dto.response.QuizResponseDTO;
import org.hartford.iqsure.entity.Quiz;
import org.hartford.iqsure.repository.QuestionRepository;
import org.hartford.iqsure.repository.QuizRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * UNIT TEST CONCEPT:
 * We are testing QuizService in ISOLATION. 
 * We use @Mock to create "fake" versions of the Repositories so we don't need a real database.
 */
@ExtendWith(MockitoExtension.class) // Enables Mockito support
public class QuizServiceTest {

    // @Mock creates a simulated object. It has no real logic, but we can tell it what to return.
    @Mock
    private QuizRepository quizRepositoryMock;

    @Mock
    private QuestionRepository questionRepositoryMock;

    // @InjectMocks creates the actual Service and "plugs in" the mocks created above.
    @InjectMocks
    private QuizService quizService;

    @Test
    public void testGetQuizById() {
        // --- STEP 1: ARRANGE (Set up the fake data and mock behavior) ---
        Quiz quiz = Quiz.builder()
                .quizId(1L)
                .title("Health Quiz")
                .category("Health")
                .difficulty(Quiz.Difficulty.EASY)
                .build();

        // Tell the mock: "When findById(1) is called, return our fake quiz object"
        when(quizRepositoryMock.findById(1L)).thenReturn(Optional.of(quiz));
        // Tell the mock: "When counting questions for quiz 1, return the number 5"
        when(questionRepositoryMock.countByQuiz_QuizId(1L)).thenReturn(5L);

        // --- STEP 2: ACT (Execute the actual method we want to test) ---
        QuizResponseDTO result = quizService.getQuizById(1L);

        // --- STEP 3: ASSERT (Check if the results are what we expected) ---
        assertNotNull(result); // The result should not be null
        assertEquals("Health Quiz", result.getTitle()); // Title should match
        assertEquals(5, result.getTotalQuestions()); // Question count should match
        
        // Final check: Did the service actually call the repository method we expected?
        verify(quizRepositoryMock).findById(1L);
    }
    
    @Test
    public void testCreateQuiz() {
        // --- STEP 1: ARRANGE ---
        QuizRequestDTO request = new QuizRequestDTO();
        request.setTitle("New Quiz");
        request.setCategory("General");
        request.setDifficulty(Quiz.Difficulty.HARD);

        Quiz savedQuiz = Quiz.builder()
                .quizId(2L)
                .title("New Quiz")
                .category("General")
                .difficulty(Quiz.Difficulty.HARD)
                .build();

        // Mock the .save() method to return our savedQuiz object
        when(quizRepositoryMock.save(any(Quiz.class))).thenReturn(savedQuiz);
        when(questionRepositoryMock.countByQuiz_QuizId(2L)).thenReturn(0L);

        // --- STEP 2: ACT ---
        QuizResponseDTO result = quizService.createQuiz(request);

        // --- STEP 3: ASSERT ---
        assertNotNull(result);
        assertEquals("New Quiz", result.getTitle());
        verify(quizRepositoryMock).save(any(Quiz.class)); // Verify .save() was called exactly once
    }
}
