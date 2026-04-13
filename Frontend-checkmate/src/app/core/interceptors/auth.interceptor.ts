import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private router: Router) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    
    if (req.url.includes('/api/auth/')) {
      return next.handle(req);
    }

    const token = localStorage.getItem('token');
    let authReq = req;
    
    if (token) {
      authReq = req.clone({
        headers: req.headers.set('Authorization', `Bearer ${token}`)
      });
    }

    return next.handle(authReq).pipe(
      catchError((error: HttpErrorResponse) => {

  // ✅ Allow these APIs without forcing login
  const allowedUrls = ['/tasks'];

  const isAllowed = allowedUrls.some(url => req.url.includes(url));

  if ((error.status === 401 || error.status === 403) && !isAllowed) {
    localStorage.clear();
    this.router.navigate(['/login']);
  } else {
    console.log('Ignored auth error for:', req.url);
  }

  return throwError(() => error);
})
    );
  }
}