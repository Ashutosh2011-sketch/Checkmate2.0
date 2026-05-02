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
    return this.get<ChecklistSummary[]>('/checklists/all');
  }

  // Existing method (KEEP IT)
  getTasks(): Observable<any[]> {
    return this.get<any[]>('/tasks');
  }

  
  getTasksByChecklist(id: number): Observable<any[]> {
    return this.get<any[]>(`/tasks/checklist/${id}`);
  }
}