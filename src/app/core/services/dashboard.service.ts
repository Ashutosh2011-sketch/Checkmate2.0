import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Dashboard } from '../models/dashboard.model';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  getDashboardData(): Observable<Dashboard> {
    return of({
      progress: 65,
      assignedChecklists: [
        'IT Onboarding Checklist',
        'Compliance Review'
      ],
      claimedTasks: [
        'Audit Preparation'
      ],
      notifications: [
        'New task assigned',
        'Checklist deadline tomorrow'
      ]
    });
  }
}
