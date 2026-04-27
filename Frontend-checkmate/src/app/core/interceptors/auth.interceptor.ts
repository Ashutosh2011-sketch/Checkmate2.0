import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private router: Router) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    
    // Skip auth for login/register endpoints
    if (req.url.includes('/api/auth/')) {
      return next.handle(req);
    }

    // Attach JWT token to all other requests
    const token = localStorage.getItem('token');
    let authReq = req;
    
    if (token) {
      authReq = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${token}`)
      });
    }

    return next.handle(authReq).pipe(
      catchError((error: HttpErrorResponse) => {

<<<<<<< HEAD
        // Only redirect to login if token is missing or truly expired
        // Do NOT clear localStorage on every 401 — only on actual auth failures
        if (error.status === 401 && !req.url.includes('/api/auth/')) {
          // Only redirect if we don't have a token (means session expired)
          if (!token) {
            this.router.navigate(['/login']);
          } else {
            // Token exists but got 401 — could be expired
            console.warn('Auth error for:', req.url, '- Status:', error.status);
          }
        }
=======
  // ✅ Allow these APIs without forcing login
const allowedUrls = ['/tasks', '/reports', '/api/reports'];
>>>>>>> b5439415b5f35a8444dbc016a72d3e70074b7f88

        return throwError(() => error);
      })
    );
  }
}