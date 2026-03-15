/*
 * FILE: policy-mgmt.ts | LOCATION: pages/admin/policy-mgmt/
 * PURPOSE: Admin Policy Management page (URL: /admin/policy-mgmt). Admin can:
 *          - Create, edit, delete insurance policies
 *          - Set base premium, coverage amount, duration, and policy type
 * TEMPLATE: policy-mgmt.html | STYLES: policy-mgmt.scss
 * CALLS: api.service.ts → getAllPolicies(), createPolicy(), updatePolicy(), deletePolicy()
 * BACKEND: PolicyController → PolicyService
 * GUARD: AdminGuard — only accessible by admin users
 */
import { Component, OnInit } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../services/api.service';

@Component({ selector: 'app-policy-mgmt', standalone: true, imports: [CommonModule, FormsModule, DecimalPipe], templateUrl: './policy-mgmt.html', styleUrls: ['./policy-mgmt.scss'] })
export class PolicyMgmtComponent implements OnInit {
  policies: any[] = []; loading = true; showForm = false; editingPolicy: any = null;
  form: any = { title: '', description: '', policyType: 'HEALTH', basePremium: null, coverageAmount: null, durationMonths: null, isActive: true, ageRange: '', planType: 'INDIVIDUAL' };
  constructor(private api: ApiService) {}
  ngOnInit(): void { this.loadPolicies(); }
  loadPolicies(): void { this.api.getAllPolicies().subscribe(p => { this.policies = p; this.loading = false; }); }
  openCreate(): void { this.editingPolicy = null; this.form = { title: '', description: '', policyType: 'HEALTH', basePremium: null, coverageAmount: null, durationMonths: null, isActive: true, ageRange: '', planType: 'INDIVIDUAL' }; this.showForm = true; }
  openEdit(p: any): void { this.editingPolicy = p; this.form = { ...p }; this.showForm = true; }
  save(): void {
    const obs = this.editingPolicy ? this.api.updatePolicy(this.editingPolicy.policyId, this.form) : this.api.createPolicy(this.form);
    obs.subscribe(() => { this.showForm = false; this.loadPolicies(); });
  }
  delete(id: number): void { if (!confirm('Delete policy?')) return; this.api.deletePolicy(id).subscribe(() => this.loadPolicies()); }
}
