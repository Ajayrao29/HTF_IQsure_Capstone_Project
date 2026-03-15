package org.hartford.iqsure.controller;

import org.hartford.iqsure.dto.response.EducationContentDTO;
import org.hartford.iqsure.service.EducationContentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * UNIT TEST CONCEPT: Controller layer testing.
 * We ensure the Controller talks to the Service correctly 
 * and returns the proper HTTP status codes.
 */
@ExtendWith(MockitoExtension.class)
public class EducationContentControllerTest {

    // Fake the service layer.
    @Mock
    private EducationContentService educationContentServiceMock;

    // Inject the fake service into the controller.
    @InjectMocks
    private EducationContentController educationContentController;

    @Test
    public void testGetByLanguage() {
        // --- ARRANGE ---
        EducationContentDTO contentDTO = EducationContentDTO.builder()
                .title("Mock Title")
                .language("en")
                .build();
        // Tell Mock Service to return our fake DTO list.
        when(educationContentServiceMock.getByLanguage("en")).thenReturn(java.util.List.of(contentDTO));

        // --- ACT ---
        // Run the controller's GET request logic
        ResponseEntity<List<EducationContentDTO>> response = educationContentController.getByLanguage("en");

        // --- ASSERT ---
        assertEquals(200, response.getStatusCode().value()); // Status should be 200 OK
        assertEquals(1, response.getBody().size()); // Logic should return 1 item
        assertEquals("Mock Title", response.getBody().get(0).getTitle()); // Content should match
        verify(educationContentServiceMock).getByLanguage("en"); // Verify service interaction
    }

    @Test
    public void testGetByTopicAndLanguage() {
        // --- ARRANGE ---
        EducationContentDTO contentDTO = EducationContentDTO.builder()
                .topic("insurance")
                .title("Insurance Basics")
                .build();
        when(educationContentServiceMock.getByTopicAndLanguage("insurance", "en")).thenReturn(contentDTO);

        // --- ACT ---
        ResponseEntity<EducationContentDTO> response = educationContentController.getByTopicAndLanguage("insurance", "en");

        // --- ASSERT ---
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Insurance Basics", response.getBody().getTitle());
        verify(educationContentServiceMock).getByTopicAndLanguage("insurance", "en");
    }
}
