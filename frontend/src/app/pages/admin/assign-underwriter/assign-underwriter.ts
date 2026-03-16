import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../services/api.service';
import { UserPolicy, User } from '../../../models/models';

@Component({
  selector: 'app-assign-underwriter',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './assign-underwriter.html',
  styleUrls: ['./assign-underwriter.scss']
})
export class AssignUnderwriterComponent implements OnInit {
  pendingPolicies: UserPolicy[] = [];
  underwriters: User[] = [];
  loading = true;
  processing = false;
  notification: { message: string, type: 'success' | 'error' } | null = null;

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.notification = null;
    this.api.getAllUserPoliciesAdmin().subscribe({
      next: (policies) => {
        this.pendingPolicies = policies.filter(p => p.status === 'PENDING_UNDERWRITING');
        this.loading = false;
      },
      error: () => this.loading = false
    });

    this.api.getUsersByRole('ROLE_UNDERWRITER').subscribe(users => {
      this.underwriters = users;
    });
  }

  assign(policyId: number, underwriterId: string): void {
    if (!underwriterId || this.processing) return;

    this.processing = true;
    this.notification = null;

    this.api.assignUnderwriter(policyId, parseInt(underwriterId)).subscribe({
      next: () => {
        this.pendingPolicies = this.pendingPolicies.filter(p => p.id !== policyId);
        this.processing = false;
        this.showNotification('Underwriter assigned successfully!', 'success');
      },
      error: () => {
        this.processing = false;
        this.showNotification('Failed to assign underwriter. Please try again.', 'error');
      }
    });
  }

  showNotification(message: string, type: 'success' | 'error'): void {
    this.notification = { message, type };
    setTimeout(() => {
      this.notification = null;
    }, 3000);
  }
}
