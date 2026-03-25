import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';

/**
 * Global HTTP Interceptor for Authentication and Error Handling.
 * 1. Attaches Bearer Token if user is logged in.
 * 2. Catches global HTTP errors for logging/debugging.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const token = auth.getToken();

  let requestToForward = req;

  // Best Practice: Automatically attach JWT token to all requests
  if (token) {
    requestToForward = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
  }

  // Best Practice: Centralized error handling for all API calls
  return next(requestToForward).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = 'An unknown error occurred!';
      
      if (error.error instanceof ErrorEvent) {
        // Client-side error (e.g., network issue)
        errorMessage = `Network Error: ${error.error.message}`;
      } else {
        // Server-side error (e.g., 401, 500)
        errorMessage = `Server Error [${error.status}]: ${error.message}`;
        
        // Log sensitive status codes for debugging
        if (error.status === 401) {
          console.warn('Unauthorized request - session may have expired.');
          // Optional: auth.logout() if you want to force re-login on 401
        }
      }

      console.error('🌐 HTTP Global Error:', errorMessage, error);
      
      // Pass the error back to the component so it can show a specific message
      return throwError(() => error);
    })
  );
};
