/*
 * FILE: policies.ts | LOCATION: pages/policies/
 * PURPOSE: Policy browsing & purchasing page (URL: /policies). Users can:
 *          1. Browse active insurance policies
 *          2. Select coupons (redeemed rewards) to apply to a premium
 *          3. Preview their personalized premium (with gamification discounts + coupons)
 *          4. See the discount rules panel to learn how to earn more discounts
 *          5. Purchase a policy (rewards are marked as used after purchase)
 * TEMPLATE: policies.html | STYLES: policies.scss
 */
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

@Component({ 
  selector: 'app-policies', 
  standalone: true, 
  imports: [CommonModule, DecimalPipe, FormsModule], 
  templateUrl: './policies.html', 
  styleUrls: ['./policies.scss'] 
})
export class PoliciesComponent implements OnInit {
  policies: any[] = [];
  preview: any = null;
  selectedPolicyId: number | null = null;
  loadingPreview = false;
  purchasing = false;
  message = '';
  loading = true;

  // Coupons
  availableRewards: any[] = [];
  selectedRewardIds: number[] = [];
  showCouponPicker = false;

  // Discount rules info panel
  discountRules: any[] = [];
  showRulesPanel = false;
  userPoints = 0;
  bestScore = 0;
  badgeCount = 0;

  constructor(private api: ApiService, private auth: AuthService, private router: Router) {}

  ngOnInit(): void {
    const userId = this.auth.getUserId()!;
    this.api.getActivePolicies().subscribe(p => { this.policies = p; this.loading = false; });
    this.api.getAvailableRewardsForUser(userId).subscribe(r => this.availableRewards = r);
    this.api.getAllDiscountRules().subscribe(rules => this.discountRules = rules.filter((r: any) => r.isActive));
    this.api.getProfile(userId).subscribe(u => {
      this.userPoints = u.userPoints;
      this.badgeCount = 0;
    });
  }

  previewPremium(policyId: number): void {
    this.selectedPolicyId = policyId;
    this.preview = null;
    this.loadingPreview = true;
    this.message = '';
    this.api.calculatePremium(this.auth.getUserId()!, policyId, this.selectedRewardIds).subscribe({
      next: (b) => { 
        this.preview = b; 
        this.loadingPreview = false;
        this.bestScore = b.bestQuizScorePercent;
        this.badgeCount = b.badgesEarned;
      },
      error: () => { this.loadingPreview = false; }
    });
  }

  toggleReward(rewardId: number): void {
    const idx = this.selectedRewardIds.indexOf(rewardId);
    if (idx >= 0) {
      this.selectedRewardIds.splice(idx, 1);
    } else {
      this.selectedRewardIds.push(rewardId);
    }
    // Refresh the preview with the new coupon selection
    if (this.selectedPolicyId) {
      this.previewPremium(this.selectedPolicyId);
    }
  }

  isRewardSelected(rewardId: number): boolean {
    return this.selectedRewardIds.includes(rewardId);
  }

  purchase(): void {
    if (!this.selectedPolicyId) return;
    // Pass selected rewards to the application form
    this.router.navigate(['/apply-policy'], { 
      queryParams: { 
        policyId: this.selectedPolicyId,
        rewardIds: this.selectedRewardIds
      } 
    });
  }

  closePreview(): void { 
    this.preview = null; 
    this.selectedPolicyId = null; 
    this.message = ''; 
    this.selectedRewardIds = [];
  }

  getPolicyIcon(type: string): string { return type === 'HEALTH' ? '❤️' : type === 'LIFE' ? '🌿' : '🚗'; }

  getRuleStatus(rule: any): 'unlocked' | 'locked' {
    const meetsPoints = !rule.minUserPoints || this.userPoints >= rule.minUserPoints;
    const meetsScore = !rule.minQuizScorePercent || this.bestScore >= rule.minQuizScorePercent;
    const meetsBadges = !rule.minBadgesEarned || this.badgeCount >= rule.minBadgesEarned;
    return (meetsPoints && meetsScore && meetsBadges) ? 'unlocked' : 'locked';
  }
}
