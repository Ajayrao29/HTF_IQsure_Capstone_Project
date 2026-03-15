/*
 * FILE: rewards.ts | LOCATION: pages/rewards/
 * PURPOSE: Shows rewards auto-earned by completing quizzes and qualifying for discount rules.
 *          Rewards are NOT manually redeemed—they are automatically awarded in the backend
 *          after each quiz submission. Users can use them as coupons when buying a policy.
 * TEMPLATE: rewards.html | STYLES: rewards.scss
 */
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

@Component({ selector: 'app-rewards', standalone: true, imports: [CommonModule], templateUrl: './rewards.html', styleUrls: ['./rewards.scss'] })
export class RewardsComponent implements OnInit {
  rewards: any[] = [];
  loading = true;

  get availableRewards() { return this.rewards.filter(r => !r.isUsed && !r.isExpired); }
  get usedRewards()      { return this.rewards.filter(r => r.isUsed); }
  get expiredRewards()   { return this.rewards.filter(r => r.isExpired && !r.isUsed); }

  constructor(private api: ApiService, private auth: AuthService) {}

  ngOnInit(): void {
    this.api.getEarnedRewardsByUser(this.auth.getUserId()!).subscribe({
      next: (r) => { this.rewards = r; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  getRewardIcon(type: string): string {
    if (type.toLowerCase().includes('quiz')) return '📝';
    if (type.toLowerCase().includes('score') || type.toLowerCase().includes('passer')) return '🎯';
    if (type.toLowerCase().includes('badge')) return '🏅';
    if (type.toLowerCase().includes('streak') || type.toLowerCase().includes('fire')) return '🔥';
    return '🎫';
  }
}
