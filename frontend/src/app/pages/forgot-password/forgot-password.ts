import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [FormsModule, RouterLink, CommonModule],
  templateUrl: './forgot-password.html',
  styleUrls: ['./forgot-password.scss']
})
export class ForgotPasswordComponent {
  email = '';
  message = '';
  error = '';
  loading = false;
  success = false;

  constructor(private api: ApiService, private router: Router) {}

  onSubmit(): void {
    this.error = '';
    this.message = '';
    
    if (!this.email) {
      this.error = 'Please enter your email address';
      return;
    }

    this.loading = true;
    this.api.forgotPassword(this.email).subscribe({
      next: () => {
        this.loading = false;
        this.success = true;
        this.message = 'If an account exists with this email, you will receive an OTP shortly.';
        setTimeout(() => this.router.navigate(['/reset-password']), 3000);
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.message || 'Something went wrong. Please try again.';
      }
    });
  }
}
