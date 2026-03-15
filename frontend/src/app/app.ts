/*
 * ============================================================================
 * FILE: app.ts | LOCATION: frontend/src/app/
 * PURPOSE: The ROOT component of the entire Angular application.
 *          Everything else is rendered INSIDE this component.
 *          It shows/hides the navigation bar based on the current URL.
 *
 * STRUCTURE:
 *   <app-navbar>        → Navbar component (components/navbar/navbar.ts)
 *   <router-outlet>     → Placeholder where page components are loaded based on URL
 *
 * LOGIC:
 *   - Listens to router navigation events
 *   - Hides navbar on public pages: /, /about, /login, /register
 *   - Shows navbar on all other pages (dashboard, quizzes, admin, etc.)
 *
 * CONNECTS TO:
 *   - components/navbar/navbar.ts → the navigation bar component
 *   - app.routes.ts → determines which page component loads in <router-outlet>
 *   - services/auth.service.ts → checks if user is logged in
 * ============================================================================
 */
import { Component } from '@angular/core';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { NavbarComponent } from './components/navbar/navbar';   // → components/navbar/navbar.ts
import { AuthService } from './services/auth.service';          // → services/auth.service.ts

import { filter } from 'rxjs';

@Component({
  selector: 'app-root',       // This component's HTML tag name
  standalone: true,            // Standalone component (no NgModule needed)
  imports: [RouterOutlet, NavbarComponent],
  template: `
    <!-- Show navbar only on authenticated pages (not landing/login/register) -->
    @if (showNavbar) {
      <app-navbar></app-navbar>
    }
    <main class="main-content">
      <!-- This is where the current page component gets rendered based on the URL -->
      <!-- e.g., /dashboard → DashboardComponent, /quizzes → QuizzesComponent -->
      <router-outlet></router-outlet>
    </main>
    `,
  styles: [`.main-content { min-height: calc(100vh - 64px); }`]
})
export class App {
  showNavbar = false;  // Controls whether the navbar is visible

  constructor(public auth: AuthService, private router: Router) {
    // Listen to every route change and decide if navbar should show
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      // These are public pages where we DON'T show the navbar
      const hiddenRoutes = ['/', '/about', '/login', '/register'];
      this.showNavbar = !hiddenRoutes.includes(event.url);
    });
  }
}
