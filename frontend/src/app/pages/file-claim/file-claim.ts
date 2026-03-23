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
  
  claimStep: number = 1; // 1: Incident, 2: Medical, 3: Evidence, 4: Settlement
  
  formData = {
    userPolicyId: null as number | null,
    type: 'CASHLESS',
    amount: null as number | null,
    hospitalName: '',
    incidentDate: '',
    diagnosis: '',
    description: '',
    bankAccountNo: '',
    bankIFSC: '',
    billsUploaded: false
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

  nextStep(): void {
    if (this.validateStep()) {
      this.claimStep++;
      window.scrollTo(0, 0);
    }
  }

  prevStep(): void {
    if (this.claimStep > 1) {
      this.claimStep--;
      window.scrollTo(0, 0);
    }
  }

  validateStep(): boolean {
    if (this.claimStep === 1) {
      if (!this.formData.userPolicyId || !this.formData.amount || !this.formData.incidentDate) {
        this.errorMessage = 'Please complete all incident details.';
        return false;
      }
    }
    if (this.claimStep === 2) {
      if (!this.formData.diagnosis) {
        this.errorMessage = 'Please provide the diagnosis.';
        return false;
      }
    }
    this.errorMessage = '';
    return true;
  }

  submitClaim(): void {
    if (!this.formData.userPolicyId || !this.formData.amount || !this.formData.incidentDate || !this.formData.diagnosis) {
      this.errorMessage = 'Incomplete application. Please go back and fill details.';
      return;
    }

    const userId = this.auth.getUserId()!;
    this.api.fileClaim(userId, this.formData.userPolicyId, this.formData).subscribe({
      next: () => {
        this.successMessage = 'Claim filed successfully! Reference ID: CLM-' + Math.floor(Math.random() * 90000 + 10000);
        this.errorMessage = '';
        setTimeout(() => this.router.navigate(['/my-claims']), 3000);
      },
      error: (err) => {
        const errorMsg = err.error?.message || err.message || 'Error occurred';
        this.errorMessage = 'Error filing claim: ' + errorMsg;
      }
    });
  }
}
