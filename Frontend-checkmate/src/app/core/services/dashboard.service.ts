import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AdminDashboardSummary, Dashboard, TaskInfo } from '../models/dashboard.model';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  private api = 'http://localhost:8080/api/dashboard';

  constructor(private http: HttpClient) {}

  getDashboardData(userName: string): Observable<Dashboard> {
    return this.http.get<Dashboard>(`${this.api}/${userName}`);
  }

  getAdminSummary(): Observable<AdminDashboardSummary> {
    return this.http.get<AdminDashboardSummary>(`${this.api}/admin/summary`);
  }

  // Update task completion percentage
  updateTaskStatus(taskId: number, completionPercent: number): Observable<TaskInfo> {
    return this.http.put<TaskInfo>(`${this.api}/tasks/${taskId}/status`, { completionPercent });
  }

  // Mark a single task as completed (100%)
  markTaskComplete(taskId: number): Observable<TaskInfo> {
    return this.http.put<TaskInfo>(`${this.api}/tasks/${taskId}/complete`, {});
  }

  // Mark entire checklist as completed
  markChecklistComplete(checklistId: number): Observable<any> {
    return this.http.put(`${this.api}/checklists/${checklistId}/complete`, {});
  }
}