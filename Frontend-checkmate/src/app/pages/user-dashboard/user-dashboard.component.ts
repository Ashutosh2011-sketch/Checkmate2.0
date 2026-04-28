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

  // Tab state: 'inprogress' or 'completed'
  activeTab: string = 'inprogress';

  // Selected checklist for detail view
  selectedChecklist: ChecklistInfo | null = null;

  // Tasks for the selected checklist
  checklistTasks: TaskInfo[] = [];

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

  // Switch tab
  setTab(tab: string): void {
    this.activeTab = tab;
    this.selectedChecklist = null;
    this.checklistTasks = [];
  }

  // Get checklists for active tab
  get displayedChecklists(): ChecklistInfo[] {
    if (!this.dashboard) return [];
    return this.activeTab === 'inprogress'
      ? (this.dashboard.assignedChecklists || [])
      : (this.dashboard.completedChecklists || []);
  }

  // Select a checklist -> show its tasks
  selectChecklist(checklist: ChecklistInfo): void {
    this.selectedChecklist = checklist;
    // Filter claimed tasks by checklist name
    this.checklistTasks = (this.dashboard?.claimedTasks || [])
      .filter(t => t.checklistName === checklist.name);
  }

  // Close task detail panel
  closeDetail(): void {
    this.selectedChecklist = null;
    this.checklistTasks = [];
  }

  // Update task completion percentage
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

  // Mark a single task as complete
  markTaskComplete(task: TaskInfo): void {
    this.dashboardService.markTaskComplete(task.id).subscribe({
      next: (updated) => {
        task.completionPercent = 100;
        task.completed = true;
        task.status = 'Completed';
        this.refreshChecklistProgress();
      },
      error: (err) => console.error('Error marking task complete:', err)
    });
  }

  // Mark entire checklist as completed
  markChecklistComplete(): void {
    if (!this.selectedChecklist?.checklistId) return;
    this.dashboardService.markChecklistComplete(this.selectedChecklist.checklistId).subscribe({
      next: () => {
        // Reload everything
        this.selectedChecklist = null;
        this.checklistTasks = [];
        this.loadDashboard();
      },
      error: (err) => console.error('Error marking checklist complete:', err)
    });
  }

  // Recalculate checklist progress after a task update
  private refreshChecklistProgress(): void {
    if (!this.selectedChecklist) return;
    const tasks = this.checklistTasks;
    const total = tasks.length;
    if (total === 0) return;

    let sumPercent = 0;
    let completedCount = 0;
    for (const t of tasks) {
      sumPercent += t.completed ? 100 : t.completionPercent;
      if (t.completed) completedCount++;
    }

    this.selectedChecklist.progress = Math.round(sumPercent / total);
    this.selectedChecklist.completedTasks = completedCount;

    // If all tasks completed, move checklist to completed list
    if (completedCount === total) {
      this.selectedChecklist.status = 'Completed';
      this.selectedChecklist.progress = 100;
      // Reload to move it to the right list
      this.loadDashboard();
      this.selectedChecklist = null;
      this.checklistTasks = [];
    }

    // Recalculate overall progress
    if (this.dashboard) {
      const allTasks = this.dashboard.claimedTasks;
      const totalP = allTasks.reduce((sum, t) => sum + (t.completed ? 100 : t.completionPercent), 0);
      this.dashboard.progress = allTasks.length > 0 ? Math.round(totalP / allTasks.length) : 0;
    }
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
