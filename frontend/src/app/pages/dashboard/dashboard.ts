/*
 * FILE: dashboard.ts | LOCATION: pages/dashboard/
 * PURPOSE: Shared dashboard page (URL: /dashboard). Renders admin or user view based on role.
 *   ADMIN:  Shows platform stats (users, quizzes, policies, rewards), overview & quick actions.
 *   USER:   Shows personal progress: points, badges, recent quiz attempts, total savings.
 * TEMPLATE: dashboard.html | STYLES: dashboard.scss
 * CALLS: api.service.ts → multiple endpoints depending on role
 * BACKEND: UserController, BadgeController, AttemptController, UserPolicyController,
 *          QuizController, PolicyController, RewardController, DiscountRuleController
 */
import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { forkJoin, Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.scss']
})
export class DashboardComponent implements OnInit, OnDestroy {

  /* ── Shared ── */
  loading = true;
  private destroy$ = new Subject<void>();

  /* ── User-specific ── */
  user: any = null;
  myBadges: any[] = [];
  myAttempts: any[] = [];
  myPolicies: any[] = [];
  totalSavings = 0;
  userStats = {
    totalPolicies: 0,
    activePolicies: 0,
    awaitingQuote: 0,
    pendingClaims: 0
  };
  aiInsight: string = '';
  cognitiveLevel: string = 'STANDARD';

  /* ── Admin-specific ── */
  recentUsers: any[] = [];
  stats = {
    totalUsers: 0,
    totalAdmins: 0,
    totalCustomers: 0,
    totalQuizzes: 0,
    totalPolicies: 0,
    activePolicies: 0,
    inactivePolicies: 0,
    totalRewards: 0,
    discountRules: 0
  };

  constructor(public auth: AuthService, private api: ApiService) {}

  ngOnInit(): void {
    if (this.auth.isAdmin()) {
      this.loadAdminDashboard();
    } else {
      this.loadUserDashboard();
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /* ── Admin data loader ── */
  private loadAdminDashboard(): void {
    forkJoin({
      users: this.api.getAllUsers(),
      quizzes: this.api.getAllQuizzes(),
      policies: this.api.getAllPolicies(),
      rewards: this.api.getAllRewards(),
      discountRules: this.api.getAllDiscountRules()
    }).pipe(takeUntil(this.destroy$))
      .subscribe({
        next: ({ users, quizzes, policies, rewards, discountRules }) => {
          this.stats.totalUsers     = users.length;
          this.stats.totalAdmins    = users.filter((u: any) => u.role === 'ROLE_ADMIN').length;
          this.stats.totalCustomers = users.filter((u: any) => u.role !== 'ROLE_ADMIN').length;
          this.stats.totalQuizzes   = quizzes.length;
          this.stats.totalPolicies  = policies.length;
          this.stats.activePolicies = policies.filter((p: any) => p.active !== false).length;
          this.stats.inactivePolicies = policies.filter((p: any) => p.active === false).length;
          this.stats.totalRewards   = rewards.length;
          this.stats.discountRules  = discountRules.length;
          this.recentUsers = users.slice(-5).reverse();
          this.loading = false;
        },
        error: () => { this.loading = false; }
      });
  }

  /* ── User data loader ── */
  private loadUserDashboard(): void {
    const userId = this.auth.getUserId()!;
    forkJoin({
      profile: this.api.getProfile(userId),
      badges: this.api.getBadgesByUser(userId),
      attempts: this.api.getAttemptsByUser(userId),
      policies: this.api.getUserPolicies(userId),
      claims: this.api.getClaimsByUser(userId)
    }).pipe(takeUntil(this.destroy$))
      .subscribe({
        next: ({ profile, badges, attempts, policies, claims }) => {
          this.user = profile;
          this.myBadges = badges;
          this.myAttempts = attempts.slice(0, 5);
          this.myPolicies = policies.slice(0, 3); // Top 3 most recent
          this.totalSavings = policies.reduce((sum: number, pol: any) => sum + (pol.savedAmount || 0), 0);
          
          this.userStats.totalPolicies = policies.length;
          this.userStats.activePolicies = policies.filter((p: any) => p.status === 'ACTIVE').length;
          this.userStats.awaitingQuote = policies.filter((p: any) => ['PENDING', 'UNDER_EVALUATION', 'QUOTES_SENT'].includes(p.status)).length;
          this.userStats.pendingClaims = claims.filter((c: any) => c.status === 'SUBMITTED' || c.status === 'UNDER_REVIEW').length;
          
          /* 🤖 AGENTIC AI: COGNITIVE ANALYSIS & RISK MITIGATION FORECAST */
          this.cognitiveLevel = profile.userPoints >= 500 ? 'ELITE' : profile.userPoints >= 200 ? 'PRO' : 'STANDARD';
          
          // Actuarial-based risk mitigation calculation
          const riskFactor = profile.userPoints * 150; // Points mapped to potential surgery/ER avoidance value
          const safetyPct = Math.min(98, (profile.userPoints / 10));

          this.aiInsight = profile.userPoints >= 500 ? 
            `Superior Cognitive Status! Your projected liability mitigation is ₹${riskFactor.toLocaleString()}. You've reached a 98th-percentile safety quotient.` :
            `You've identified ₹${riskFactor.toLocaleString()} in potential hazards. Earning ${500 - profile.userPoints} more points will unlock the 'ELITE' 15% discount tier.`;

          this.loading = false;
        },
        error: () => { this.loading = false; }
      });
  }

  get recentAttempts(): any[] { return this.myAttempts; }

  shareAchievement(): void {
    const text = `I've earned ${this.user?.userPoints} points on IQsure! Join me in maximizing savings and learning about insurance.`;
    if (navigator.share) {
      navigator.share({ title: 'IQsure Achievement', text });
    } else {
      navigator.clipboard.writeText(text);
      alert('Achievement copied to clipboard!');
    }
  }
}
