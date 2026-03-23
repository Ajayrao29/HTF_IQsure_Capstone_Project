import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [FormsModule, RouterLink, CommonModule],
  templateUrl: './reset-password.html',
  styleUrls: ['./reset-password.scss']
})
export class ResetPasswordComponent {
  otp = '';
  newPassword = '';
  confirmPassword = '';
  message = '';
  error = '';
  loading = false;
  success = false;

  constructor(private api: ApiService, private router: Router) {}

  onSubmit(): void {
    this.error = '';
    this.message = '';
    
    if (!this.otp || !this.newPassword || !this.confirmPassword) {
      this.error = 'Please fill in all fields';
      return;
    }

    if (this.newPassword !== this.confirmPassword) {
      this.error = 'Passwords do not match';
      return;
    }

    if (this.newPassword.length < 6) {
      this.error = 'Password must be at least 6 characters';
      return;
    }

    this.loading = true;
    this.api.resetPassword({ otp: this.otp, newPassword: this.newPassword }).subscribe({
      next: () => {
        this.loading = false;
        this.success = true;
        this.message = 'Password reset successful! You can now log in with your new password.';
        setTimeout(() => this.router.navigate(['/login']), 3000);
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.message || 'Invalid or expired OTP. Please try again.';
      }
    });
  }
}
