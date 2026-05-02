import { Component, OnInit, OnDestroy } from '@angular/core';
import { DashboardService } from '../../core/services/dashboard.service';
import { Dashboard, ChecklistInfo, TaskInfo, TaskComment, TaskAttachment } from '../../core/models/dashboard.model';
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

  // ==================== COLLABORATION STATE ====================
  expandedTaskId: number | null = null;
  activeCollabTab: string = 'comments'; // 'comments' | 'attachments'

  // Comments
  taskComments: TaskComment[] = [];
  newCommentText: string = '';
  loadingComments: boolean = false;

  // Attachments
  taskAttachments: TaskAttachment[] = [];
  loadingAttachments: boolean = false;

  // Counts cache: taskId -> {commentCount, attachmentCount}
  collabCounts: Map<number, { commentCount: number; attachmentCount: number }> = new Map();

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
          // Load collaboration counts for all tasks
          this.loadAllCollabCounts();
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
    this.closeCollabPanel();
  }

  get displayedChecklists(): ChecklistInfo[] {
    if (!this.dashboard) return [];

    return this.activeTab === 'inprogress'
      ? (this.dashboard.assignedChecklists || [])
      : (this.dashboard.completedChecklists || []);
  }

  selectChecklist(checklist: ChecklistInfo): void {
    this.selectedChecklist = checklist;
    this.closeCollabPanel();

    this.checklistTasks = (this.dashboard?.claimedTasks || [])
      .filter(t => t.checklistName === checklist.name);
  }

  closeDetail(): void {
    this.selectedChecklist = null;
    this.checklistTasks = [];
    this.closeCollabPanel();
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

  // ================= COLLABORATION =================

  loadAllCollabCounts(): void {
    const tasks = this.dashboard?.claimedTasks || [];
    for (const task of tasks) {
      this.dashboardService.getCollaborationCounts(task.id).subscribe({
        next: (counts) => {
          this.collabCounts.set(task.id, counts);
        },
        error: () => {
          this.collabCounts.set(task.id, { commentCount: 0, attachmentCount: 0 });
        }
      });
    }
  }

  getCommentCount(taskId: number): number {
    return this.collabCounts.get(taskId)?.commentCount || 0;
  }

  getAttachmentCount(taskId: number): number {
    return this.collabCounts.get(taskId)?.attachmentCount || 0;
  }

  toggleCollabPanel(task: TaskInfo): void {
    if (this.expandedTaskId === task.id) {
      this.closeCollabPanel();
    } else {
      this.expandedTaskId = task.id;
      this.activeCollabTab = 'comments';
      this.loadComments(task.id);
      this.loadAttachments(task.id);
    }
  }

  closeCollabPanel(): void {
    this.expandedTaskId = null;
    this.taskComments = [];
    this.taskAttachments = [];
    this.newCommentText = '';
  }

  setCollabTab(tab: string): void {
    this.activeCollabTab = tab;
  }

  // --- Comments ---
  loadComments(taskId: number): void {
    this.loadingComments = true;
    this.dashboardService.getComments(taskId).subscribe({
      next: (comments) => {
        this.taskComments = comments;
        this.loadingComments = false;
      },
      error: (err) => {
        console.error('Error loading comments:', err);
        this.taskComments = [];
        this.loadingComments = false;
      }
    });
  }

  addComment(): void {
    if (!this.expandedTaskId || !this.newCommentText.trim()) return;

    this.dashboardService.addComment(this.expandedTaskId, this.newCommentText.trim(), this.userName)
      .subscribe({
        next: (comment) => {
          this.taskComments.unshift(comment);
          this.newCommentText = '';
          // Update count
          const counts = this.collabCounts.get(this.expandedTaskId!) || { commentCount: 0, attachmentCount: 0 };
          counts.commentCount++;
          this.collabCounts.set(this.expandedTaskId!, counts);
        },
        error: (err) => console.error('Error adding comment:', err)
      });
  }

  deleteComment(commentId: number): void {
    if (!this.expandedTaskId) return;

    this.dashboardService.deleteComment(commentId).subscribe({
      next: () => {
        this.taskComments = this.taskComments.filter(c => c.id !== commentId);
        const counts = this.collabCounts.get(this.expandedTaskId!) || { commentCount: 0, attachmentCount: 0 };
        counts.commentCount = Math.max(0, counts.commentCount - 1);
        this.collabCounts.set(this.expandedTaskId!, counts);
      },
      error: (err) => console.error('Error deleting comment:', err)
    });
  }

  // --- Attachments ---
  loadAttachments(taskId: number): void {
    this.loadingAttachments = true;
    this.dashboardService.getAttachments(taskId).subscribe({
      next: (attachments) => {
        this.taskAttachments = attachments;
        this.loadingAttachments = false;
      },
      error: (err) => {
        console.error('Error loading attachments:', err);
        this.taskAttachments = [];
        this.loadingAttachments = false;
      }
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length || !this.expandedTaskId) return;

    const file = input.files[0];

    // 10MB limit
    if (file.size > 10 * 1024 * 1024) {
      alert('File size exceeds 10MB limit');
      return;
    }

    this.dashboardService.uploadAttachment(this.expandedTaskId, file, this.userName)
      .subscribe({
        next: (attachment) => {
          this.taskAttachments.push(attachment);
          const counts = this.collabCounts.get(this.expandedTaskId!) || { commentCount: 0, attachmentCount: 0 };
          counts.attachmentCount++;
          this.collabCounts.set(this.expandedTaskId!, counts);
          // Reset file input
          input.value = '';
        },
        error: (err) => {
          console.error('Error uploading file:', err);
          alert('Error uploading file. Please try again.');
        }
      });
  }

  downloadFile(attachment: TaskAttachment): void {
    this.dashboardService.downloadAttachment(attachment.id).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = attachment.fileName;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => console.error('Error downloading file:', err)
    });
  }

  deleteAttachment(attachmentId: number): void {
    if (!this.expandedTaskId) return;

    this.dashboardService.deleteAttachment(attachmentId).subscribe({
      next: () => {
        this.taskAttachments = this.taskAttachments.filter(a => a.id !== attachmentId);
        const counts = this.collabCounts.get(this.expandedTaskId!) || { commentCount: 0, attachmentCount: 0 };
        counts.attachmentCount = Math.max(0, counts.attachmentCount - 1);
        this.collabCounts.set(this.expandedTaskId!, counts);
      },
      error: (err) => console.error('Error deleting attachment:', err)
    });
  }

  isImageFile(fileType: string): boolean {
    return fileType ? fileType.startsWith('image/') : false;
  }

  formatFileSize(bytes: number): string {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
  }

  getFileIcon(fileType: string): string {
    if (!fileType) return '📄';
    if (fileType.startsWith('image/')) return '🖼️';
    if (fileType.includes('pdf')) return '📕';
    if (fileType.includes('word') || fileType.includes('document')) return '📘';
    if (fileType.includes('sheet') || fileType.includes('excel')) return '📊';
    if (fileType.includes('zip') || fileType.includes('rar')) return '📦';
    return '📄';
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