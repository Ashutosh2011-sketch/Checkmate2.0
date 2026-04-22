import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface ChecklistDto {
  id?: number;
  title: string;
  description?: string;
  status?: string;
  department?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ChecklistService {

  private apiUrl = `${environment.apiUrl}/checklists`;

  constructor(private http: HttpClient) {}

  getAllChecklists(): Observable<ChecklistDto[]> {
    return this.http.get<ChecklistDto[]>(`${this.apiUrl}/all`);
  }

  createChecklist(checklist: ChecklistDto): Observable<ChecklistDto> {
    return this.http.post<ChecklistDto>(`${this.apiUrl}/create`, checklist);
  }
}