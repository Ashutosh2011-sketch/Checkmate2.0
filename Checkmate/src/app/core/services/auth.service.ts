import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly TOKEN_KEY = 'auth_token';
  private readonly ROLE_KEY = 'user_role';
  private readonly USER_KEY = 'user_data';

  private role: string = 'ADMIN';

  constructor(private router: Router) {
    // Load role from localStorage if exists
    const storedRole = localStorage.getItem(this.ROLE_KEY);
    if (storedRole) {
      this.role = storedRole;
    }
  }

  /**
   * Login with email and password
   * Returns mock token + role based on email
   */
  login(email: string, password: string): boolean {
    // Mock validation - in production, this would call an API
    if (email && password) {
      const isAdmin = email.toLowerCase() === 'admin@checkmate.com';
      const role = isAdmin ? 'ADMIN' : 'USER';
      const token = this.generateMockToken(email);
      
      // Store in localStorage
      localStorage.setItem(this.TOKEN_KEY, token);
      localStorage.setItem(this.ROLE_KEY, role);
      localStorage.setItem(this.USER_KEY, JSON.stringify({ email, role }));
      
      this.role = role;
      return true;
    }
    return false;
  }

  /**
   * Logout - clear storage and navigate to landing page
   */
  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.ROLE_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.role = '';
    this.router.navigate(['']);
  }

  /**
   * Check if user is logged in
   */
  isLoggedIn(): boolean {
    return !!localStorage.getItem(this.TOKEN_KEY);
  }

  /**
   * Get current user role
   */
  getRole(): string {
    return this.role || localStorage.getItem(this.ROLE_KEY) || '';
  }

  /**
   * Check if current user is admin
   */
  isAdmin(): boolean {
    return this.getRole() === 'ADMIN';
  }

  /**
   * Get auth token
   */
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  /**
   * Generate mock JWT-like token
   */
  private generateMockToken(email: string): string {
    const payload = {
      email,
      timestamp: Date.now(),
      random: Math.random().toString(36).substring(2)
    };
    return btoa(JSON.stringify(payload));
  }
}
