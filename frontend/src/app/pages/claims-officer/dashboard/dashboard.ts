import { Component, OnInit } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../../services/api.service';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-claims-officer-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, DecimalPipe],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.scss']
})
export class ClaimsOfficerDashboardComponent implements OnInit {
  stats: any = {
    claimsInQueue: 0,
    underReview: 0,
    totalProcessed: 0,
    approved: 0,
    rejected: 0,
    approvalRate: '0%',
    department: 'Claims Processing',
    approvalLimit: 500000.0
  };
  
  userName = '';
  employeeId = '';
  loading = true;

  constructor(private api: ApiService, private auth: AuthService) {}

  ngOnInit(): void {
    const user = this.auth.getUser();
    if (user) {
      this.userName = user.name;
      this.employeeId = user.employeeId || 'EMP-CO-001';
    }
    this.loadStats();
  }

  loadStats(): void {
    const userId = this.auth.getUserId();
    if (userId) {
      this.api.getClaimsOfficerStats(userId).subscribe(s => {
        this.stats = s;
        this.loading = false;
      });
    }
  }
}
