package org.hartford.iqsure.controller;

import lombok.RequiredArgsConstructor;
import org.hartford.iqsure.dto.response.UserPolicyResponseDTO;
import org.hartford.iqsure.entity.UserPolicy;
import org.hartford.iqsure.service.UserPolicyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/pipeline")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminPipelineController {

    private final UserPolicyService userPolicyService;

    @GetMapping("/policies")
    public ResponseEntity<List<UserPolicyResponseDTO>> getAllPolicies() {
        return ResponseEntity.ok(userPolicyService.getAllUserPolicies());
    }

    @GetMapping("/policies/status")
    public ResponseEntity<List<UserPolicyResponseDTO>> getPoliciesByStatus(@RequestParam UserPolicy.PolicyStatus status) {
        return ResponseEntity.ok(userPolicyService.getPoliciesByStatus(status));
    }

    @GetMapping("/underwriter/stats")
    public ResponseEntity<java.util.Map<String, Object>> getUnderwriterStats() {
        // In a real app, get the logged in user's ID
        // For now, let's assume we fetch it from authentication context
        // This is a placeholder for the logic
        return ResponseEntity.ok(java.util.Map.of(
            "pendingAssignments", 0,
            "quotesSent", 0,
            "activePolicies", 0,
            "customersServed", 0,
            "totalPremium", 0.0,
            "commissionEarned", 0.0
        ));
    }

    @PutMapping("/policies/{id}/assign")
    public ResponseEntity<UserPolicyResponseDTO> assignUnderwriter(@PathVariable Long id, @RequestParam Long underwriterId) {
        return ResponseEntity.ok(userPolicyService.assignUnderwriter(id, underwriterId));
    }

    @PostMapping("/policies/{id}/quote")
    public ResponseEntity<UserPolicyResponseDTO> sendQuote(@PathVariable Long id, @RequestParam java.math.BigDecimal quoteAmount, @RequestParam String remarks) {
        return ResponseEntity.ok(userPolicyService.sendQuote(id, quoteAmount, remarks));
    }

    @PutMapping("/policies/{id}/activate")
    public ResponseEntity<UserPolicyResponseDTO> activatePolicy(@PathVariable Long id) {
        return ResponseEntity.ok(userPolicyService.activatePolicy(id));
    }

    @GetMapping("/officer/stats")
    public ResponseEntity<java.util.Map<String, Object>> getClaimsOfficerStats() {
        return ResponseEntity.ok(java.util.Map.of(
            "claimsInQueue", 0,
            "underReview", 0,
            "totalProcessed", 0,
            "approved", 0,
            "rejected", 0,
            "approvalRate", "0%",
            "department", "Claims Processing",
            "approvalLimit", 500000.0
        ));
    }
}
