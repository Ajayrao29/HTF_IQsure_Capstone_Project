/*
 * ============================================================================
 * FILE: app.config.ts | LOCATION: frontend/src/app/
 * PURPOSE: Angular application configuration. Sets up:
 *   1. Zone change detection (Angular's change detection strategy)
 *   2. Router (maps URLs to page components → see app.routes.ts)
 *   3. HttpClient with auth interceptor (attaches token to every API call)
 *
 * CONNECTS TO:
 *   - app.routes.ts → defines all URL routes
 *   - auth.interceptor.ts → adds Bearer token to HTTP requests
 *   - main.ts → bootstraps the app using this config
 * ============================================================================
 */
import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { routes } from './app.routes';                    // → app.routes.ts (all URL routes)
import { authInterceptor } from './services/auth.interceptor'; // → services/auth.interceptor.ts

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),  // Optimizes change detection
    provideRouter(routes),                                   // Register all routes from app.routes.ts
    provideHttpClient(withInterceptors([authInterceptor])),  // Every HTTP call goes through authInterceptor
  ]
};
