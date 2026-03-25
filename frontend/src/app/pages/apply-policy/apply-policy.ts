import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { 
  Policy, InsuredMember, User, UserPolicy, 
  Reward, DiscountRule, PremiumBreakdown 
} from '../../models/models';

/**
 * Multi-step form component for active policy application and purchase.
 * Best Practice: Typed forms and reactive state management.
 */
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
  userPolicies: UserPolicy[] = [];
  loading = true;
  
  // UX State navigation
  step: 'PLANS' | 'FORM' = 'PLANS';
  formStep: number = 1; // 1: Nominee, 2: Members, 3: Medical, 4: Rewards, 5: Review
  selectedPlan: Policy | null = null;
  
  formData = {
    nomineeName: '',
    nomineeRelationship: '',
    healthReport: null as File | null,
    members: [] as InsuredMember[],
    rewardIds: [] as number[],
    declarations: {
      hospitalizedLastYear: false,
      chronicConditions: false,
      smokeOrAlcohol: false,
      surgicalHistory: false
    }
  };

  availableRewards: Reward[] = [];
  discountRules: DiscountRule[] = [];
  userProfile: User | null = null;
  preview: PremiumBreakdown | null = null;
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
    this.loadCatalog();
    this.loadUserData();
    this.handleQueryParams();
  }

  /** Fetch available plans/policies */
  private loadCatalog(): void {
    this.api.getActivePolicies().subscribe({
      next: (data) => {
        this.plans = data;
        this.filteredPlans = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Failed to load portfolio:', err);
        this.loading = false;
      }
    });
  }

  /** Load personalized user data for rewards and eligibility */
  private loadUserData(): void {
    const userId = this.auth.getUserId()!;
    this.api.getAvailableRewardsForUser(userId).subscribe(r => this.availableRewards = r);
    this.api.getAllDiscountRules().subscribe(rules => this.discountRules = rules.filter(r => r.isActive));
    this.api.getProfile(userId).subscribe(u => this.userProfile = u);
    this.api.getUserPolicies(userId).subscribe(p => this.userPolicies = p);
  }

  /** Handle direct navigation via links (e.g., from Dashboard) */
  private handleQueryParams(): void {
    this.route.queryParams.subscribe(params => {
      const pid = params['policyId'];
      if (!pid) return;

      const rids = params['rewardIds'];
      if (rids) {
        this.formData.rewardIds = Array.isArray(rids) ? rids.map(Number) : [Number(rids)];
      }

      // Wait for catalog to load before selecting plan
      const checkInterval = setInterval(() => {
        if (!this.loading && this.plans.length > 0) {
          const plan = this.plans.find(p => p.policyId == pid);
          if (plan) {
            this.selectPlan(plan);
          }
          clearInterval(checkInterval);
        }
      }, 100);
    });
  }

  filterPlans(category: string): void {
    this.activeCategory = category;
    this.filteredPlans = category === 'ALL' 
      ? this.plans 
      : this.plans.filter(p => p.policyType === category);
  }

  /** Check if user already holds or is applying for this exact policy */
  isPlanDisabled(policyId: number): boolean {
    return this.userPolicies.some(p => p.policyId === policyId && 
      ['PENDING_UNDERWRITING', 'UNDER_EVALUATION', 'QUOTES_SENT', 'ACTIVE'].includes(p.status));
  }

  /** Initiate the application flow for a specific plan */
  selectPlan(plan: Policy): void {
    if (this.isPlanDisabled(plan.policyId)) return;
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
    if (event.target.files.length > 0) {
      this.formData.healthReport = event.target.files[0];
    }
  }

  nextFormStep(): void {
    if (this.validateCurrentStep()) {
      this.formStep++;
      window.scrollTo(0, 0);
      if (this.formStep === 5) this.calculatePreview();
    }
  }

  prevFormStep(): void {
    if (this.formStep > 1) {
      this.formStep--;
      window.scrollTo(0, 0);
    }
  }

  private validateCurrentStep(): boolean {
    if (this.formStep === 1) {
      if (!this.formData.nomineeName || !this.formData.nomineeRelationship) {
        this.errorMessage = 'Please provide nominee details.';
        return false;
      }
    }
    if (this.formStep === 2) {
      if (this.formData.members.length === 0) {
        this.errorMessage = 'At least one member must be insured.';
        return false;
      }
      for (let m of this.formData.members) {
        if (!m.fullName || !m.dateOfBirth || !m.gender) {
          this.errorMessage = 'Please complete all member details.';
          return false;
        }
      }
    }
    this.errorMessage = '';
    return true;
  }

  toggleReward(rewardId: number): void {
    const idx = this.formData.rewardIds.indexOf(rewardId);
    if (idx >= 0) {
      this.formData.rewardIds = []; 
    } else {
      this.formData.rewardIds = [rewardId]; // Select one premium reward
    }
    this.calculatePreview();
  }

  /** Calculate actuarial premium preview based on current data */
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

  /** Check if user currently meets a specific discount criteria */
  getRuleStatus(rule: DiscountRule): boolean {
    if (!this.userProfile || !this.preview) return false;
    const meetsPoints = !rule.minUserPoints || this.userProfile.userPoints >= rule.minUserPoints;
    const meetsScore = !rule.minQuizScorePercent || this.preview.bestQuizScorePercent >= rule.minQuizScorePercent;
    const meetsBadges = !rule.minBadgesEarned || this.preview.badgesEarned >= rule.minBadgesEarned;
    return meetsPoints && meetsScore && meetsBadges;
  }

  /** Final form submission with file upload handling */
  submitRequest(): void {
    if (!this.selectedPlan) return;
    
    const userId = this.auth.getUserId()!;
    this.successMessage = '';
    this.errorMessage = '';

    if (this.formData.healthReport) {
      this.api.uploadFile(this.formData.healthReport).subscribe({
        next: (resp) => this.executePurchase(userId, resp.filePath),
        error: (err) => this.errorMessage = 'File upload failed: ' + (err.error?.message || 'Check connection')
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
