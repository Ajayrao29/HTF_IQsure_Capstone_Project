package org.hartford.iqsure.controller;

import lombok.RequiredArgsConstructor;
import org.hartford.iqsure.dto.response.ClaimResponseDTO;
import org.hartford.iqsure.entity.Claim;
import org.hartford.iqsure.service.ClaimService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/claims")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService claimService;

    @GetMapping
    public ResponseEntity<List<ClaimResponseDTO>> getAllClaims() {
        return ResponseEntity.ok(claimService.getAllClaims().stream()
                .map(ClaimResponseDTO::fromEntity)
                .collect(Collectors.toList()));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ClaimResponseDTO>> getAllClaimsAdmin() {
        return ResponseEntity.ok(claimService.getAllClaims().stream()
                .map(ClaimResponseDTO::fromEntity)
                .collect(Collectors.toList()));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ClaimResponseDTO>> getClaimsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(claimService.getClaimsByUser(userId).stream()
                .map(ClaimResponseDTO::fromEntity)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClaimResponseDTO> getClaimById(@PathVariable Long id) {
        return ResponseEntity.ok(ClaimResponseDTO.fromEntity(claimService.getClaimById(id)));
    }

    @PostMapping("/file")
    public ResponseEntity<ClaimResponseDTO> fileClaim(@RequestParam Long userId, @RequestParam Long userPolicyId, @RequestBody Claim claim) {
        return ResponseEntity.ok(ClaimResponseDTO.fromEntity(claimService.fileClaim(userId, userPolicyId, claim)));
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<ClaimResponseDTO> assignOfficer(@PathVariable Long id, @RequestParam Long officerId) {
        return ResponseEntity.ok(ClaimResponseDTO.fromEntity(claimService.assignOfficer(id, officerId)));
    }

    @PutMapping("/{id}/process")
    public ResponseEntity<ClaimResponseDTO> processClaim(@PathVariable Long id, 
                                             @RequestParam Claim.ClaimStatus status, 
                                             @RequestParam String remarks, 
                                             @RequestParam(required = false) java.math.BigDecimal approvedAmount) {
        return ResponseEntity.ok(ClaimResponseDTO.fromEntity(claimService.processClaim(id, status, remarks, approvedAmount)));
    }

    @PutMapping("/{id}/settle")
    public ResponseEntity<ClaimResponseDTO> settleClaim(@PathVariable Long id, @RequestParam java.math.BigDecimal settlementAmount) {
        return ResponseEntity.ok(ClaimResponseDTO.fromEntity(claimService.settleClaim(id, settlementAmount)));
    }
}
