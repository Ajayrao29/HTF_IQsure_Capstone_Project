import { Component, OnInit } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../../services/api.service';

@Component({
  selector: 'app-underwriter-my-policies',
  standalone: true,
  imports: [CommonModule, DecimalPipe, RouterLink],
  templateUrl: './my-policies.html',
  styleUrls: ['./my-policies.scss']
})
export class UnderwriterMyPoliciesComponent implements OnInit {
  policies: any[] = [];
  loading = true;

  constructor(private api: ApiService) {}

  ngOnInit(): void {
    // We should filter policies that are assigned to this underwriter and NOT in pending evaluation
    this.api.getAllUserPoliciesAdmin().subscribe(apps => {
      // In a real app, this filtering would happen on the backend
      // For now we'll do basic filtering just to show something
      this.policies = apps.filter(a => a.status !== 'UNDER_EVALUATION');
      this.loading = false;
    });
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
