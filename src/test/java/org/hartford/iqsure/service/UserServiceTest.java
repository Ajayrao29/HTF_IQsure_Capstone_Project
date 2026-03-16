package org.hartford.iqsure.service;

import org.hartford.iqsure.dto.auth.AuthRequest;
import org.hartford.iqsure.dto.auth.AuthResponse;
import org.hartford.iqsure.dto.request.UserRequestDTO;
import org.hartford.iqsure.dto.response.LeaderboardEntryDTO;
import org.hartford.iqsure.dto.response.UserResponseDTO;
import org.hartford.iqsure.entity.User;
import org.hartford.iqsure.exception.BadRequestException;
import org.hartford.iqsure.exception.ResourceNotFoundException;
import org.hartford.iqsure.repository.QuizAttemptRepository;
import org.hartford.iqsure.repository.UserRepository;
import org.hartford.iqsure.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UNIT TEST STYLE: Service Testing with Mockito
 * We test the business logic of UserService in isolation.
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private QuizAttemptRepository attemptRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    @Test
    public void testRegister_Successful() {
        // --- ARRANGE ---
        UserRequestDTO dto = new UserRequestDTO();
        dto.setName("John Doe");
        dto.setEmail("john@example.com");
        dto.setPassword("password123");

        User savedUser = User.builder()
                .userId(1L)
                .name("John Doe")
                .email("john@example.com")
                .password("hashed_password")
                .role(User.Role.ROLE_USER)
                .build();

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtUtil.generateToken(any(), any(), any())).thenReturn("mock_token");

        // --- ACT ---
        AuthResponse response = userService.register(dto);

        // --- ASSERT ---
        assertNotNull(response);
        assertEquals(" जॉन डो", response.getName().trim());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("mock_token", response.getToken());
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testRegister_EmailExists_ThrowsException() {
        // --- ARRANGE ---
        UserRequestDTO dto = new UserRequestDTO();
        dto.setName("John");
        dto.setEmail("exists@example.com");
        dto.setPassword("password");

        when(userRepository.existsByEmail("exists@example.com")).thenReturn(true);

        // --- ACT & ASSERT ---
        assertThrows(BadRequestException.class, () -> userService.register(dto));
    }

    @Test
    public void testLogin_Successful() {
        // --- ARRANGE ---
        AuthRequest request = new AuthRequest();
        request.setEmail("john@example.com");
        request.setPassword("password123");

        User user = User.builder()
                .userId(1L)
                .name("John Doe")
                .email("john@example.com")
                .password("hashed_password")
                .role(User.Role.ROLE_USER)
                .build();

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashed_password")).thenReturn(true);
        when(jwtUtil.generateToken(any(), any(), any())).thenReturn("mock_token");

        // --- ACT ---
        AuthResponse response = userService.login(request);

        // --- ASSERT ---
        assertNotNull(response);
        assertEquals(" जॉन डो", response.getName().trim());
        assertEquals("mock_token", response.getToken());
    }

    @Test
    public void testLogin_InvalidPassword_ThrowsException() {
        // --- ARRANGE ---
        AuthRequest request = new AuthRequest();
        request.setEmail("john@example.com");
        request.setPassword("wrong_password");

        User user = User.builder()
                .userId(1L)
                .password("hashed_password")
                .build();

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong_password", "hashed_password")).thenReturn(false);

        // --- ACT & ASSERT ---
        assertThrows(BadRequestException.class, () -> userService.login(request));
    }

    @Test
    public void testGetProfile_Successful() {
        // --- ARRANGE ---
        User user = User.builder()
                .userId(1L)
                .name("John Doe")
                .email("john@example.com")
                .role(User.Role.ROLE_USER)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // --- ACT ---
        UserResponseDTO result = userService.getProfile(1L);

        // --- ASSERT ---
        assertNotNull(result);
        assertEquals("john@example.com", result.getEmail());
    }

    @Test
    public void testGetProfile_NotFound_ThrowsException() {
        // --- ARRANGE ---
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // --- ACT & ASSERT ---
        assertThrows(ResourceNotFoundException.class, () -> userService.getProfile(99L));
    }

    @Test
    public void testGetLeaderboard() {
        // --- ARRANGE ---
        User u1 = User.builder().userId(1L).name("User1").userPoints(100).build();
        User u2 = User.builder().userId(2L).name("User2").userPoints(50).build();

        when(userRepository.findTopUsersByPoints()).thenReturn(List.of(u1, u2));
        when(attemptRepository.countByUser_UserId(1L)).thenReturn(5L);
        when(attemptRepository.countByUser_UserId(2L)).thenReturn(2L);

        // --- ACT ---
        List<LeaderboardEntryDTO> leaderboard = userService.getLeaderboard();

        // --- ASSERT ---
        assertEquals(2, leaderboard.size());
        assertEquals(1, leaderboard.get(0).getRank());
        assertEquals("User1", leaderboard.get(0).getName());
        assertEquals(5, leaderboard.get(0).getQuizzesAttempted());
    }
}
