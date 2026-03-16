import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../services/api.service';
import { Claim, User } from '../../../models/models';

@Component({
  selector: 'app-assign-claims-officer',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './assign-claims-officer.html',
  styleUrls: ['./assign-claims-officer.scss']
})
export class AssignClaimsOfficerComponent implements OnInit {
  pendingClaims: Claim[] = [];
  officers: User[] = [];
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
    this.api.getAllClaims().subscribe({
      next: (claims) => {
        this.pendingClaims = claims.filter(c => c.status === 'SUBMITTED');
        this.loading = false;
      },
      error: () => this.loading = false
    });

    this.api.getUsersByRole('ROLE_CLAIMS_OFFICER').subscribe(users => {
      this.officers = users;
    });
  }

  assign(claimId: number, officerId: string): void {
    if (!officerId || this.processing) return;
    
    this.processing = true;
    this.notification = null;

    this.api.assignClaimOfficer(claimId, parseInt(officerId)).subscribe({
      next: () => {
        this.pendingClaims = this.pendingClaims.filter(c => c.id !== claimId);
        this.processing = false;
        this.showNotification('Claims Officer assigned successfully!', 'success');
      },
      error: () => {
        this.processing = false;
        this.showNotification('Failed to assign officer. Please try again.', 'error');
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
