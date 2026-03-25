import { Component, OnInit } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../services/api.service';
import { Policy } from '../../../models/models';

/**
 * Admin component for managing insurance policy catalog.
 * Best Practice: Use standalone components and explicit type interfaces.
 */
@Component({
  selector: 'app-policy-mgmt',
  standalone: true,
  imports: [CommonModule, FormsModule, DecimalPipe],
  templateUrl: './policy-mgmt.html',
  styleUrls: ['./policy-mgmt.scss']
})
export class PolicyMgmtComponent implements OnInit {
  policies: Policy[] = [];
  loading = true;
  showForm = false;
  editingPolicy: Policy | null = null;

  // New/Edit form state
  form: Partial<Policy> = this.getEmptyForm();

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadPolicies();
  }

  /** Fetch all policies from the backend */
  loadPolicies(): void {
    this.loading = true;
    this.api.getAllPolicies().subscribe({
      next: (data) => {
        this.policies = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching policies:', err);
        this.loading = false;
      }
    });
  }

  /** Open the modal/form for creating a new policy */
  openCreate(): void {
    this.editingPolicy = null;
    this.form = this.getEmptyForm();
    this.showForm = true;
  }

  /** Open the modal/form for editing an existing policy */
  openEdit(policy: Policy): void {
    this.editingPolicy = policy;
    this.form = { ...policy }; // Create a shallow copy to avoid direct mutation
    this.showForm = true;
  }

  /** Save or Update policy data */
  save(): void {
    if (!this.validateForm()) return;

    this.loading = true;
    const request = this.editingPolicy 
      ? this.api.updatePolicy(this.editingPolicy.policyId, this.form) 
      : this.api.createPolicy(this.form);

    request.subscribe({
      next: () => {
        this.showForm = false;
        this.loadPolicies();
      },
      error: (err) => {
        console.error('Error saving policy:', err);
        this.loading = false;
        alert('Failed to save policy. Check console for details.');
      }
    });
  }

  /** Deletes a policy after user confirmation */
  delete(id: number): void {
    if (!confirm('Are you sure you want to delete this policy? This cannot be undone.')) {
      return;
    }

    this.api.deletePolicy(id).subscribe({
      next: () => this.loadPolicies(),
      error: (err) => console.error('Error deleting policy:', err)
    });
  }

  private validateForm(): boolean {
    if (!this.form.title?.trim()) {
      alert('Policy title is required');
      return false;
    }
    if (this.form.basePremium === null || this.form.basePremium === undefined || this.form.basePremium <= 0) {
      alert('Valid base premium is required');
      return false;
    }
    return true;
  }

  private getEmptyForm(): Partial<Policy> {
    return {
      title: '',
      description: '',
      policyType: 'HEALTH',
      basePremium: 0,
      coverageAmount: 0,
      durationMonths: 12,
      isActive: true,
      ageRange: '18-60',
      planType: 'INDIVIDUAL'
    };
  }
}
