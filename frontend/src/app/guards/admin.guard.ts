/*
 * FILE: admin.guard.ts | LOCATION: frontend/src/app/guards/
 * PURPOSE: Route guard that BLOCKS access to admin pages if user is NOT an admin.
 *          If user isn't admin → redirects to /dashboard page.
 * USED IN: app.routes.ts → canActivate: [AuthGuard, AdminGuard] on admin routes
 * CHECKS: AuthService.isAdmin() (services/auth.service.ts)
 */


import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';  // → services/auth.service.ts

@Injectable({ providedIn: 'root' })
export class AdminGuard implements CanActivate {
  constructor(private auth: AuthService, private router: Router) {}

  // Called by Angular Router before navigating to an admin route
  canActivate(): boolean {
    if (this.auth.isAdmin()) return true;      // User is admin → allow access
    this.router.navigate(['/dashboard']);        // Not admin → redirect to user dashboard
    return false;
  }
}

