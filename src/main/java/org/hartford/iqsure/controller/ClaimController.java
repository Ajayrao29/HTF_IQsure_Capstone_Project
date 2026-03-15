package org.hartford.iqsure.controller;

import lombok.RequiredArgsConstructor;
import org.hartford.iqsure.entity.Claim;
import org.hartford.iqsure.service.ClaimService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/claims")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ClaimController {

    private final ClaimService claimService;

    @GetMapping
    public ResponseEntity<List<Claim>> getAllClaims() {
        return ResponseEntity.ok(claimService.getAllClaims());
    }

    @GetMapping("/all")
    public ResponseEntity<List<Claim>> getAllClaimsAdmin() {
        return ResponseEntity.ok(claimService.getAllClaims());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Claim>> getClaimsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(claimService.getClaimsByUser(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Claim> getClaimById(@PathVariable Long id) {
        return ResponseEntity.ok(claimService.getClaimById(id));
    }

    @PostMapping("/file")
    public ResponseEntity<Claim> fileClaim(@RequestParam Long userId, @RequestParam Long userPolicyId, @RequestBody Claim claim) {
        return ResponseEntity.ok(claimService.fileClaim(userId, userPolicyId, claim));
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<Claim> assignOfficer(@PathVariable Long id, @RequestParam Long officerId) {
        return ResponseEntity.ok(claimService.assignOfficer(id, officerId));
    }

    @PutMapping("/{id}/process")
    public ResponseEntity<Claim> processClaim(@PathVariable Long id, 
                                             @RequestParam Claim.ClaimStatus status, 
                                             @RequestParam String remarks, 
                                             @RequestParam(required = false) java.math.BigDecimal approvedAmount) {
        return ResponseEntity.ok(claimService.processClaim(id, status, remarks, approvedAmount));
    }
}
