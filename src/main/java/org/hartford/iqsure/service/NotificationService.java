package org.hartford.iqsure.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hartford.iqsure.entity.Notification;
import org.hartford.iqsure.entity.User;
import org.hartford.iqsure.repository.NotificationRepository;
import org.hartford.iqsure.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    // One SSE emitter per logged-in user ID
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long userId) {
        // 1 hour timeout — frontend reconnects on timeout
        SseEmitter emitter = new SseEmitter(3600_000L);
        emitters.put(userId, emitter);
        
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));
        emitter.onError(e -> emitters.remove(userId));

        // Send a ping immediately so browser confirms connection
        try {
            emitter.send(SseEmitter.event().name("ping").data("connected"));
        } catch (IOException e) {
            emitters.remove(userId);
        }
        return emitter;
    }

    @Transactional
    public void createNotification(Long userId, String message, Notification.NotificationType type, Long relatedId, String targetUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = Notification.builder()
                .recipient(user)
                .message(message)
                .type(type)
                .relatedId(relatedId)
                .targetUrl(targetUrl)
                .build();

        notificationRepository.save(notification);

        // Push via SSE if user is connected
        SseEmitter emitter = emitters.get(userId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(notification));
            } catch (IOException e) {
                log.warn("SSE push failed for user {}: {}", userId, e.getMessage());
                emitters.remove(userId);
            }
        }
    }

    @Transactional
    public void createNotificationForAdmins(String message, Notification.NotificationType type, Long relatedId, String targetUrl) {
        List<User> admins = userRepository.findAll().stream()
                .filter(u -> u.getRole() == User.Role.ROLE_ADMIN)
                .toList();

        for (User admin : admins) {
            createNotification(admin.getUserId(), message, type, relatedId, targetUrl);
        }
    }

    public List<Notification> getNotificationsForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user);
    }

    public long getUnreadCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.countByRecipientAndReadFalse(user);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Notification> unread = notificationRepository.findByRecipientOrderByCreatedAtDesc(user).stream()
                .filter(n -> !n.isRead())
                .toList();
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }
}
