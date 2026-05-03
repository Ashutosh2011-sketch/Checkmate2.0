import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface TemplateTaskDto {
  title: string;
  description?: string;
  priority: string;
  dueDateDays: number;
  assignees?: string[];
}

export interface TemplateSectionDto {
  sectionName: string;
  tasks: TemplateTaskDto[];
}

export interface TemplateDto {
  id?: number;
  templateName: string;
  department: string;
  visibility: string;
  workflowType: string;
  description?: string;
  createdBy?: string;
  currentVersion?: number;
  createdAt?: string;
  updatedAt?: string;
  sections?: TemplateSectionDto[];
}

export interface TemplateVersionDto {
  id: number;
  version: number;
  snapshot: string;
  changeNote: string;
  createdBy: string;
  createdAt: string;
}

@Injectable({ providedIn: 'root' })
export class TemplateService {

  private apiUrl = `${environment.apiUrl}/templates`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<TemplateDto[]> {
    return this.http.get<TemplateDto[]>(this.apiUrl);
  }

  getById(id: number): Observable<TemplateDto> {
    return this.http.get<TemplateDto>(`${this.apiUrl}/${id}`);
  }

  create(dto: TemplateDto): Observable<TemplateDto> {
    return this.http.post<TemplateDto>(this.apiUrl, dto);
  }

  update(id: number, dto: TemplateDto, changeNote?: string): Observable<TemplateDto> {
    let params = new HttpParams();
    if (changeNote) params = params.set('changeNote', changeNote);
    return this.http.put<TemplateDto>(`${this.apiUrl}/${id}`, dto, { params });
  }

  delete(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }

  getVersions(templateId: number): Observable<TemplateVersionDto[]> {
    return this.http.get<TemplateVersionDto[]>(`${this.apiUrl}/${templateId}/versions`);
  }

  restoreVersion(templateId: number, versionId: number): Observable<TemplateDto> {
    return this.http.post<TemplateDto>(
      `${this.apiUrl}/${templateId}/versions/${versionId}/restore`, {}
    );
  }
}