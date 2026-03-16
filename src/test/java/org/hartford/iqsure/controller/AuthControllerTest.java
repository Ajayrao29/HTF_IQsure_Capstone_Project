package org.hartford.iqsure.controller;

import org.hartford.iqsure.dto.auth.AuthRequest;
import org.hartford.iqsure.dto.auth.AuthResponse;
import org.hartford.iqsure.dto.request.UserRequestDTO;
import org.hartford.iqsure.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    @Test
    public void testRegister() {
        // --- ARRANGE ---
        UserRequestDTO dto = new UserRequestDTO();
        AuthResponse responseDTO = AuthResponse.builder().userId(1L).token("token").build();
        when(userService.register(any(UserRequestDTO.class))).thenReturn(responseDTO);

        // --- ACT ---
        ResponseEntity<AuthResponse> response = authController.register(dto);

        // --- ASSERT ---
        assertEquals(201, response.getStatusCode().value());
        assertEquals("token", response.getBody().getToken());
    }

    @Test
    public void testLogin() {
        // --- ARRANGE ---
        AuthRequest request = new AuthRequest();
        AuthResponse responseDTO = AuthResponse.builder().userId(1L).token("login_token").build();
        when(userService.login(any(AuthRequest.class))).thenReturn(responseDTO);

        // --- ACT ---
        ResponseEntity<AuthResponse> response = authController.login(request);

        // --- ASSERT ---
        assertEquals(200, response.getStatusCode().value());
        assertEquals("login_token", response.getBody().getToken());
    }
}
