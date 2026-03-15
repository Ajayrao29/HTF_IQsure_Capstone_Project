import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../../services/api.service';
import { UserPolicy, Claim } from '../../../models/models';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.scss']
})
export class AdminDashboardComponent implements OnInit {
  policies: UserPolicy[] = [];
  claims: Claim[] = [];
  
  stats = {
    pendingApps: 0,
    underEvaluation: 0,
    quotesSent: 0,
    activePolicies: 0,
    expiredPolicies: 0
  };

  claimStats = {
    submitted: 0,
    underReview: 0,
    approved: 0,
    settled: 0,
    rejected: 0
  };

  loading = true;

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.api.getAllUserPoliciesAdmin().subscribe(policies => {
      this.policies = policies;
      this.calculateStats();
      this.loading = false;
    });

    this.api.getAllClaims().subscribe(claims => {
      this.claims = claims;
      this.calculateClaimStats();
    });
  }

  calculateStats(): void {
    this.stats.pendingApps = this.policies.filter(p => p.status === 'PENDING').length;
    this.stats.underEvaluation = this.policies.filter(p => p.status === 'UNDER_EVALUATION').length;
    this.stats.quotesSent = this.policies.filter(p => p.status === 'QUOTES_SENT').length;
    this.stats.activePolicies = this.policies.filter(p => p.status === 'ACTIVE').length;
    this.stats.expiredPolicies = this.policies.filter(p => p.status === 'EXPIRED').length;
  }

  calculateClaimStats(): void {
    this.claimStats.submitted = this.claims.filter(c => c.status === 'SUBMITTED').length;
    this.claimStats.underReview = this.claims.filter(c => c.status === 'UNDER_REVIEW').length;
    this.claimStats.approved = this.claims.filter(c => c.status === 'APPROVED' || c.status === 'PARTIAL_APPROVED').length;
    this.claimStats.settled = this.claims.filter(c => c.status === 'SETTLED').length;
    this.claimStats.rejected = this.claims.filter(c => c.status === 'REJECTED').length;
  }
}
