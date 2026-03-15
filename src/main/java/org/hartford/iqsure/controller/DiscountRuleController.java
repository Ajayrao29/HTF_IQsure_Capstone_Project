/*
 * FILE: DiscountRuleController.java | LOCATION: controller/
 * PURPOSE: Discount rule CRUD API. Admin configures rules that give premium discounts
 *          based on user gamification data (points, badges, quiz scores).
 * ENDPOINTS: GET/POST/PUT/DELETE /api/v1/discount-rules
 * FLOW: DiscountRulesComponent → api.service.ts → THIS → DiscountRuleService
 */
package org.hartford.iqsure.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hartford.iqsure.dto.request.DiscountRuleRequestDTO;
import org.hartford.iqsure.dto.response.DiscountRuleResponseDTO;
import org.hartford.iqsure.service.DiscountRuleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Discount Rule Engine endpoints.
 *
 * GET    /api/v1/discount-rules          - list all active rules
 * GET    /api/v1/discount-rules/all      - list all rules incl. inactive (admin)
 * GET    /api/v1/discount-rules/{id}     - get rule by id
 * POST   /api/v1/discount-rules          - create rule (admin)
 * PUT    /api/v1/discount-rules/{id}     - update rule (admin)
 * DELETE /api/v1/discount-rules/{id}     - delete rule (admin)
 */
@RestController
@RequestMapping("/api/v1/discount-rules")
@RequiredArgsConstructor
@Tag(name = "Discount Rules", description = "Gamification-based premium discount rule management")
public class DiscountRuleController {

    private final DiscountRuleService discountRuleService;

    @GetMapping
    @Operation(summary = "Get all active discount rules")
    public ResponseEntity<List<DiscountRuleResponseDTO>> getActiveRules() {
        return ResponseEntity.ok(discountRuleService.getActiveRules());
    }

    @GetMapping("/all")
    @Operation(summary = "Get all discount rules including inactive (Admin)")
    public ResponseEntity<List<DiscountRuleResponseDTO>> getAllRules() {
        return ResponseEntity.ok(discountRuleService.getAllRules());
    }

    @GetMapping("/{ruleId}")
    @Operation(summary = "Get discount rule by ID")
    public ResponseEntity<DiscountRuleResponseDTO> getById(@PathVariable Long ruleId) {
        return ResponseEntity.ok(discountRuleService.getRuleById(ruleId));
    }

    @PostMapping
    @Operation(summary = "Create a new discount rule (Admin)")
    public ResponseEntity<DiscountRuleResponseDTO> create(@Valid @RequestBody DiscountRuleRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(discountRuleService.createRule(dto));
    }

    @PutMapping("/{ruleId}")
    @Operation(summary = "Update a discount rule (Admin)")
    public ResponseEntity<DiscountRuleResponseDTO> update(
            @PathVariable Long ruleId,
            @Valid @RequestBody DiscountRuleRequestDTO dto) {
        return ResponseEntity.ok(discountRuleService.updateRule(ruleId, dto));
    }

    @DeleteMapping("/{ruleId}")
    @Operation(summary = "Delete a discount rule (Admin)")
    public ResponseEntity<Void> delete(@PathVariable Long ruleId) {
        discountRuleService.deleteRule(ruleId);
        return ResponseEntity.noContent().build();
    }
}
