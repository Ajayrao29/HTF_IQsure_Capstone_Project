import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { UserPolicy } from '../../models/models';

@Component({
  selector: 'app-file-claim',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './file-claim.html',
  styleUrls: ['./file-claim.scss']
})
export class FileClaimComponent implements OnInit {
  policies: UserPolicy[] = [];
  loading = true;
  
  formData = {
    userPolicyId: null as number | null,
    type: 'CASHLESS',
    amount: null as number | null,
    hospitalName: '',
    incidentDate: '',
    diagnosis: '',
    description: ''
  };

  constructor(
    private api: ApiService,
    private auth: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const userId = this.auth.getUserId();
    if (userId) {
      this.api.getUserPolicies(userId).subscribe({
        next: (data) => {
          this.policies = data.filter(p => p.status === 'ACTIVE');
          this.loading = false;
        },
        error: () => this.loading = false
      });
    }
  }

  submitClaim(): void {
    if (!this.formData.userPolicyId || !this.formData.amount) {
      alert('Please select a policy and enter an amount.');
      return;
    }

    const userId = this.auth.getUserId()!;
    this.api.fileClaim(userId, this.formData.userPolicyId, this.formData).subscribe({
      next: () => {
        alert('Claim filed successfully! Our officer will review it.');
        this.router.navigate(['/my-claims']);
      },
      error: (err) => {
        alert('Error filing claim: ' + (err.error?.message || 'Unknown error'));
      }
    });
  }
}
