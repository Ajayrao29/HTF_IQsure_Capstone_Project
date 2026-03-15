/*
 * FILE: discount-rules.ts | LOCATION: pages/admin/discount-rules/
 * PURPOSE: Admin Discount Rules page (URL: /admin/discount-rules). Admin can:
 *          - Create, edit, delete discount rules
 *          - Set conditions: min quiz score, min points, min badges
 *          - Set discount percentage and applicable policy type
 *          These rules are evaluated by PremiumCalculationService when a user previews/buys a policy.
 * TEMPLATE: discount-rules.html | STYLES: discount-rules.scss
 * CALLS: api.service.ts → getAllDiscountRules(), createDiscountRule(), updateDiscountRule(), deleteDiscountRule()
 * BACKEND: DiscountRuleController → DiscountRuleService
 * GUARD: AdminGuard — only accessible by admin users
 */
import { Component, OnInit } from '@angular/core';

import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../services/api.service';

@Component({ selector: 'app-discount-rules', standalone: true, imports: [FormsModule], templateUrl: './discount-rules.html', styleUrls: ['./discount-rules.scss'] })
export class DiscountRulesComponent implements OnInit {
  rules: any[] = []; loading = true; showForm = false; editingRule: any = null;
  form: any = { ruleName: '', description: '', minQuizScorePercent: 0, minUserPoints: 0, minBadgesEarned: 0, discountPercentage: null, applicablePolicyType: null, isActive: true };
  constructor(private api: ApiService) {}
  ngOnInit(): void { this.loadRules(); }
  loadRules(): void { this.api.getAllDiscountRules().subscribe(r => { this.rules = r; this.loading = false; }); }
  openCreate(): void { this.editingRule = null; this.form = { ruleName: '', description: '', minQuizScorePercent: 0, minUserPoints: 0, minBadgesEarned: 0, discountPercentage: null, applicablePolicyType: null, isActive: true }; this.showForm = true; }
  openEdit(r: any): void { this.editingRule = r; this.form = { ...r }; this.showForm = true; }
  save(): void {
    const obs = this.editingRule ? this.api.updateDiscountRule(this.editingRule.ruleId, this.form) : this.api.createDiscountRule(this.form);
    obs.subscribe(() => { this.showForm = false; this.loadRules(); });
  }
  delete(id: number): void { if (!confirm('Delete rule?')) return; this.api.deleteDiscountRule(id).subscribe(() => this.loadRules()); }
}
