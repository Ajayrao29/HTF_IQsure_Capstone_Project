import { Component, OnInit } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../../services/api.service';
import { AuthService } from '../../../services/auth.service';
import { UserPolicy } from '../../../models/models';

@Component({
  selector: 'app-underwriter-my-policies',
  standalone: true,
  imports: [CommonModule, DecimalPipe, RouterLink],
  templateUrl: './my-policies.html',
  styleUrls: ['./my-policies.scss']
})
export class UnderwriterMyPoliciesComponent implements OnInit {
  policies: UserPolicy[] = [];
  loading = true;

  constructor(private api: ApiService, private auth: AuthService) {}

  ngOnInit(): void {
    const userId = this.auth.getUserId();
    if (userId) {
      this.api.getUnderwriterPoliciesByStatus(userId).subscribe((apps: UserPolicy[]) => {
        // Show everything successfully assigned to them that is either SENT or ACTIVE
        this.policies = apps.filter(a => a.status !== 'UNDER_EVALUATION');
        this.loading = false;
      });
    } else {
      this.loading = false;
    }
  }

  getStatusClass(status: string): string {
    switch(status) {
      case 'ACTIVE': return 'status-active';
      case 'QUOTE_SENT': return 'status-quote';
      case 'EXPIRED': return 'status-expired';
      default: return '';
    }
  }
}
