import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdminDashboardSummary, Dashboard } from '../models/dashboard.model';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  private api = 'http://localhost:8080/api/dashboard';

  constructor(private http: HttpClient) {}

  // ✅ REAL BACKEND CALL
  getDashboardData(userName: string): Observable<Dashboard> {
    return this.http.get<Dashboard>(`${this.api}/${userName}`);
  }

  getAdminSummary(): Observable<AdminDashboardSummary> {
    return this.http.get<AdminDashboardSummary>(`${this.api}/admin/summary`);
  }

  /*
  ================== OPTIONAL (BACKUP MOCK) ==================
  If backend fails, you can temporarily use this:

  getDashboardData(userName: string): Observable<Dashboard> {
    return of({
      progress: 65,
      assignedChecklists: ['IT Onboarding Checklist'],
      claimedTasks: ['Audit Preparation'],
      notifications: ['New task assigned']
    });
  }
  ===========================================================
  */
}