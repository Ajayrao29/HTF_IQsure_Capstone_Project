/*
 * FILE: login.ts | LOCATION: pages/login/
 * PURPOSE: Login page (URL: /login). User enters email + password to log in.
 *          On success → saves user to AuthService → redirects to /dashboard.
 * TEMPLATE: login.html | STYLES: login.scss
 * CALLS: api.service.ts → login() → POST /api/auth/login → AuthController
 */
import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { FormsModule } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrls: ['./login.scss']
})
export class LoginComponent {
  email = '';
  password = '';
  error = '';
  loading = false;

  constructor(
    private api: ApiService,
    private auth: AuthService,
    private router: Router
  ) {}

  login(): void {
    this.error = '';

    if (!this.email || !this.password) {
      this.error = 'Please fill in all fields';
      return;
    }

    if (!this.email.includes('@')) {
      this.error = 'Please enter a valid email';
      return;
    }

    this.loading = true;

    this.api.login({ email: this.email, password: this.password }).subscribe({
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
        } else {
          this.error = 'Invalid email or password';
        }
        this.loading = false;
      }
    });
  }
}
