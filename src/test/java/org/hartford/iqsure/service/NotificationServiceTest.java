package org.hartford.iqsure.service;

import org.hartford.iqsure.entity.Notification;
import org.hartford.iqsure.entity.User;
import org.hartford.iqsure.repository.NotificationRepository;
import org.hartford.iqsure.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    public void testCreateNotification() {
        // --- ARRANGE ---
        User user = User.builder().userId(1L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // --- ACT ---
        notificationService.createNotification(1L, "Hello", Notification.NotificationType.QUIZ_COMPLETED, null, "/quizzes");

        // --- ASSERT ---
        verify(notificationRepository).save(any(Notification.class));
    }

    @Test
    public void testCreateNotificationForAdmins() {
        // --- ARRANGE ---
        User admin1 = User.builder().userId(1L).role(User.Role.ROLE_ADMIN).build();
        User admin2 = User.builder().userId(2L).role(User.Role.ROLE_ADMIN).build();
        User regularUser = User.builder().userId(3L).role(User.Role.ROLE_USER).build();

        when(userRepository.findAll()).thenReturn(List.of(admin1, admin2, regularUser));
        when(userRepository.findById(1L)).thenReturn(Optional.of(admin1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin2));

        // --- ACT ---
        notificationService.createNotificationForAdmins("Broadcast", Notification.NotificationType.SYSTEM, null, "/");

        // --- ASSERT ---
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }

    @Test
    public void testMarkAsRead() {
        // --- ARRANGE ---
        Notification notification = Notification.builder().id(1L).isRead(false).build();
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        // --- ACT ---
        notificationService.markAsRead(1L);

        // --- ASSERT ---
        assertEquals(true, notification.isRead());
        verify(notificationRepository).save(notification);
    }
}
