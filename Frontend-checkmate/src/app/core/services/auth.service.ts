
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Router } from '@angular/router'; 

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  
  private apiUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient, private router: Router) {}

  login(email: string, password: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, { email, password });
  }

  registerNewEmployee(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, userData);
  }

  // Save token, role, and permissions
  saveTokenAndRole(token: string, role: string) {
    localStorage.setItem('token', token);
    localStorage.setItem('role', role);
  }

  // Save permissions from login response
  savePermissions(permissions: string[]) {
    localStorage.setItem('permissions', JSON.stringify(permissions));
  }

  // Get stored permissions
  getPermissions(): string[] {
    const perms = localStorage.getItem('permissions');
    return perms ? JSON.parse(perms) : [];
  }

  // Check if user has a specific permission
  hasPermission(permissionName: string): boolean {
    if (this.isAdmin()) return true; // Admin has all permissions
    return this.getPermissions().includes(permissionName);
  }

  isAdmin(): boolean {
    return localStorage.getItem('role') === 'ADMIN';
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('token'); 
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('role');
    localStorage.removeItem('permissions');
    localStorage.removeItem('userName');
    this.router.navigate(['/login']); 
  }
}