import { Component, OnInit } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../../services/api.service';
import { Claim } from '../../../models/models';

@Component({
  selector: 'app-claims-officer-claims',
  standalone: true,
  imports: [CommonModule, FormsModule, DecimalPipe, RouterLink],
  templateUrl: './claims.html',
  styleUrls: ['./claims.scss']
})
export class ClaimsOfficerClaimsComponent implements OnInit {
  claims: Claim[] = [];
  filteredClaims: Claim[] = [];
  loading = true;
  activeFilter = 'ALL';

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    this.loadClaims();
  }

  loadClaims(): void {
    // This is a placeholder as we don't have a specific "get claims by officer" endpoint yet
    // In a real app we would call something like this.api.getClaimsByOfficer(officerId)
    // For now, let's just fetch all claims and filter (though it's inefficient)
    this.api.getAllClaimsAdmin().subscribe((c: Claim[]) => {
      this.claims = c;
      this.applyFilter('ALL');
      this.loading = false;
    });
  }

  applyFilter(filter: string): void {
    this.activeFilter = filter;
    if (filter === 'ALL') {
      this.filteredClaims = this.claims;
    } else {
      this.filteredClaims = this.claims.filter(c => c.status === filter);
    }
  }

  getStatusClass(status: string): string {
    switch(status) {
      case 'APPROVED': return 'status-approved';
      case 'REJECTED': return 'status-rejected';
      case 'SUBMITTED': return 'status-submitted';
      case 'UNDER_REVIEW': return 'status-review';
      default: return '';
    }
  }
}
