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

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.api.getAllClaims().subscribe(claims => {
      this.pendingClaims = claims.filter(c => c.status === 'SUBMITTED');
      this.loading = false;
    });

    this.api.getUsersByRole('ROLE_CLAIMS_OFFICER').subscribe(users => {
      this.officers = users;
    });
  }

  assign(claimId: number, officerId: string): void {
    if (!officerId) return;
    
    this.api.assignClaimOfficer(claimId, parseInt(officerId)).subscribe(() => {
      this.pendingClaims = this.pendingClaims.filter(c => c.id !== claimId);
      alert('Claims Officer assigned successfully!');
    });
  }
}
