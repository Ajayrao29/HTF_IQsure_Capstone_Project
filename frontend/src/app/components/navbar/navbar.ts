/*
 * FILE: navbar.ts | LOCATION: frontend/src/app/components/navbar/
 * PURPOSE: Navigation bar component shown on all authenticated pages.
 *          Shows different navigation links for Users vs Admins.
 *          Template: navbar.html | Styles: navbar.scss
 *
 * FOR USERS: Dashboard, Quizzes, Policies, My Policies, Savings, Badges, Achievements, Rewards, Leaderboard
 * FOR ADMINS: Users, Quizzes, Policies, Badges, Rewards, Discount Rules
 *
 * FEATURES: User avatar with initials, profile dropdown, logout button
 * USES: AuthService (services/auth.service.ts), Router (for logout redirect)
 */
import { Component } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';

import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './navbar.html',
  styleUrls: ['./navbar.scss']
})
export class NavbarComponent {
  constructor(public auth: AuthService, private router: Router) {}

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }

  get userName(): string {
    return this.auth.getUser()?.name || '';
  }

  get userEmail(): string {
    return this.auth.getUser()?.email || '';
  }

  get userPoints(): number {
    return 0; // Will be fetched from API in real implementation
  }

  get isAdmin(): boolean {
    return this.auth.isAdmin();
  }

  get isUnderwriter(): boolean {
    return this.auth.getUser()?.role === 'ROLE_UNDERWRITER';
  }

  get isClaimsOfficer(): boolean {
    return this.auth.getUser()?.role === 'ROLE_CLAIMS_OFFICER';
  }

  getInitials(): string {
    const name = this.userName;
    if (!name) return 'U';
    const parts = name.trim().split(' ');
    if (parts.length >= 2) {
      return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
    }
    return name.substring(0, 2).toUpperCase();
  }
}
