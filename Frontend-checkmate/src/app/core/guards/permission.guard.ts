import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class PermissionGuard implements CanActivate {

  constructor(private authService: AuthService, private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    
    // Must be logged in first
    if (!this.authService.isLoggedIn()) {
      return this.router.createUrlTree(['/login']);
    }

    // Admin can access everything
    if (this.authService.isAdmin()) {
      return true;
    }

    // Check if route requires a specific permission
    const requiredPermission = route.data['permission'] as string;
    
    if (!requiredPermission) {
      // No permission required, just need to be logged in
      return true;
    }

    // Check if user has the required permission
    if (this.authService.hasPermission(requiredPermission)) {
      return true;
    }

    // No permission → redirect to user dashboard
    return this.router.createUrlTree(['/dashboard']);
  }
}
