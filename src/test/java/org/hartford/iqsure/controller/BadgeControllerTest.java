package org.hartford.iqsure.controller;

import org.hartford.iqsure.dto.request.BadgeRequestDTO;
import org.hartford.iqsure.dto.response.BadgeResponseDTO;
import org.hartford.iqsure.service.BadgeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BadgeControllerTest {

    @Mock
    private BadgeService badgeService;

    @InjectMocks
    private BadgeController badgeController;

    @Test
    public void testCreate() {
        // --- ARRANGE ---
        BadgeRequestDTO request = new BadgeRequestDTO();
        request.setName("Rookie");
        BadgeResponseDTO responseDTO = BadgeResponseDTO.builder().badgeId(1L).name("Rookie").build();
        when(badgeService.createBadge(any(BadgeRequestDTO.class))).thenReturn(responseDTO);

        // --- ACT ---
        ResponseEntity<BadgeResponseDTO> response = badgeController.create(request);

        // --- ASSERT ---
        assertEquals(201, response.getStatusCode().value());
        assertEquals("Rookie", response.getBody().getName());
        verify(badgeService).createBadge(any(BadgeRequestDTO.class));
    }

    @Test
    public void testGetAll() {
        // --- ARRANGE ---
        BadgeResponseDTO dto = BadgeResponseDTO.builder().badgeId(1L).name("All").build();
        when(badgeService.getAllBadges()).thenReturn(List.of(dto));

        // --- ACT ---
        ResponseEntity<List<BadgeResponseDTO>> response = badgeController.getAll();

        // --- ASSERT ---
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testGetByUser() {
        // --- ARRANGE ---
        BadgeResponseDTO dto = BadgeResponseDTO.builder().badgeId(1L).name("UserBadge").build();
        when(badgeService.getBadgesByUser(1L)).thenReturn(List.of(dto));

        // --- ACT ---
        ResponseEntity<List<BadgeResponseDTO>> response = badgeController.getByUser(1L);

        // --- ASSERT ---
        assertEquals(200, response.getStatusCode().value());
        assertEquals("UserBadge", response.getBody().get(0).getName());
    }

    @Test
    public void testDelete() {
        // --- ACT ---
        ResponseEntity<Void> response = badgeController.delete(1L);

        // --- ASSERT ---
        assertEquals(204, response.getStatusCode().value());
        verify(badgeService).deleteBadge(1L);
    }
}
