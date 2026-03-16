package org.hartford.iqsure.controller;

import org.hartford.iqsure.entity.Notification;
import org.hartford.iqsure.service.NotificationService;
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
public class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    @Test
    public void testGetNotifications() {
        // --- ARRANGE ---
        Notification n = Notification.builder().id(1L).message("Note").build();
        when(notificationService.getNotificationsForUser(1L)).thenReturn(List.of(n));

        // --- ACT ---
        ResponseEntity<List<Notification>> response = notificationController.getNotifications(1L);

        // --- ASSERT ---
        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        verify(notificationService).getNotificationsForUser(1L);
    }

    @Test
    public void testGetUnreadCount() {
        // --- ARRANGE ---
        when(notificationService.getUnreadCount(1L)).thenReturn(5L);

        // --- ACT ---
        ResponseEntity<Long> response = notificationController.getUnreadCount(1L);

        // --- ASSERT ---
        assertEquals(200, response.getStatusCode().value());
        assertEquals(5L, response.getBody());
    }

    @Test
    public void testMarkAsRead() {
        // --- ACT ---
        ResponseEntity<Void> response = notificationController.markAsRead(1L);

        // --- ASSERT ---
        assertEquals(200, response.getStatusCode().value());
        verify(notificationService).markAsRead(1L);
    }
}
