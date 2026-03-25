package org.hartford.iqsure.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hartford.iqsure.dto.auth.AuthRequest;
import org.hartford.iqsure.dto.auth.AuthResponse;
import org.hartford.iqsure.dto.request.UserRequestDTO;
import org.hartford.iqsure.dto.response.LeaderboardEntryDTO;
import org.hartford.iqsure.dto.response.UserResponseDTO;
import org.hartford.iqsure.entity.Notification;
import org.hartford.iqsure.entity.PasswordResetToken;
import org.hartford.iqsure.entity.User;
import org.hartford.iqsure.exception.BadRequestException;
import org.hartford.iqsure.exception.ResourceNotFoundException;
import org.hartford.iqsure.repository.PasswordResetTokenRepository;
import org.hartford.iqsure.repository.QuizAttemptRepository;
import org.hartford.iqsure.repository.UserRepository;
import org.hartford.iqsure.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Service for managing users, authentication, and leaderboard.
 * Best Practice: Separation of concerns - this layer contains business logic.
 */
@Slf4j // Best Practice: Logging for better traceability
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final QuizAttemptRepository attemptRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final NotificationService notificationService;
    private final PasswordResetTokenRepository tokenRepository;

    private static final BigDecimal DEFAULT_APPROVAL_LIMIT = new BigDecimal("500000.00");

    @Transactional
    public AuthResponse register(UserRequestDTO dto) {
        log.info("Registering new user with email: {}", dto.getEmail());

        if (userRepository.existsByEmail(dto.getEmail())) {
            log.warn("Registration failed: Email {} already exists", dto.getEmail());
            throw new BadRequestException("Email already registered");
        }

        User user = User.builder()
                .name(dto.getName() != null ? dto.getName().trim() : "")
                .email(dto.getEmail().trim().toLowerCase())
                .password(passwordEncoder.encode(dto.getPassword()))
                .phone(dto.getPhone() != null ? dto.getPhone().trim() : null)
                .userPoints(0)
                .role(User.Role.ROLE_USER)
                .build();

        user = userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getUserId());

        return buildAuthResponse(user, token);
    }

    public AuthResponse login(AuthRequest request) {
        log.info("Attempting login for user: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> {
                    log.warn("Login failed: User {} not found", request.getEmail());
                    return new BadRequestException("Invalid email or password");
                });

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed: Incorrect password for user {}", request.getEmail());
            throw new BadRequestException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getUserId());

        return buildAuthResponse(user, token);
    }

    public UserResponseDTO getProfile(Long userId) {
        return userRepository.findById(userId)
                .map(this::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    @Transactional
    public UserResponseDTO updateProfile(Long userId, UserRequestDTO dto) {
        log.info("Updating profile for user ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        user.setName(dto.getName());
        user.setPhone(dto.getPhone());
        user.setCity(dto.getCity());
        user.setState(dto.getState());
        user.setZipCode(dto.getZipCode());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            notificationService.createNotification(userId, "Security Alert: Your password has been updated.", Notification.NotificationType.GENERAL, null, "/profile");
        }

        user = userRepository.save(user);
        notificationService.createNotification(userId, "Profile details updated successfully!", Notification.NotificationType.GENERAL, null, "/profile");
        return toDTO(user);
    }

    @Transactional
    public void forgotPassword(String email) {
        log.info("Processing forgot password for email: {}", email);
        
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new BadRequestException("No account found with that email address"));

        String otp = String.format("%06d", new Random().nextInt(999999));
        
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);

        PasswordResetToken token = PasswordResetToken.builder()
                .token(otp)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(10))
                .build();
        tokenRepository.save(token);

        emailService.sendOtpEmail(email, otp);
        notificationService.createNotification(user.getUserId(), "A password reset request was initiated. Use OTP: " + otp, Notification.NotificationType.GENERAL, null, "/reset-password");
    }

    @Transactional
    public void resetPassword(String otp, String newPassword) {
        log.info("Resetting password with OTP");
        
        PasswordResetToken resetToken = tokenRepository.findByToken(otp)
                .orElseThrow(() -> new BadRequestException("Invalid OTP"));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken);
            throw new BadRequestException("OTP has expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(resetToken);
        notificationService.createNotification(user.getUserId(), "Your password has been reset successfully.", Notification.NotificationType.GENERAL, null, "/login");
    }

    public void deleteUser(Long userId) {
        log.info("Deleting user ID: {}", userId);
        userRepository.deleteById(userId);
    }

    @Transactional
    public UserResponseDTO updateStatus(Long userId, String status) {
        log.info("Updating status for user ID: {} to {}", userId, status);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        user.setStatus(status);
        return toDTO(userRepository.save(user));
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDTO).toList();
    }

    public List<UserResponseDTO> getUsersByRole(User.Role role) {
        return userRepository.findByRole(role).stream().map(this::toDTO).toList();
    }

    @Transactional
    public UserResponseDTO createUnderwriter(UserRequestDTO dto) {
        log.info("Creating new underwriter: {}", dto.getEmail());
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(User.Role.ROLE_UNDERWRITER)
                .licenseNumber(dto.getLicenseNumber())
                .specialization(dto.getSpecialization())
                .commissionPercentage(dto.getCommissionPercentage())
                .status("ACTIVE")
                .build();
        return toDTO(userRepository.save(user));
    }

    @Transactional
    public UserResponseDTO createClaimsOfficer(UserRequestDTO dto) {
        log.info("Creating new claims officer: {}", dto.getEmail());
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Email already exists");
        }
        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(User.Role.ROLE_CLAIMS_OFFICER)
                .employeeId(dto.getEmployeeId())
                .department(dto.getDepartment())
                .approvalLimit(dto.getApprovalLimit() != null ? dto.getApprovalLimit() : DEFAULT_APPROVAL_LIMIT)
                .status("ACTIVE")
                .build();
        return toDTO(userRepository.save(user));
    }

    public List<LeaderboardEntryDTO> getLeaderboard() {
        List<User> users = userRepository.findTopUsersByPoints();
        return IntStream.range(0, users.size())
                .mapToObj(i -> LeaderboardEntryDTO.builder()
                        .rank(i + 1)
                        .userId(users.get(i).getUserId())
                        .name(users.get(i).getName())
                        .userPoints(users.get(i).getUserPoints())
                        .quizzesAttempted(attemptRepository.countByUser_UserId(users.get(i).getUserId()))
                        .build())
                .toList();
    }

    private UserResponseDTO toDTO(User user) {
        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .userPoints(user.getUserPoints())
                .role(user.getRole().name())
                .licenseNumber(user.getLicenseNumber())
                .specialization(user.getSpecialization())
                .commissionPercentage(user.getCommissionPercentage())
                .totalQuotesSent(user.getTotalQuotesSent())
                .employeeId(user.getEmployeeId())
                .department(user.getDepartment())
                .approvalLimit(user.getApprovalLimit())
                .totalClaimsProcessed(user.getTotalClaimsProcessed())
                .totalClaimsApproved(user.getTotalClaimsApproved())
                .totalClaimsRejected(user.getTotalClaimsRejected())
                .city(user.getCity())
                .state(user.getState())
                .zipCode(user.getZipCode())
                .status(user.getStatus())
                .build();
    }

    private AuthResponse buildAuthResponse(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .licenseNumber(user.getLicenseNumber())
                .employeeId(user.getEmployeeId())
                .department(user.getDepartment())
                .approvalLimit(user.getApprovalLimit())
                .build();
    }
}
