/*
 * FILE: reward-mgmt.ts | LOCATION: pages/admin/reward-mgmt/
 * PURPOSE: Admin Reward Management page (URL: /admin/reward-mgmt). Admin can:
 *          - Create rewards with type, discount value, and expiry date
 *          - Delete rewards
 * TEMPLATE: reward-mgmt.html | STYLES: reward-mgmt.scss
 * CALLS: api.service.ts → getAllRewards(), createReward(), deleteReward() → RewardController
 * GUARD: AdminGuard — only accessible by admin users
 */
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../services/api.service';

@Component({ selector: 'app-reward-mgmt', standalone: true, imports: [CommonModule, FormsModule], templateUrl: './reward-mgmt.html', styleUrls: ['./reward-mgmt.scss'] })
export class RewardMgmtComponent implements OnInit {
  rewards: any[] = []; loading = true; showForm = false;
  form: any = { rewardType: '', discountValue: null, expiryDate: '' };
  constructor(private api: ApiService) {}
  ngOnInit(): void { this.api.getAllRewards().subscribe(r => { this.rewards = r; this.loading = false; }); }
  save(): void { this.api.createReward(this.form).subscribe(r => { this.rewards.push(r); this.form = { rewardType: '', discountValue: null, expiryDate: '' }; this.showForm = false; }); }
  delete(id: number): void { if (!confirm('Delete reward?')) return; this.api.deleteReward(id).subscribe(() => this.rewards = this.rewards.filter(r => r.rewardId !== id)); }
}
