import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  AdminDashboardSummary, Dashboard, TaskInfo,
  TaskComment, TaskAttachment, CollaborationCounts
} from '../models/dashboard.model';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  private api = `${environment.apiUrl}/dashboard`;
  private collabApi = `${environment.apiUrl}/collaboration`;

  constructor(private http: HttpClient) {}

  getDashboardData(userName: string): Observable<Dashboard> {
    return this.http.get<Dashboard>(`${this.api}/${userName}`);
  }

  getAdminSummary(): Observable<AdminDashboardSummary> {
    return this.http.get<AdminDashboardSummary>(`${this.api}/admin/summary`);
  }

  updateTaskStatus(taskId: number, completionPercent: number): Observable<TaskInfo> {
    return this.http.put<TaskInfo>(`${this.api}/tasks/${taskId}/status`, { completionPercent });
  }

  markTaskComplete(taskId: number): Observable<TaskInfo> {
    return this.http.put<TaskInfo>(`${this.api}/tasks/${taskId}/complete`, {});
  }

  markChecklistComplete(checklistId: number): Observable<any> {
    return this.http.put(`${this.api}/checklists/${checklistId}/complete`, {});
  }

  // ==================== COLLABORATION ====================

  getComments(taskId: number): Observable<TaskComment[]> {
    return this.http.get<TaskComment[]>(`${this.collabApi}/tasks/${taskId}/comments`);
  }

  addComment(taskId: number, content: string, authorName: string): Observable<TaskComment> {
    return this.http.post<TaskComment>(`${this.collabApi}/tasks/${taskId}/comments`, {
      content, authorName
    });
  }

  deleteComment(commentId: number): Observable<any> {
    return this.http.delete(`${this.collabApi}/comments/${commentId}`);
  }

  getAttachments(taskId: number): Observable<TaskAttachment[]> {
    return this.http.get<TaskAttachment[]>(`${this.collabApi}/tasks/${taskId}/attachments`);
  }

  uploadAttachment(taskId: number, file: File, uploadedBy: string): Observable<TaskAttachment> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('uploadedBy', uploadedBy);
    return this.http.post<TaskAttachment>(`${this.collabApi}/tasks/${taskId}/attachments`, formData);
  }

  downloadAttachment(attachmentId: number): Observable<Blob> {
    return this.http.get(`${this.collabApi}/attachments/${attachmentId}/download`, {
      responseType: 'blob'
    });
  }

  deleteAttachment(attachmentId: number): Observable<any> {
    return this.http.delete(`${this.collabApi}/attachments/${attachmentId}`);
  }

  getCollaborationCounts(taskId: number): Observable<CollaborationCounts> {
    return this.http.get<CollaborationCounts>(`${this.collabApi}/tasks/${taskId}/counts`);
  }
}