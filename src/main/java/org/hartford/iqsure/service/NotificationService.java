package org.hartford.iqsure.service;

import lombok.RequiredArgsConstructor;
import org.hartford.iqsure.entity.Notification;
import org.hartford.iqsure.entity.User;
import org.hartford.iqsure.repository.NotificationRepository;
import org.hartford.iqsure.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

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
        return notificationRepository.countByRecipientAndIsReadFalse(user);
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
