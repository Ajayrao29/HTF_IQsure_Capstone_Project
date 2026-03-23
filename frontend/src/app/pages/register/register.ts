/*
 * FILE: register.ts | LOCATION: pages/register/
 * PURPOSE: Registration page (URL: /register). New user creates an account.
 *          On success → saves user to AuthService → redirects to /dashboard.
 * TEMPLATE: register.html | STYLES: register.scss
 * CALLS: api.service.ts → register() → POST /api/auth/register → AuthController
 */
import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

@Component({ selector: 'app-register', standalone: true, imports: [FormsModule, RouterLink], templateUrl: './register.html', styleUrls: ['./register.scss'] })
export class RegisterComponent {
  name = '';
  email = '';
  password = '';
  phone = '';
  onboardingTip: string = 'Welcome to IQsure! Tell us your name to begin your journey.';
  loading = false;
  error = '';

  constructor(private api: ApiService, private auth: AuthService, private router: Router) {}

  updateTip(field: string): void {
    if (field === 'name') this.onboardingTip = 'Great start! Your name will be used on your global certificates.';
    if (field === 'email') this.onboardingTip = 'Security first: This will be your identity for 2FA and policy access.';
    if (field === 'password') this.onboardingTip = 'Strong passwords unlock "Safe-Vault" badges. Use 8+ chars!';
  }
  register(): void {
    this.error = '';

    if (!this.name || !this.email || !this.password) {
      this.error = 'Please fill in all required fields';
      return;
    }

    if (this.name.trim().length < 2) {
      this.error = 'Name must be at least 2 characters';
      return;
    }

    if (!this.email.includes('@') || !this.email.includes('.')) {
      this.error = 'Please enter a valid email address';
      return;
    }

    if (this.password.length < 6) {
      this.error = 'Password must be at least 6 characters';
      return;
    }

    this.loading = true;

    this.api.register({
      name: this.name.trim(),
      email: this.email.trim().toLowerCase(),
      password: this.password,
      phone: this.phone ? this.phone.trim() : null
    }).subscribe({
      next: (res) => {
        this.loading = false;
        if (res && res.userId) {
          this.auth.saveUser(res);
          this.router.navigate(['/dashboard']);
        } else {
          this.error = 'Unexpected response from server.';
        }
      },
      error: (err: any) => {
        if (err.status === 0) {
          this.error = 'Cannot connect to server. Please make sure the backend is running on port 8080.';
        } else if (err.error?.message) {
          this.error = err.error.message;
        } else if (err.error && typeof err.error === 'string') {
          this.error = err.error;
        } else if (err.error && typeof err.error === 'object') {
          const vals = Object.values(err.error).filter(v => typeof v === 'string');
          this.error = vals.length > 0 ? vals.join(', ') : 'Registration failed.';
        } else {
          this.error = 'Registration failed.';
        }
        this.loading = false;
      }
    });
  }
}
