import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe, DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { Claim } from '../../models/models';

@Component({
  selector: 'app-my-claims',
  standalone: true,
  imports: [CommonModule, RouterLink, DatePipe, DecimalPipe],
  templateUrl: './my-claims.html',
  styleUrls: ['./my-claims.scss']
})
export class MyClaimsComponent implements OnInit {
  claims: Claim[] = [];
  loading = true;

  constructor(private api: ApiService, private auth: AuthService) {}

  ngOnInit(): void {
    const userId = this.auth.getUserId();
    if (userId) {
      this.api.getClaimsByUser(userId).subscribe({
        next: (data) => {
          this.claims = data;
          this.loading = false;
        },
        error: () => this.loading = false
      });
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
