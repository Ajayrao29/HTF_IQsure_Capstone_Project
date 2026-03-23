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
import { NotificationService } from './services/notification.service';
import { filter } from 'rxjs';

@Component({
  selector: 'app-root',       // This component's HTML tag name
  standalone: true,            // Standalone component (no NgModule needed)
  imports: [RouterOutlet, NavbarComponent],
  template: `
    @if (showNavbar) {
      <app-navbar></app-navbar>
    }
    <main class="main-content">
      <router-outlet></router-outlet>
    </main>
    `,
  styles: [`.main-content { min-height: calc(100vh - 64px); }`]
})
export class App {
  showNavbar = false;  // Controls whether the navbar is visible

  constructor(
    public auth: AuthService, 
    private router: Router,
    private notifications: NotificationService
  ) {
    // Listen to every route change and decide if navbar should show
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      const hiddenRoutes = ['/', '/about', '/login', '/register', '/forgot-password', '/reset-password'];
      this.showNavbar = !hiddenRoutes.includes(event.url);
      
      // If logged in, ensure we are subscribed to notifications
      const user = this.auth.getUser();
      if (user && user.userId) {
        this.notifications.subscribe(user.userId);
      }
    });
  }
}
