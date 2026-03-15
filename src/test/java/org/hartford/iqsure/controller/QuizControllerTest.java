package org.hartford.iqsure.controller;

import org.hartford.iqsure.dto.response.QuizResponseDTO;
import org.hartford.iqsure.service.QuizService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * UNIT TEST CONCEPT: Controller Testing
 * We test the Controller independently of the Service by Mocking the QuizService.
 * We want to verify the API returns the correct HTTP status (200 OK) and data.
 */
@ExtendWith(MockitoExtension.class)
public class QuizControllerTest {

    // Create a fake Service so we don't run any real business logic.
    @Mock
    private QuizService quizServiceMock;

    // Inject the fake service into our Controller.
    @InjectMocks
    private QuizController quizController;

    @Test
    public void testGetAllQuizzes() {
        // --- ARRANGE ---
        QuizResponseDTO quizDTO = QuizResponseDTO.builder()
                .quizId(1L)
                .title("Sample Quiz")
                .build();
        // Mock the service to return a list containing our fake quiz DTO.
        when(quizServiceMock.getAllQuizzes()).thenReturn(java.util.List.of(quizDTO));

        // --- ACT ---
        // Call the controller method (passing null for category since it's optional)
        ResponseEntity<List<QuizResponseDTO>> response = quizController.getAll(null);

        // --- ASSERT ---
        assertEquals(200, response.getStatusCode().value()); // Verify HTTP 200 OK status
        assertEquals(1, response.getBody().size()); // List should contain 1 item
        assertEquals("Sample Quiz", response.getBody().get(0).getTitle()); // Data should match
        verify(quizServiceMock).getAllQuizzes(); // Verify the service was called
    }

    @Test
    public void testGetById() {
        // --- ARRANGE ---
        QuizResponseDTO quizDTO = QuizResponseDTO.builder()
                .quizId(1L)
                .title("Sample Quiz")
                .build();
        when(quizServiceMock.getQuizById(1L)).thenReturn(quizDTO);

        // --- ACT ---
        ResponseEntity<QuizResponseDTO> response = quizController.getById(1L);

        // --- ASSERT ---
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Sample Quiz", response.getBody().getTitle());
        verify(quizServiceMock).getQuizById(1L);
    }
}
