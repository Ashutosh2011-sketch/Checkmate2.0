import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private apiUrl = 'http://localhost:8080/api/notifications/me';

  constructor(private http: HttpClient) { }

  private getHeaders() {
    const token = localStorage.getItem('token');
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }

  // Database se notifications lane ke liye
  getNotifications(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl, { headers: this.getHeaders() });
  }

  // notification.service.ts mein ye method add karo:

markAsRead(id: number): Observable<void> {
  const url = `http://localhost:8080/api/notifications/${id}/read`;
  return this.http.put<void>(url, {}, { headers: this.getHeaders() });
}
}