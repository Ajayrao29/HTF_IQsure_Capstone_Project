/*
 * FILE: my-policies.ts | LOCATION: pages/my-policies/
 * PURPOSE: My Policies page (URL: /my-policies). Shows all policies the user has purchased.
 * CALLS: api.service.ts → getUserPolicies() → UserPolicyController → GET /api/v1/users/{id}/policies
 */
import { Component, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule, DecimalPipe } from '@angular/common';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

@Component({ selector: 'app-my-policies', standalone: true, imports: [CommonModule, RouterLink, DecimalPipe], templateUrl: './my-policies.html', styleUrls: ['./my-policies.scss'] })
export class MyPoliciesComponent implements OnInit {
  policies: any[] = []; loading = true;
  constructor(private api: ApiService, private auth: AuthService) {}
  ngOnInit(): void { this.api.getUserPolicies(this.auth.getUserId()!).subscribe(p => { this.policies = p; this.loading = false; }); }
  getPolicyIcon(type: string): string { return type === 'HEALTH' ? '❤️' : type === 'LIFE' ? '🌿' : '🚗'; }
  getStatusClass(status: string): string { return status === 'ACTIVE' ? 'active' : status === 'EXPIRED' ? 'expired' : 'cancelled'; }
}
