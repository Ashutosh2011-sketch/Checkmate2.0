import { Component, OnInit, OnDestroy } from '@angular/core';
import { DashboardService } from '../../core/services/dashboard.service';
import { Dashboard, ChecklistInfo, TaskInfo } from '../../core/models/dashboard.model';
import { NotificationService } from '../../core/services/notification.service';
import { interval, Subscription } from 'rxjs';

@Component({
  selector: 'app-user-dashboard',
  templateUrl: './user-dashboard.component.html',
  styleUrls: ['./user-dashboard.component.css']
})
export class UserDashboardComponent implements OnInit, OnDestroy {

  dashboard: Dashboard | null = null;
  userName: string = '';
  notifications: any[] = [];

  private pollingSub?: Subscription;

  // Tab state
  activeTab: string = 'inprogress';

  // Selected checklist
  selectedChecklist: ChecklistInfo | null = null;
  checklistTasks: TaskInfo[] = [];

  constructor(
    private dashboardService: DashboardService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.userName = localStorage.getItem('userName') || 'User';

    this.loadDashboard();
    this.loadNotifications();

    // Poll every 30 seconds
    this.pollingSub = interval(30000).subscribe(() => {
      this.loadDashboard();
      this.loadNotifications();
    });
  }

  // ================= DASHBOARD =================
  loadDashboard(): void {
    this.dashboardService.getDashboardData(this.userName)
      .subscribe({
        next: (data: Dashboard) => {
          console.log('Dashboard:', data);
          this.dashboard = data;
        },
        error: (err) => console.error('Dashboard Error:', err)
      });
  }

  // ================= NOTIFICATIONS =================
  loadNotifications(): void {
    this.notificationService.getNotifications().subscribe({
      next: (data) => {
        console.log('Notifications:', data);
        this.notifications = data;
      },
      error: (err) => console.error('Notification Error:', err)
    });
  }

  markAsRead(id: number): void {
    this.notificationService.markAsRead(id).subscribe({
      next: () => {
        this.notifications = this.notifications.filter(n => n.id !== id);
      },
      error: (err) => console.error('Error marking notification:', err)
    });
  }

  // ================= UI LOGIC =================
  setTab(tab: string): void {
    this.activeTab = tab;
    this.selectedChecklist = null;
    this.checklistTasks = [];
  }

  get displayedChecklists(): ChecklistInfo[] {
    if (!this.dashboard) return [];

    return this.activeTab === 'inprogress'
      ? (this.dashboard.assignedChecklists || [])
      : (this.dashboard.completedChecklists || []);
  }

  selectChecklist(checklist: ChecklistInfo): void {
    this.selectedChecklist = checklist;

    this.checklistTasks = (this.dashboard?.claimedTasks || [])
      .filter(t => t.checklistName === checklist.name);
  }

  closeDetail(): void {
    this.selectedChecklist = null;
    this.checklistTasks = [];
  }

  // ================= TASK ACTIONS =================
  updateTaskPercent(task: TaskInfo, event: Event): void {
    const input = event.target as HTMLInputElement;
    const percent = parseInt(input.value, 10);

    this.dashboardService.updateTaskStatus(task.id, percent).subscribe({
      next: (updated) => {
        task.completionPercent = updated.completionPercent;
        task.completed = updated.completed;
        task.status = updated.status;
        this.refreshChecklistProgress();
      },
      error: (err) => console.error('Error updating task:', err)
    });
  }

  markTaskComplete(task: TaskInfo): void {
    this.dashboardService.markTaskComplete(task.id).subscribe({
      next: () => {
        task.completionPercent = 100;
        task.completed = true;
        task.status = 'Completed';
        this.refreshChecklistProgress();
      },
      error: (err) => console.error('Error marking task complete:', err)
    });
  }

  markChecklistComplete(): void {
    if (!this.selectedChecklist?.checklistId) return;

    this.dashboardService.markChecklistComplete(this.selectedChecklist.checklistId)
      .subscribe({
        next: () => {
          this.selectedChecklist = null;
          this.checklistTasks = [];
          this.loadDashboard();
        },
        error: (err) => console.error('Error marking checklist complete:', err)
      });
  }

  // ================= PROGRESS CALC =================
  private refreshChecklistProgress(): void {
    if (!this.selectedChecklist) return;

    const tasks = this.checklistTasks;
    const total = tasks.length;
    if (total === 0) return;

    let sum = 0;
    let completed = 0;

    for (const t of tasks) {
      sum += t.completed ? 100 : (t.completionPercent || 0);
      if (t.completed) completed++;
    }

    this.selectedChecklist.progress = Math.round(sum / total);
    this.selectedChecklist.completedTasks = completed;

    // Move to completed if all tasks done
    if (completed === total) {
      this.selectedChecklist.status = 'Completed';
      this.selectedChecklist.progress = 100;

      this.loadDashboard();
      this.selectedChecklist = null;
      this.checklistTasks = [];
    }

    // Update overall dashboard progress
    if (this.dashboard) {
      const all = this.dashboard.claimedTasks || [];
      const totalP = all.reduce(
        (s, t) => s + (t.completed ? 100 : (t.completionPercent || 0)),
        0
      );

      this.dashboard.progress = all.length
        ? Math.round(totalP / all.length)
        : 0;
    }
  }

  // ================= UI HELPERS =================
  getProgressGradient(): string {
    const progress = this.dashboard?.progress || 0;
    return `conic-gradient(#2a5298 0% ${progress}%, #e6e6e6 ${progress}% 100%)`;
  }

  getPriorityClass(priority: string): string {
    if (!priority) return 'badge-default';

    switch (priority.toLowerCase()) {
      case 'high': return 'badge-high';
      case 'medium': return 'badge-medium';
      case 'low': return 'badge-low';
      default: return 'badge-default';
    }
  }

  getStatusClass(status: string): string {
    if (!status) return 'status-pending';

    switch (status.toLowerCase()) {
      case 'completed': return 'status-completed';
      case 'in progress': return 'status-inprogress';
      case 'pending': return 'status-pending';
      default: return 'status-pending';
    }
  }

  // ================= CLEANUP =================
  ngOnDestroy(): void {
    this.pollingSub?.unsubscribe();
  }
}