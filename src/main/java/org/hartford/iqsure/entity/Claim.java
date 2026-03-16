package org.hartford.iqsure.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "claims")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String claimNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_policy_id", nullable = false)
    private UserPolicy userPolicy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private ClaimType type;

    private BigDecimal amount;
    private BigDecimal approvedAmount;
    private BigDecimal settlementAmount;

    private String hospitalName;
    private LocalDate incidentDate;
    private String diagnosis;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ClaimStatus status = ClaimStatus.SUBMITTED;

    @Column(columnDefinition = "TEXT")
    private String rejectionReason;

    // =================== CLAIMS OFFICER FIELDS ===================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_officer_id")
    private User assignedOfficer;

    private LocalDateTime reviewStartedAt;
    private LocalDateTime reviewedAt;

    @Column(columnDefinition = "TEXT")
    private String reviewerRemarks;

    // =================== SETTLEMENT FIELDS ===================

    private LocalDate settlementDate;

    // =================== TIMESTAMPS ===================

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum ClaimType {
        CASHLESS,
        REIMBURSEMENT,
        ACCIDENTAL
    }

    public enum ClaimStatus {
        SUBMITTED,
        UNDER_REVIEW,
        APPROVED,
        PARTIAL_APPROVED,
        SETTLED,
        REJECTED
    }
}
