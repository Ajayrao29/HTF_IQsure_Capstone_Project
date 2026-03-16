import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../../services/api.service';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-underwriter-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.scss']
})
export class UnderwriterDashboardComponent implements OnInit {
  stats: any = {
    pendingAssignments: 0,
    quotesSent: 0,
    activePolicies: 0,
    customersServed: 0,
    totalPremium: 0,
    commissionEarned: 0
  };
  
  userName = '';
  licenseNumber = '';
  loading = true;

  constructor(private api: ApiService, private auth: AuthService) {}

  ngOnInit(): void {
    const user = this.auth.getUser();
    if (user) {
      this.userName = user.name;
      this.licenseNumber = user.licenseNumber || 'LIC-PENDING';
    }
    this.loadStats();
  }

  loadStats(): void {
    const userId = this.auth.getUserId();
    if (userId) {
      this.api.getUnderwriterStats(userId).subscribe(s => {
        this.stats = s;
        this.loading = false;
      });
    }
  }
}
