/*
 * FILE: auth.guard.ts | LOCATION: frontend/src/app/guards/
 * PURPOSE: Route guard that BLOCKS access to pages if user is NOT logged in.
 *          If user isn't logged in → redirects to /login page.
 * USED IN: app.routes.ts → canActivate: [AuthGuard] on all protected routes
 * CHECKS: AuthService.isLoggedIn() (services/auth.service.ts)
 */
import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';  // → services/auth.service.ts

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
  constructor(private auth: AuthService, private router: Router) {}

  // Called by Angular Router before navigating to a protected route
  canActivate(): boolean {
    if (this.auth.isLoggedIn()) return true;  // User is logged in → allow access
    this.router.navigate(['/login']);         // Not logged in → redirect to login page
    return false;
  }
}








