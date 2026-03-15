/*
 * ============================================================================
 * FILE: auth.service.ts | LOCATION: frontend/src/app/services/
 * PURPOSE: Manages the logged-in user's session using browser localStorage.
 *          After login/register, the AuthResponse is saved here.
 *          All components use this service to check: "Is user logged in? Is admin?"
 *
 * STORAGE KEY: "iqsure_user" in localStorage
 * STORED DATA: { token, tokenType, userId, name, email, role }
 *
 * USED BY:
 *   - LoginComponent → saveUser() after successful login
 *   - RegisterComponent → saveUser() after successful register
 *   - AuthGuard → isLoggedIn() to protect routes
 *   - AdminGuard → isAdmin() to protect admin routes
 *   - NavbarComponent → getUser(), isAdmin() for display
 *   - All page components → getUserId() for API calls
 *   - AuthInterceptor → getToken() to attach to HTTP requests
 * ============================================================================
 */
import { Injectable } from '@angular/core';
import { AuthResponse } from '../models/models';  // → models/models.ts

// Simple service to store and read the logged-in user info from localStorage
@Injectable({ providedIn: 'root' })  // Singleton — one instance shared across entire app
export class AuthService {

  private KEY = 'iqsure_user';  // localStorage key name

  // Save user data after login/register
  // Called by: LoginComponent.login(), RegisterComponent.register()
  saveUser(user: AuthResponse): void {
    localStorage.setItem(this.KEY, JSON.stringify(user));
  }

  // Get the currently logged-in user (or null if not logged in)
  // Used everywhere to get user info
  getUser(): AuthResponse | null {
    const data = localStorage.getItem(this.KEY);
    return data ? JSON.parse(data) : null;
  }

  // Is someone currently logged in?
  // Used by: AuthGuard (guards/auth.guard.ts) to protect routes
  isLoggedIn(): boolean {
    return this.getUser() !== null;
  }

  // Is the logged-in user an admin?
  // Used by: AdminGuard (guards/admin.guard.ts), NavbarComponent
  isAdmin(): boolean {
    return this.getUser()?.role === 'ROLE_ADMIN';
  }

  // Get user ID of logged-in user (needed for all API calls)
  // Used by: Every page component that calls api.service methods
  getUserId(): number | null {
    return this.getUser()?.userId ?? null;
  }

  // Get JWT token (currently "NO-AUTH" — placeholder)
  // Used by: authInterceptor (services/auth.interceptor.ts)
  getToken(): string | null {
    return this.getUser()?.token ?? null;
  }

  // Log out — removes user data from localStorage
  // Called by: NavbarComponent.logout()
  logout(): void {
    localStorage.removeItem(this.KEY);
  }
}
