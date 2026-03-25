import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../services/api.service';
import { Reward } from '../../../models/models';

/**
 * Admin component for managing reward inventory and discount parameters.
 * Best Practice: Explicit typing and clear lifecycle hooks.
 */
@Component({
  selector: 'app-reward-mgmt',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reward-mgmt.html',
  styleUrls: ['./reward-mgmt.scss']
})
export class RewardMgmtComponent implements OnInit {
  rewards: Reward[] = [];
  loading = true;
  showForm = false;
  
  form: Partial<Reward> = this.getEmptyForm();

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadRewards();
  }

  /** Reload all reward definitions from the API */
  loadRewards(): void {
    this.loading = true;
    this.api.getAllRewards().subscribe({
      next: (data) => {
        this.rewards = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load rewards:', err);
        this.loading = false;
      }
    });
  }

  /** Persist a new reward definition */
  save(): void {
    this.loading = true;
    this.api.createReward(this.form).subscribe({
      next: (newReward) => {
        this.rewards.push(newReward);
        this.form = this.getEmptyForm();
        this.showForm = false;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to create reward:', err);
        this.loading = false;
        alert('Could not create reward. Check console for details.');
      }
    });
  }

  /** Permanent removal of a reward from the catalog */
  delete(id: number): void {
    if (!confirm('Are you sure you want to delete this reward?')) {
      return;
    }

    this.api.deleteReward(id).subscribe({
      next: () => this.rewards = this.rewards.filter(r => r.rewardId !== id),
      error: (err) => console.error('Failed to delete reward:', err)
    });
  }

  private getEmptyForm(): Partial<Reward> {
    return { rewardType: 'DISCOUNT', discountValue: 0, expiryDate: '' };
  }
}
