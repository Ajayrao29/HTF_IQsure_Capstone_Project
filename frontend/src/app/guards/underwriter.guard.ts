import { Injectable } from '@angular/core';
import { Router, CanActivate } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({ providedIn: 'root' })
export class UnderwriterGuard implements CanActivate {
  constructor(private auth: AuthService, private router: Router) {}
  canActivate(): boolean {
    const user = this.auth.getUser();
    if (user && user.role === 'ROLE_UNDERWRITER') return true;
    this.router.navigate(['/dashboard']);
    return false;
  }
}
