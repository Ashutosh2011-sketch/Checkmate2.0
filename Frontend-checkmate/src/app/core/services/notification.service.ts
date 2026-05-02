
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private baseUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) { }

  private getHeaders() {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  getNotifications(): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.baseUrl}/api/notifications/me`, 
      { headers: this.getHeaders() }
    );
  }

  markAsRead(id: number): Observable<void> {
    return this.http.put<void>(
      `${this.baseUrl}/api/notifications/${id}/read`, 
      {}, 
      { headers: this.getHeaders() }
    );
  }

  getAdminNotifications(): Observable<any[]> {
    return this.http.get<any[]>(
      `${this.baseUrl}/api/notifications/admin`,
      { headers: this.getHeaders() }
    );
  }

  markAllAdminRead(): Observable<void> {
    return this.http.put<void>(
      `${this.baseUrl}/api/notifications/admin/mark-all-read`, 
      {}, 
      { headers: this.getHeaders() }
    );
  }
}