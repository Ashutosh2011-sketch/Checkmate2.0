import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Checklist } from '../models/checklist.model';
import { ApiService } from './api.service';

@Injectable({
  providedIn: 'root'
})
export class ChecklistService extends ApiService {

  createChecklist(checklist: any): Observable<any> {
    return this.post('/checklists/create', checklist);
  }

  getAllChecklists(): Observable<Checklist[]> {
    return this.get<Checklist[]>('/checklists/all');
  }
}

