/*
 * FILE: UserPolicyService.java | LOCATION: service/
 * PURPOSE: Handles policy purchase logic. When user buys a policy, this service:
 *          1. Calls PremiumCalculationService to get discounted price
 *          2. Creates a UserPolicy record with the final premium
 * CALLED BY: UserPolicyController.java
 * USES: UserRepository, PolicyRepository, UserPolicyRepository, PremiumCalculationService
 */
package org.hartford.iqsure.service;

import lombok.RequiredArgsConstructor;
import org.hartford.iqsure.dto.request.InsuredMemberRequestDTO;
import org.hartford.iqsure.dto.request.UserPolicyRequestDTO;
import org.hartford.iqsure.dto.response.InsuredMemberResponseDTO;
import org.hartford.iqsure.dto.response.PremiumBreakdownDTO;
import org.hartford.iqsure.dto.response.UserPolicyResponseDTO;
import org.hartford.iqsure.entity.InsuredMember;
import org.hartford.iqsure.entity.Policy;
import org.hartford.iqsure.entity.User;
import org.hartford.iqsure.entity.UserPolicy;
import org.hartford.iqsure.exception.BadRequestException;
import org.hartford.iqsure.exception.ResourceNotFoundException;
import org.hartford.iqsure.repository.PolicyRepository;
import org.hartford.iqsure.repository.UserPolicyRepository;
import org.hartford.iqsure.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPolicyService {

    private final UserRepository userRepository;
    private final PolicyRepository policyRepository;
    private final UserPolicyRepository userPolicyRepository;
    private final PremiumCalculationService premiumCalculationService;
    private final NotificationService notificationService;

    @Transactional
    public UserPolicyResponseDTO purchasePolicy(Long userId, UserPolicyRequestDTO dto, List<Long> selectedRewardIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Policy policy = policyRepository.findById(dto.getPolicyId())
                .orElseThrow(() -> new ResourceNotFoundException("Policy not found: " + dto.getPolicyId()));

        if (!policy.getIsActive()) {
            throw new BadRequestException("Policy is not currently active: " + policy.getTitle());
        }

        // Calculate premium using gamification discounts + selected coupon rewards
        PremiumBreakdownDTO breakdown = premiumCalculationService.calculatePremium(userId, dto.getPolicyId(), selectedRewardIds);

        UserPolicy userPolicy = UserPolicy.builder()
                .user(user)
                .policy(policy)
                .finalPremium(breakdown.getFinalPremium())
                .discountApplied(breakdown.getTotalDiscountPercent())
                .purchaseDate(LocalDateTime.now())
                .status(UserPolicy.PolicyStatus.PENDING_UNDERWRITING) // Default to PENDING_UNDERWRITING for the pipeline flow
                .remainingCoverage(java.math.BigDecimal.valueOf(policy.getCoverageAmount()))
                .nomineeName(dto.getNomineeName())
                .nomineeRelationship(dto.getNomineeRelationship())
                .healthReportPath(dto.getHealthReportPath())
                .build();

        if (dto.getInsuredMembers() != null) {
            for (InsuredMemberRequestDTO m : dto.getInsuredMembers()) {
                userPolicy.getInsuredMembers().add(InsuredMember.builder()
                        .userPolicy(userPolicy)
                        .fullName(m.getFullName())
                        .relationship(m.getRelationship())
                        .dateOfBirth(m.getDateOfBirth())
                        .gender(m.getGender())
                        .preExistingConditions(m.getPreExistingConditions())
                        .build());
            }
        }

        UserPolicy saved = userPolicyRepository.save(userPolicy);
        UserPolicyResponseDTO result = toDTO(saved);

        // Mark the selected rewards as used so they can't be applied to another policy
        premiumCalculationService.markRewardsAsUsed(selectedRewardIds);

        // Notify Admins
        notificationService.createNotificationForAdmins(
                "New policy requested by " + user.getName() + " for " + policy.getTitle(),
                org.hartford.iqsure.entity.Notification.NotificationType.POLICY_REQUESTED,
                saved.getId(),
                "/admin/assign-uw"
        );

        return result;
    }

    public List<UserPolicyResponseDTO> getUserPolicies(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found: " + userId);
        }
        return userPolicyRepository.findByUser_UserId(userId)
                .stream().map(this::toDTO).toList();
    }

    public UserPolicyResponseDTO getUserPolicyById(Long userId, Long userPolicyId) {
        UserPolicy up = userPolicyRepository.findById(userPolicyId)
                .orElseThrow(() -> new ResourceNotFoundException("UserPolicy not found: " + userPolicyId));
        if (!up.getUser().getUserId().equals(userId) && !up.getUser().getRole().equals(User.Role.ROLE_ADMIN)) {
            throw new BadRequestException("This policy does not belong to the specified user.");
        }
        return toDTO(up);
    }

    public List<UserPolicyResponseDTO> getAllUserPolicies() {
        return userPolicyRepository.findAll().stream().map(this::toDTO).toList();
    }

    public List<UserPolicyResponseDTO> getPoliciesByStatus(UserPolicy.PolicyStatus status) {
        return userPolicyRepository.findByStatus(status).stream().map(this::toDTO).toList();
    }

    public List<UserPolicyResponseDTO> getPoliciesByUnderwriterAndStatus(Long underwriterId, UserPolicy.PolicyStatus status) {
        return userPolicyRepository.findByAssignedUnderwriter_UserIdAndStatus(underwriterId, status)
                .stream().map(this::toDTO).toList();
    }

    public List<UserPolicyResponseDTO> getPoliciesByUnderwriter(Long underwriterId) {
        return userPolicyRepository.findByAssignedUnderwriter_UserId(underwriterId)
                .stream().map(this::toDTO).toList();
    }

    @Transactional
    public UserPolicyResponseDTO assignUnderwriter(Long userPolicyId, Long underwriterId) {
        UserPolicy up = userPolicyRepository.findById(userPolicyId)
                .orElseThrow(() -> new ResourceNotFoundException("UserPolicy not found: " + userPolicyId));
        User underwriter = userRepository.findById(underwriterId)
                .orElseThrow(() -> new ResourceNotFoundException("Underwriter not found: " + underwriterId));

        if (underwriter.getRole() != User.Role.ROLE_UNDERWRITER) {
            throw new BadRequestException("User is not an underwriter");
        }

        up.setAssignedUnderwriter(underwriter);
        up.setAssignedAt(LocalDateTime.now());
        up.setStatus(UserPolicy.PolicyStatus.UNDER_EVALUATION);

        UserPolicy saved = userPolicyRepository.save(up);

        // Notify Underwriter
        notificationService.createNotification(
                underwriterId,
                "You have been assigned to evaluate a policy request for " + up.getUser().getName(),
                org.hartford.iqsure.entity.Notification.NotificationType.UNDERWRITER_ASSIGNED,
                saved.getId(),
                "/underwriter/pending"
        );

        // Notify User
        notificationService.createNotification(
                up.getUser().getUserId(),
                "Your application for " + up.getPolicy().getTitle() + " has been assigned to an underwriter for evaluation.",
                org.hartford.iqsure.entity.Notification.NotificationType.POLICY_STATUS_UPDATE,
                saved.getId(),
                "/my-policies"
        );

        return toDTO(saved);
    }

    @Transactional
    public UserPolicyResponseDTO rejectPolicy(Long userPolicyId, String remarks) {
        UserPolicy up = userPolicyRepository.findById(userPolicyId)
                .orElseThrow(() -> new ResourceNotFoundException("UserPolicy not found: " + userPolicyId));

        up.setStatus(UserPolicy.PolicyStatus.REJECTED);
        up.setUnderwriterRemarks(remarks);

        UserPolicy saved = userPolicyRepository.save(up);

        // Notify User
        notificationService.createNotification(
                up.getUser().getUserId(),
                "Your application for " + up.getPolicy().getTitle() + " was not approved at this time. Reason: " + remarks,
                org.hartford.iqsure.entity.Notification.NotificationType.POLICY_STATUS_UPDATE,
                saved.getId(),
                "/my-policies"
        );

        return toDTO(saved);
    }

    public java.util.Map<String, Object> getUnderwriterStats(Long underwriterId) {
        User underwriter = userRepository.findById(underwriterId)
                .orElseThrow(() -> new ResourceNotFoundException("Underwriter not found: " + underwriterId));

        List<UserPolicy> myPolicies = userPolicyRepository.findByAssignedUnderwriter_UserId(underwriterId);

        long pendingAssignments = myPolicies.stream()
                .filter(p -> p.getStatus() == UserPolicy.PolicyStatus.UNDER_EVALUATION)
                .count();

        long quotesSentCount = myPolicies.stream()
                .filter(p -> p.getStatus() == UserPolicy.PolicyStatus.QUOTES_SENT || p.getStatus() == UserPolicy.PolicyStatus.ACTIVE)
                .count();

        long activePoliciesCount = myPolicies.stream()
                .filter(p -> p.getStatus() == UserPolicy.PolicyStatus.ACTIVE)
                .count();

        long customersServed = myPolicies.stream()
                .map(p -> p.getUser().getUserId())
                .distinct()
                .count();

        double totalPremium = myPolicies.stream()
                .filter(p -> p.getStatus() == UserPolicy.PolicyStatus.ACTIVE)
                .mapToDouble(UserPolicy::getFinalPremium)
                .sum();

        double commissionPercentage = underwriter.getCommissionPercentage() != null ? 
                underwriter.getCommissionPercentage().doubleValue() : 0.0;
        double commissionEarned = (totalPremium * commissionPercentage) / 100.0;

        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("pendingAssignments", pendingAssignments);
        stats.put("quotesSent", quotesSentCount);
        stats.put("activePolicies", activePoliciesCount);
        stats.put("customersServed", customersServed);
        stats.put("totalPremium", Math.round(totalPremium * 100.0) / 100.0);
        stats.put("commissionEarned", Math.round(commissionEarned * 100.0) / 100.0);

        return stats;
    }


    @Transactional
    public UserPolicyResponseDTO sendQuote(Long userPolicyId, java.math.BigDecimal quoteAmount, String remarks) {
        UserPolicy up = userPolicyRepository.findById(userPolicyId)
                .orElseThrow(() -> new ResourceNotFoundException("UserPolicy not found: " + userPolicyId));

        up.setQuoteAmount(quoteAmount);
        up.setUnderwriterRemarks(remarks);
        up.setStatus(UserPolicy.PolicyStatus.QUOTES_SENT);

        // Update underwriter stats
        User underwriter = up.getAssignedUnderwriter();
        if (underwriter != null) {
            underwriter.setTotalQuotesSent((underwriter.getTotalQuotesSent() != null ? underwriter.getTotalQuotesSent() : 0) + 1);
            userRepository.save(underwriter);
        }

        UserPolicy saved = userPolicyRepository.save(up);

        // Notify User
        notificationService.createNotification(
                up.getUser().getUserId(),
                "Good news! Your quote for " + up.getPolicy().getTitle() + " is ready for review.",
                org.hartford.iqsure.entity.Notification.NotificationType.QUOTE_RECEIVED,
                saved.getId(),
                "/my-policies"
        );

        return toDTO(saved);
    }

    @Transactional
    public UserPolicyResponseDTO payPolicy(Long userPolicyId) {
        UserPolicy up = userPolicyRepository.findById(userPolicyId)
                .orElseThrow(() -> new ResourceNotFoundException("UserPolicy not found: " + userPolicyId));

        if (up.getStatus() != UserPolicy.PolicyStatus.QUOTES_SENT) {
            throw new BadRequestException("Policy must have a quote sent before it can be paid.");
        }

        up.setStatus(UserPolicy.PolicyStatus.ACTIVE);
        UserPolicy saved = userPolicyRepository.save(up);

        // Notify User
        notificationService.createNotification(
                up.getUser().getUserId(),
                "Payment successful! Your policy " + up.getPolicy().getTitle() + " is now ACTIVE.",
                org.hartford.iqsure.entity.Notification.NotificationType.POLICY_STATUS_UPDATE,
                saved.getId(),
                "/my-policies"
        );

        return toDTO(saved);
    }

    @Transactional
    public UserPolicyResponseDTO activatePolicy(Long userPolicyId) {
        UserPolicy up = userPolicyRepository.findById(userPolicyId)
                .orElseThrow(() -> new ResourceNotFoundException("UserPolicy not found: " + userPolicyId));

        up.setStatus(UserPolicy.PolicyStatus.ACTIVE);
        UserPolicy saved = userPolicyRepository.save(up);

        // Notify User
        notificationService.createNotification(
                up.getUser().getUserId(),
                "Your policy " + up.getPolicy().getTitle() + " has been manually activated and is now in effect.",
                org.hartford.iqsure.entity.Notification.NotificationType.POLICY_STATUS_UPDATE,
                saved.getId(),
                "/my-policies"
        );

        return toDTO(saved);
    }

    private UserPolicyResponseDTO toDTO(UserPolicy up) {
        double saved = Math.round((up.getPolicy().getBasePremium() - up.getFinalPremium()) * 100.0) / 100.0;
        return UserPolicyResponseDTO.builder()
                .id(up.getId())
                .userId(up.getUser().getUserId())
                .userName(up.getUser().getName())
                .policyId(up.getPolicy().getPolicyId())
                .policyTitle(up.getPolicy().getTitle())
                .policyType(up.getPolicy().getPolicyType())
                .basePremium(up.getPolicy().getBasePremium())
                .coverageAmount(up.getPolicy().getCoverageAmount())
                .durationMonths(up.getPolicy().getDurationMonths())
                .finalPremium(up.getFinalPremium())
                .discountApplied(up.getDiscountApplied())
                .purchaseDate(up.getPurchaseDate())
                .status(up.getStatus())
                .savedAmount(saved)
                .assignedUnderwriterId(up.getAssignedUnderwriter() != null ? up.getAssignedUnderwriter().getUserId() : null)
                .assignedUnderwriterName(up.getAssignedUnderwriter() != null ? up.getAssignedUnderwriter().getName() : null)
                .assignedAt(up.getAssignedAt())
                .quoteAmount(up.getQuoteAmount())
                .underwriterRemarks(up.getUnderwriterRemarks())
                .totalClaimedAmount(up.getTotalClaimedAmount())
                .remainingCoverage(up.getRemainingCoverage())
                .nomineeName(up.getNomineeName())
                .nomineeRelationship(up.getNomineeRelationship())
                .healthReportPath(up.getHealthReportPath())
                .insuredMembers(up.getInsuredMembers() != null ? up.getInsuredMembers().stream().map(m -> InsuredMemberResponseDTO.builder()
                        .id(m.getId())
                        .fullName(m.getFullName())
                        .relationship(m.getRelationship())
                        .dateOfBirth(m.getDateOfBirth())
                        .gender(m.getGender())
                        .preExistingConditions(m.getPreExistingConditions())
                        .build()).toList() : null)
                .build();
    }
}
