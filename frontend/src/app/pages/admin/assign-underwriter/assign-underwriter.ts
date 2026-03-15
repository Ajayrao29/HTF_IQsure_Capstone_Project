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

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.api.getPoliciesByStatus('PENDING').subscribe(policies => {
      this.pendingPolicies = policies;
      this.loading = false;
    });

    this.api.getUsersByRole('ROLE_UNDERWRITER').subscribe(users => {
      this.underwriters = users;
    });
  }

  assign(policyId: number, underwriterId: string): void {
    if (!underwriterId) return;
    
    this.api.assignUnderwriter(policyId, parseInt(underwriterId)).subscribe(() => {
      this.pendingPolicies = this.pendingPolicies.filter(p => p.id !== policyId);
      alert('Underwriter assigned successfully!');
    });
  }
}
