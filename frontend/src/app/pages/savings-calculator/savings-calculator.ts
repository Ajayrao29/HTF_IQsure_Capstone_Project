/*
 * FILE: savings-calculator.ts | LOCATION: pages/savings-calculator/
 * PURPOSE: Savings Calculator page (URL: /savings). Shows total savings from purchased policies
 *          and potential future savings based on current gamification data.
 * TEMPLATE: savings-calculator.html | STYLES: savings-calculator.scss
 * CALLS: api.service.ts → getUserPolicies(), getProfile(), getActivePolicies(), calculatePremium()
 * BACKEND: UserPolicyController, UserController, PolicyController, PremiumCalculationService
 */
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-savings',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './savings-calculator.html',
  styleUrls: ['./savings-calculator.scss']
})
export class SavingsCalculatorComponent implements OnInit {
  totalSavings = 0;
  potentialSavings = 0;
  policies: any[] = [];
  userPoints = 0;
  availableRewards: any[] = [];

  constructor(private api: ApiService, private auth: AuthService) {}

  ngOnInit(): void {
    const userId = this.auth.getUserId()!;

    this.api.getUserPolicies(userId).subscribe(policies => {
      this.policies = policies;
      this.totalSavings = policies.reduce((sum, p) => sum + (p.savedAmount || 0), 0);
    });

    this.api.getProfile(userId).subscribe(u => {
      this.userPoints = u.userPoints || 0;

      this.api.getAvailableRewardsForUser(userId).subscribe(rewards => {
        this.availableRewards = rewards || [];
        this.calculatePotentialSavings(userId);
      });
    });
  }

  calculatePotentialSavings(userId: number): void {
    this.api.getActivePolicies().subscribe(policies => {
      if (!policies || policies.length === 0) {
        this.potentialSavings = 0;
        return;
      }

      const selectedRewardIds = this.availableRewards.map(r => r.userRewardId);

      if (selectedRewardIds.length === 0) {
        this.potentialSavings = 0;
        return;
      }

      let potential = 0;
      let completed = 0;

      policies.forEach(p => {
        this.api.calculatePremium(userId, p.policyId, selectedRewardIds).subscribe({
          next: calc => {
            potential += calc.discountedAmount || 0;
            completed++;

            if (completed === policies.length) {
              this.potentialSavings = Number(potential.toFixed(2));
            }
          },
          error: () => {
            completed++;

            if (completed === policies.length) {
              this.potentialSavings = Number(potential.toFixed(2));
            }
          }
        });
      });
    });
  }
}
