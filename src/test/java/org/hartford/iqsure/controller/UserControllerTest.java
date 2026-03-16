package org.hartford.iqsure.controller;

import org.hartford.iqsure.dto.response.LeaderboardEntryDTO;
import org.hartford.iqsure.dto.response.UserResponseDTO;
import org.hartford.iqsure.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    public void testGetAll() {
        // --- ARRANGE ---
        UserResponseDTO user = UserResponseDTO.builder().userId(1L).name("Admin").build();
        when(userService.getAllUsers()).thenReturn(List.of(user));

        // --- ACT ---
        ResponseEntity<List<UserResponseDTO>> response = userController.getAll();

        // --- ASSERT ---
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        verify(userService).getAllUsers();
    }

    @Test
    public void testGetProfile() {
        // --- ARRANGE ---
        UserResponseDTO user = UserResponseDTO.builder().userId(1L).name("User").build();
        when(userService.getProfile(1L)).thenReturn(user);

        // --- ACT ---
        ResponseEntity<UserResponseDTO> response = userController.getProfile(1L);

        // --- ASSERT ---
        assertEquals(200, response.getStatusCode().value());
        assertEquals("User", response.getBody().getName());
        verify(userService).getProfile(1L);
    }

    @Test
    public void testLeaderboard() {
        // --- ARRANGE ---
        LeaderboardEntryDTO entry = LeaderboardEntryDTO.builder().name("Leader").rank(1).build();
        when(userService.getLeaderboard()).thenReturn(List.of(entry));

        // --- ACT ---
        ResponseEntity<List<LeaderboardEntryDTO>> response = userController.leaderboard();

        // --- ASSERT ---
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("Leader", response.getBody().get(0).getName());
        verify(userService).getLeaderboard();
    }

    @Test
    public void testDelete() {
        // --- ACT ---
        ResponseEntity<Void> response = userController.delete(1L);

        // --- ASSERT ---
        assertEquals(204, response.getStatusCode().value());
        verify(userService).deleteUser(1L);
    }
}
