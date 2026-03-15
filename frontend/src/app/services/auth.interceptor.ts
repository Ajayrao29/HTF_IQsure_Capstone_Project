/*
 * FILE: auth.interceptor.ts | LOCATION: frontend/src/app/services/
 * PURPOSE: HTTP Interceptor that automatically attaches the Bearer token
 *          to EVERY outgoing HTTP request. This way, page components don't
 *          need to manually add the Authorization header each time.
 *
 * HOW IT WORKS:
 *   1. Angular's HttpClient sends a request
 *   2. This interceptor catches it BEFORE it goes out
 *   3. Gets the token from AuthService (services/auth.service.ts)
 *   4. If token exists → clones the request and adds "Authorization: Bearer <token>" header
 *   5. Passes the request on to the backend
 *
 * REGISTERED IN: app.config.ts → provideHttpClient(withInterceptors([authInterceptor]))
 */
import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';

import { AuthService } from './auth.service';  // → services/auth.service.ts

// Functional interceptor (Angular 17+ style — no class needed)
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Get the auth service to access the stored token
  const auth = inject(AuthService);
  const token = auth.getToken();

  let requestToForward = req;

  // If a token exists, clone the request and add the Authorization header
  if (token) {
    requestToForward = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
  }

  // Pass the request on
  return next(requestToForward);
};
