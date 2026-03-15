/*
 * FILE: PolicyController.java | LOCATION: controller/
 * PURPOSE: Insurance policy catalog management API. Admin creates/edits policies; users browse them.
 * ENDPOINTS: GET/POST/PUT/DELETE /api/v1/policies
 * FLOW: PolicyMgmtComponent / PoliciesComponent → api.service.ts → THIS → PolicyService
 */
package org.hartford.iqsure.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hartford.iqsure.dto.request.PolicyRequestDTO;
import org.hartford.iqsure.dto.response.PolicyResponseDTO;
import org.hartford.iqsure.service.PolicyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Policy Management endpoints.
 *
 * GET  /api/v1/policies          - list all active policies (any user)
 * GET  /api/v1/policies/all      - list all policies incl. inactive (admin)
 * POST /api/v1/policies          - create policy (admin)
 * GET  /api/v1/policies/{id}     - get policy by id
 * PUT  /api/v1/policies/{id}     - update policy (admin)
 * DELETE /api/v1/policies/{id}   - delete policy (admin)
 */
@RestController
@RequestMapping("/api/v1/policies")
@RequiredArgsConstructor
@Tag(name = "Policies", description = "Insurance policy catalog management")
public class PolicyController {

    private final PolicyService policyService;

    @GetMapping
    @Operation(summary = "Get all active policies")
    public ResponseEntity<List<PolicyResponseDTO>> getActivePolicies() {
        return ResponseEntity.ok(policyService.getActivePolicies());
    }

    @GetMapping("/all")
    @Operation(summary = "Get all policies including inactive (Admin)")
    public ResponseEntity<List<PolicyResponseDTO>> getAllPolicies() {
        return ResponseEntity.ok(policyService.getAllPolicies());
    }

    @GetMapping("/{policyId}")
    @Operation(summary = "Get policy by ID")
    public ResponseEntity<PolicyResponseDTO> getById(@PathVariable Long policyId) {
        return ResponseEntity.ok(policyService.getPolicyById(policyId));
    }

    @PostMapping
    @Operation(summary = "Create a new policy (Admin)")
    public ResponseEntity<PolicyResponseDTO> create(@Valid @RequestBody PolicyRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(policyService.createPolicy(dto));
    }

    @PutMapping("/{policyId}")
    @Operation(summary = "Update a policy (Admin)")
    public ResponseEntity<PolicyResponseDTO> update(
            @PathVariable Long policyId,
            @Valid @RequestBody PolicyRequestDTO dto) {
        return ResponseEntity.ok(policyService.updatePolicy(policyId, dto));
    }

    @DeleteMapping("/{policyId}")
    @Operation(summary = "Delete a policy (Admin)")
    public ResponseEntity<Void> delete(@PathVariable Long policyId) {
        policyService.deletePolicy(policyId);
        return ResponseEntity.noContent().build();
    }
}
