package org.hartford.iqsure.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User recipient;

    public Long getRecipientId() {
        return recipient != null ? recipient.getUserId() : null;
    }

    @Column(nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    private Long relatedId; // ID of the policy or claim to reference

    private String targetUrl; // Frontend URL to redirect to

    @Builder.Default
    @JsonProperty("read")
    private boolean isRead = false;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum NotificationType {
        POLICY_REQUESTED,      // Admin gets this
        UNDERWRITER_ASSIGNED,  // Underwriter gets this
        QUOTE_RECEIVED,        // User gets this
        POLICY_STATUS_UPDATE,  // User gets this (Active/Rejected)
        CLAIM_FILED,           // Admin gets this
        CLAIM_ASSIGNED,        // Claims Officer gets this
        CLAIM_STATUS_UPDATE,   // User gets this
        GENERAL
    }
}
