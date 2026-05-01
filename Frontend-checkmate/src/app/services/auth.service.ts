// auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = `${environment.apiUrl}/auth`;

  constructor(private http: HttpClient) {}

  // 🔐 LOGIN API
  login(email: string, password: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, { email, password });
  }

  // 💾 SAVE TOKEN + ROLE
  saveTokenAndRole(token: string, role: string) {
    localStorage.setItem('token', token);
    localStorage.setItem('role', role);
  }

  // 💾 SAVE PERMISSIONS
  savePermissions(perms: any[]) {
    localStorage.setItem('permissions', JSON.stringify(perms));
  }

  // 🔍 GET TOKEN
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  // 🔍 CHECK LOGIN
  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  // 🔍 CHECK ADMIN
  isAdmin(): boolean {
    return localStorage.getItem('role') === 'ADMIN';
  }

  // 🚪 LOGOUT
  logout() {
    localStorage.clear();
  }
}