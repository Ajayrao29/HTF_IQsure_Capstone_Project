import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { Policy, InsuredMember } from '../../models/models';

@Component({
  selector: 'app-apply-policy',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './apply-policy.html',
  styleUrls: ['./apply-policy.scss']
})
export class ApplyPolicyComponent implements OnInit {
  today: string = new Date().toISOString().split('T')[0];
  plans: Policy[] = [];
  filteredPlans: Policy[] = [];
  loading = true;
  
  // Step navigation
  step: 'PLANS' | 'FORM' = 'PLANS';
  selectedPlan: Policy | null = null;
  
  formData = {
    nomineeName: '',
    nomineeRelationship: '',
    healthReport: null as File | null,
    members: [] as InsuredMember[],
    rewardIds: [] as number[]
  };

  availableRewards: any[] = [];
  discountRules: any[] = [];
  userProfile: any = null;
  preview: any = null;
  loadingPreview = false;
  successMessage = '';
  errorMessage = '';

  activeCategory = 'ALL';

  constructor(
    private api: ApiService,
    private auth: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.loadPlans();
    this.loadUserData();
    this.route.queryParams.subscribe(params => {
      const pid = params['policyId'];
      if (pid) {
        // Handle pre-selected rewards from query params if any
        const rids = params['rewardIds'];
        if (rids) {
          this.formData.rewardIds = Array.isArray(rids) ? rids.map(Number) : [Number(rids)];
        }

        const checkPlans = setInterval(() => {
          if (!this.loading && this.plans.length > 0) {
            const plan = this.plans.find(p => p.policyId == pid);
            if (plan) {
              this.selectPlan(plan);
              this.calculatePreview();
            }
            clearInterval(checkPlans);
          }
        }, 100);
      }
    });
  }

  loadUserData(): void {
    const userId = this.auth.getUserId()!;
    this.api.getAvailableRewardsForUser(userId).subscribe(r => this.availableRewards = r);
    this.api.getAllDiscountRules().subscribe(rules => this.discountRules = rules.filter((r: any) => r.isActive));
    this.api.getProfile(userId).subscribe(u => this.userProfile = u);
  }

  loadPlans(): void {
    this.api.getActivePolicies().subscribe({
      next: (data) => {
        this.plans = data;
        this.filteredPlans = data;
        this.loading = false;
      },
      error: () => this.loading = false
    });
  }

  filterPlans(category: string): void {
    this.activeCategory = category;
    if (category === 'ALL') {
      this.filteredPlans = this.plans;
    } else {
      this.filteredPlans = this.plans.filter(p => p.policyType === category);
    }
  }

  selectPlan(plan: Policy): void {
    this.selectedPlan = plan;
    this.step = 'FORM';
    // Add "Self" as first member by default
    const user = this.auth.getUser();
    this.formData.members = [{
      fullName: user?.name || '',
      relationship: 'Self',
      dateOfBirth: '',
      gender: '',
      preExistingConditions: ''
    }];
    window.scrollTo(0, 0);
  }

  backToPlans(): void {
    this.step = 'PLANS';
    this.selectedPlan = null;
    window.scrollTo(0, 0);
  }

  addMember(): void {
    this.formData.members.push({
      fullName: '',
      relationship: '',
      dateOfBirth: '',
      gender: '',
      preExistingConditions: ''
    });
  }

  removeMember(index: number): void {
    this.formData.members.splice(index, 1);
  }

  onFileSelected(event: any): void {
    this.formData.healthReport = event.target.files[0];
  }

  toggleReward(rewardId: number): void {
    const idx = this.formData.rewardIds.indexOf(rewardId);
    if (idx >= 0) {
      this.formData.rewardIds = []; // Toggle off
    } else {
      this.formData.rewardIds = [rewardId]; // Replace with new one (only one allowed)
    }
    this.calculatePreview();
  }

  calculatePreview(): void {
    if (!this.selectedPlan) return;
    this.loadingPreview = true;
    this.api.calculatePremium(this.auth.getUserId()!, this.selectedPlan.policyId, this.formData.rewardIds).subscribe({
      next: (data) => {
        this.preview = data;
        this.loadingPreview = false;
      },
      error: () => this.loadingPreview = false
    });
  }

  getRuleStatus(rule: any): boolean {
    if (!this.userProfile || !this.preview) return false;
    const meetsPoints = !rule.minUserPoints || this.userProfile.userPoints >= rule.minUserPoints;
    const meetsScore = !rule.minQuizScorePercent || this.preview.bestQuizScorePercent >= rule.minQuizScorePercent;
    const meetsBadges = !rule.minBadgesEarned || this.preview.badgesEarned >= rule.minBadgesEarned;
    return meetsPoints && meetsScore && meetsBadges;
  }

  submitRequest(): void {
    if (!this.selectedPlan) return;
    
    const userId = this.auth.getUserId()!;
    this.successMessage = '';
    this.errorMessage = '';

    // If there's a file, upload it first
    if (this.formData.healthReport) {
      this.api.uploadFile(this.formData.healthReport).subscribe({
        next: (resp) => {
          this.executePurchase(userId, resp.filePath);
        },
        error: (err) => {
          this.errorMessage = 'File upload failed: ' + (err.error?.message || 'Check connection');
        }
      });
    } else {
      this.executePurchase(userId, '');
    }
  }

  private executePurchase(userId: number, path: string): void {
    const request = {
      policyId: this.selectedPlan!.policyId,
      nomineeName: this.formData.nomineeName,
      nomineeRelationship: this.formData.nomineeRelationship,
      healthReportPath: path,
      insuredMembers: this.formData.members,
      rewardIds: this.formData.rewardIds
    };

    this.api.purchasePolicy(userId, request, this.formData.rewardIds).subscribe({
      next: () => {
        this.successMessage = 'Application submitted successfully! Our underwriter will review it soon.';
        setTimeout(() => this.router.navigate(['/dashboard']), 3000);
      },
      error: (err) => {
        this.errorMessage = 'Error submitting application: ' + (err.error?.message || 'Unknown error');
      }
    });
  }
}
