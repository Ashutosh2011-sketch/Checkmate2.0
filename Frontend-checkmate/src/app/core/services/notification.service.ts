import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class NotificationService {

  private baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getNotifications(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/notifications/me`);
  }

  markAsRead(id: number): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/notifications/${id}/read`, {});
  }

  getAdminNotifications(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/notifications/admin`);
  }

  markAllAdminRead(): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/notifications/admin/mark-all-read`, {});
  }
}