import { Component, OnInit } from '@angular/core';
import { DashboardService } from '../../core/services/dashboard.service';
import { Dashboard, ChecklistInfo, TaskInfo } from '../../core/models/dashboard.model';

@Component({
  selector: 'app-user-dashboard',
  templateUrl: './user-dashboard.component.html',
  styleUrls: ['./user-dashboard.component.css']
})
export class UserDashboardComponent implements OnInit {

  dashboard: Dashboard | null = null;
  userName: string = '';

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.userName = localStorage.getItem('userName') || 'User';
    this.loadDashboard();
  }

  loadDashboard(): void {
    this.dashboardService.getDashboardData(this.userName)
      .subscribe({
        next: (data: Dashboard) => {
          console.log('Dashboard:', data);
          this.dashboard = data;
        },
        error: (err) => {
          console.error('Error:', err);
        }
      });
  }

  // Dynamic conic-gradient for progress circle
  getProgressGradient(): string {
    const progress = this.dashboard?.progress || 0;
    return `conic-gradient(#2a5298 0% ${progress}%, #e6e6e6 ${progress}% 100%)`;
  }

  // Badge class based on priority
  getPriorityClass(priority: string): string {
    if (!priority) return 'badge-default';
    switch (priority.toLowerCase()) {
      case 'high': return 'badge-high';
      case 'medium': return 'badge-medium';
      case 'low': return 'badge-low';
      default: return 'badge-default';
    }
  }

  // Status badge class
  getStatusClass(status: string): string {
    if (!status) return 'status-pending';
    switch (status.toLowerCase()) {
      case 'completed': return 'status-completed';
      case 'in progress': return 'status-inprogress';
      case 'pending': return 'status-pending';
      default: return 'status-pending';
    }
  }
}
