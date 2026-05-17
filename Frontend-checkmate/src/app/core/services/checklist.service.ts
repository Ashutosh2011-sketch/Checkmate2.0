import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Checklist, ChecklistSummary } from '../models/checklist.model';
import { ApiService } from './api.service';

@Injectable({
  providedIn: 'root'
})
export class ChecklistService extends ApiService {

  createChecklist(checklist: any): Observable<any> {
    return this.post('/checklists/create', checklist);
  }

  getAllChecklists(): Observable<ChecklistSummary[]> {
    const userName = encodeURIComponent(localStorage.getItem('userName') || '');
    const role = encodeURIComponent(localStorage.getItem('role') || '');

    return this.get<ChecklistSummary[]>(`/checklists/all?userName=${userName}&role=${role}`);
  }

  // Existing method (KEEP IT)
  getTasks(): Observable<any[]> {
    return this.get<any[]>('/tasks');
  }

  
  getTasksByChecklist(id: number): Observable<any[]> {
    return this.get<any[]>(`/tasks/checklist/${id}`);
  }

  claimTask(taskId: number, userName: string): Observable<any> {
    return this.http.put<any>(`${this.baseUrl}/tasks/${taskId}/claim`, { userName });
  }
}
