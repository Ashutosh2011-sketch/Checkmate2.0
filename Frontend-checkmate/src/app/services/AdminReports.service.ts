import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

// ─── Interfaces (Real Backend Response) ──────────────────────────────────────
export interface AdminSummaryResponse {
  totalChecklists: number;
  totalTasks: number;
  completedTasks: number;
  pendingTasks: number;
  completedChecklists: number;
}

export interface DepartmentStatsResponse {
  departmentId: number;
  departmentName: string;
  totalChecklists: number;
  totalTasks: number;
  completedTasks: number;
  pendingTasks: number;
  overdueTasks: number;
  completionRate: number;
}

export interface OverdueItemResponse {
  taskId: number;
  taskName: string;
  checklistName: string;
  assignedTo: string;
  dueDate: string;
  daysOverdue: number;
  departmentName: string;
  priority: string;
}

export interface UserPerformanceResponse {
  userId: number;
  userName: string;
  departmentName: string;
  totalAssigned: number;
  completed: number;
  inProgress: number;
  overdue: number;
  completionRate: number;
  avgCompletionDays: number;
}

export interface CompletionTrendResponse {
  period: string;
  completed: number;
  total: number;
  completionRate: number;
}

export interface BottleneckResponse {
  checklistId: number;
  checklistName: string;
  currentLevel: string;
  pendingTasksCount: number;
  daysStuck: number;
  departmentName: string;
}

@Injectable({ providedIn: 'root' })
export class ReportService {
  private apiUrl = `${environment.apiUrl}/api`;

  constructor(private http: HttpClient) {}

  // ✅ Existing - Admin Dashboard Summary
  getAdminSummary(): Observable<AdminSummaryResponse> {
    return this.http.get<AdminSummaryResponse>(`${this.apiUrl}/dashboard/admin/summary`);
  }

  // 📊 Department-wise statistics
  getDepartmentStats(): Observable<DepartmentStatsResponse[]> {
    return this.http.get<DepartmentStatsResponse[]>(`${this.apiUrl}/reports/department-stats`);
  }

  // ⏰ Overdue items
  getOverdueItems(departmentId?: number): Observable<OverdueItemResponse[]> {
    let params = new HttpParams();
    if (departmentId) params = params.set('departmentId', departmentId);
    return this.http.get<OverdueItemResponse[]>(`${this.apiUrl}/reports/overdue`, { params });
  }

  // 👤 User performance
  getUserPerformance(departmentId?: number): Observable<UserPerformanceResponse[]> {
    let params = new HttpParams();
    if (departmentId) params = params.set('departmentId', departmentId);
    return this.http.get<UserPerformanceResponse[]>(`${this.apiUrl}/reports/user-performance`, { params });
  }

  // 📈 Completion trends (monthly/weekly)
  getCompletionTrends(startDate: string, endDate: string, groupBy: 'MONTH' | 'WEEK' = 'MONTH'): Observable<CompletionTrendResponse[]> {
    const params = new HttpParams()
      .set('startDate', startDate)
      .set('endDate', endDate)
      .set('groupBy', groupBy);
    return this.http.get<CompletionTrendResponse[]>(`${this.apiUrl}/reports/completion-trends`, { params });
  }

  // 🚨 Bottlenecks
  getBottlenecks(): Observable<BottleneckResponse[]> {
    return this.http.get<BottleneckResponse[]>(`${this.apiUrl}/reports/bottlenecks`);
  }
}