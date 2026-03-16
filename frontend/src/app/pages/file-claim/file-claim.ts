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
  today: string = new Date().toISOString().split('T')[0];
  
  formData = {
    userPolicyId: null as number | null,
    type: 'CASHLESS',
    amount: null as number | null,
    hospitalName: '',
    incidentDate: '',
    diagnosis: '',
    description: ''
  };

  successMessage = '';
  errorMessage = '';

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
    if (!this.formData.userPolicyId || !this.formData.amount || !this.formData.incidentDate || !this.formData.diagnosis) {
      this.errorMessage = 'Please fill in all required fields (Policy, Amount, Date, and Diagnosis).';
      this.successMessage = '';
      return;
    }

    const userId = this.auth.getUserId()!;
    this.api.fileClaim(userId, this.formData.userPolicyId, this.formData).subscribe({
      next: () => {
        this.successMessage = 'Claim filed successfully! Our officer will review it.';
        this.errorMessage = '';
        setTimeout(() => this.router.navigate(['/my-claims']), 3000);
      },
      error: (err) => {
        console.error('Claim filing failed:', err);
        // Try to get message from backend ErrorResponse, then from Angular HttpErrorResponse, then default
        const errorMsg = err.error?.message || err.message || (typeof err.error === 'string' ? err.error : 'Unknown error');
        this.errorMessage = 'Error filing claim: ' + errorMsg;
        this.successMessage = '';
      }
    });
  }
}
