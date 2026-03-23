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
public class AdminPipelineController {

    private final UserPolicyService userPolicyService;
    private final org.hartford.iqsure.service.ClaimService claimService;
    private final org.hartford.iqsure.service.RiskAnalysisService riskAnalysisService;
    private final org.hartford.iqsure.repository.UserPolicyRepository userPolicyRepository;

    @GetMapping("/policies")
    public ResponseEntity<List<UserPolicyResponseDTO>> getAllPolicies() {
        return ResponseEntity.ok(userPolicyService.getAllUserPolicies());
    }

    @GetMapping("/policies/status")
    public ResponseEntity<List<UserPolicyResponseDTO>> getPoliciesByStatus(@RequestParam UserPolicy.PolicyStatus status) {
        return ResponseEntity.ok(userPolicyService.getPoliciesByStatus(status));
    }

    @GetMapping("/underwriter/stats")
    public ResponseEntity<java.util.Map<String, Object>> getUnderwriterStats(@RequestParam Long underwriterId) {
        return ResponseEntity.ok(userPolicyService.getUnderwriterStats(underwriterId));
    }

    @GetMapping("/underwriter/policies")
    public ResponseEntity<List<UserPolicyResponseDTO>> getUnderwriterPoliciesByStatus(@RequestParam Long underwriterId, @RequestParam(required = false) UserPolicy.PolicyStatus status) {
        if (status != null) {
            return ResponseEntity.ok(userPolicyService.getPoliciesByUnderwriterAndStatus(underwriterId, status));
        }
        return ResponseEntity.ok(userPolicyService.getPoliciesByUnderwriter(underwriterId));
    }

    @PutMapping("/policies/{id}/assign")
    public ResponseEntity<UserPolicyResponseDTO> assignUnderwriter(@PathVariable Long id, @RequestParam Long underwriterId) {
        return ResponseEntity.ok(userPolicyService.assignUnderwriter(id, underwriterId));
    }

    @PutMapping("/policies/{id}/quote")
    public ResponseEntity<UserPolicyResponseDTO> sendQuote(@PathVariable Long id, @RequestBody org.hartford.iqsure.dto.request.QuoteRequestDTO dto) {
        return ResponseEntity.ok(userPolicyService.sendQuote(id, dto.getQuoteAmount(), dto.getRemarks()));
    }

    @PutMapping("/policies/{id}/activate")
    public ResponseEntity<UserPolicyResponseDTO> activatePolicy(@PathVariable Long id) {
        return ResponseEntity.ok(userPolicyService.activatePolicy(id));
    }

    @PutMapping("/policies/{id}/reject")
    public ResponseEntity<UserPolicyResponseDTO> rejectPolicy(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        return ResponseEntity.ok(userPolicyService.rejectPolicy(id, body.get("remarks")));
    }

    @GetMapping("/policies/{id}/ai-analysis")
    public ResponseEntity<org.hartford.iqsure.service.RiskAnalysisService.AIAnalysisResponse> getAiAnalysis(@PathVariable Long id) {
        org.hartford.iqsure.entity.UserPolicy policy = userPolicyRepository.findById(id).orElse(null);
        if (policy == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(riskAnalysisService.analyzePolicyRisk(policy));
    }

    @GetMapping("/officer/stats")
    public ResponseEntity<java.util.Map<String, Object>> getClaimsOfficerStats(@RequestParam Long officerId) {
        return ResponseEntity.ok(claimService.getClaimsOfficerStats(officerId));
    }
}
