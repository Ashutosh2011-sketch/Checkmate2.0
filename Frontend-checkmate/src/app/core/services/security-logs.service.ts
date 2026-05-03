import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AccessLogEntry {
  username: string;
  activityType: string;
  resourceDetail: string;
  occurredAt: string;
  ipAddress: string;
}

@Injectable({ providedIn: 'root' })
export class SecurityLogsService {
  private readonly api = 'http://localhost:8080/api/security';

  constructor(private http: HttpClient) {}

  getAccessLogs(limit = 200): Observable<AccessLogEntry[]> {
    return this.http.get<AccessLogEntry[]>(`${this.api}/access-logs`, {
      params: { limit: String(limit) }
    });
  }
}
