import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ChecklistService } from '../../core/services/checklist.service';
import { DashboardService } from '../../core/services/dashboard.service';
import { TaskComment, TaskAttachment } from '../../core/models/dashboard.model';

@Component({
  selector: 'app-checklist-detail',
  templateUrl: './checklist-detail.component.html',
  styleUrls: ['./checklist-detail.component.css']
})
export class ChecklistDetailComponent implements OnInit {

  checklistId: number = 0;
  tasks: any[] = [];
  groupedTasks: any[] = [];
  progress: number = 0;

  searchText: string = '';

  statusFilters: string[] = ['All', 'Pending', 'Completed', 'In Progress'];
  activeStatus: string = 'All';

  priorityFilters: string[] = ['All', 'High', 'Medium', 'Low'];
  activePriority: string = 'All';

  workflowType: string = 'PARALLEL';

  // ==================== COLLABORATION STATE ====================
  expandedTaskId: number | null = null;
  activeCollabTab: string = 'comments';
  taskComments: TaskComment[] = [];
  taskAttachments: TaskAttachment[] = [];
  loadingComments: boolean = false;
  loadingAttachments: boolean = false;
  newCommentText: string = '';
  userName: string = '';

  // Counts cache
  collabCounts: Map<number, { commentCount: number; attachmentCount: number }> = new Map();

  constructor(
    private route: ActivatedRoute,
    private checklistService: ChecklistService,
    private dashboardService: DashboardService
  ) {}

  ngOnInit() {
    this.checklistId = Number(this.route.snapshot.paramMap.get('id'));
    this.userName = localStorage.getItem('userName') || 'Admin';
    this.loadTasks();
  }

  loadTasks() {
    this.checklistService.getTasksByChecklist(this.checklistId).subscribe({
      next: (data: any[]) => {

        this.tasks = data;

        if (data.length && data[0].workflowType) {
          this.workflowType = data[0].workflowType;
        }

        this.applyFilters();
        this.calculateProgress();
        this.loadAllCollabCounts();
      }
    });
  }

  applyFilters() {
    let filtered = [...this.tasks];

    if (this.activeStatus !== 'All') {
      filtered = filtered.filter(t => t.status === this.activeStatus);
    }

    if (this.activePriority !== 'All') {
      filtered = filtered.filter(t => t.priority === this.activePriority);
    }

    if (this.searchText.trim()) {
      const search = this.searchText.toLowerCase();
      filtered = filtered.filter(t =>
        t.title?.toLowerCase().includes(search) ||
        t.description?.toLowerCase().includes(search)
      );
    }

    this.groupTasks(filtered);
  }

  isTaskLocked(task: any): boolean {

    // Dependency check
    if (task.dependsOn && task.dependsOn !== 'None') {
      const parent = this.tasks.find(t => t.title === task.dependsOn);
      if (!parent || parent.status !== 'Completed') return true;
    }

    // Sequential logic
    if (this.workflowType === 'SEQUENTIAL') {

      const sorted = [...this.tasks].sort((a, b) => a.sortOrder - b.sortOrder);
      const firstIncomplete = sorted.find(t => t.status !== 'Completed');

      if (!firstIncomplete) return false;

      const indexTask = sorted.findIndex(t => t.id === task.id);
      const indexFirst = sorted.findIndex(t => t.id === firstIncomplete.id);

      if (indexTask > indexFirst) return true;
    }

    return false;
  }

  groupTasks(taskList: any[]) {
    const sorted = [...taskList].sort((a, b) => a.sortOrder - b.sortOrder);

    const map = new Map<string, any[]>();

    sorted.forEach(task => {
      const section = task.sectionName || 'General';
      if (!map.has(section)) map.set(section, []);
      map.get(section)?.push(task);
    });

    this.groupedTasks = Array.from(map.entries()).map(([key, value]) => ({
      key,
      value,
      expanded: true
    }));
  }

  toggleSection(section: any) {
    section.expanded = !section.expanded;
  }

  calculateProgress() {
    const completed = this.tasks.filter(
      t => t.status?.toLowerCase() === 'completed'
    ).length;

    this.progress = this.tasks.length
      ? Math.round((completed / this.tasks.length) * 100)
      : 0;
  }

  formatDays(days: number): string {
    return days === 1 ? '1 day' : `${days} days`;
  }

  getStatusClass(status: string) {
    if (status === 'Completed') return 'status-completed';
    if (status === 'In Progress') return 'status-progress';
    return 'status-pending';
  }

  getPriorityClass(priority: string) {
    if (priority === 'High') return 'priority-high';
    if (priority === 'Medium') return 'priority-medium';
    return 'priority-low';
  }

  setStatusFilter(f: string) {
    this.activeStatus = f;
    this.applyFilters();
  }

  setPriorityFilter(f: string) {
    this.activePriority = f;
    this.applyFilters();
  }

  resetFilters() {
    this.searchText = '';
    this.activeStatus = 'All';
    this.activePriority = 'All';
    this.applyFilters();
  }

  // ==================== COLLABORATION ====================

  loadAllCollabCounts(): void {
    for (const task of this.tasks) {
      this.dashboardService.getCollaborationCounts(task.id).subscribe({
        next: (counts) => this.collabCounts.set(task.id, counts),
        error: () => this.collabCounts.set(task.id, { commentCount: 0, attachmentCount: 0 })
      });
    }
  }

  getCommentCount(taskId: number): number {
    return this.collabCounts.get(taskId)?.commentCount || 0;
  }

  getAttachmentCount(taskId: number): number {
    return this.collabCounts.get(taskId)?.attachmentCount || 0;
  }

  toggleCollabPanel(task: any): void {
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
      error: () => {
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
      error: () => {
        this.taskAttachments = [];
        this.loadingAttachments = false;
      }
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length || !this.expandedTaskId) return;

    const file = input.files[0];
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
          input.value = '';
        },
        error: (err) => {
          console.error('Error uploading file:', err);
          alert('Error uploading file.');
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

  getFileIcon(fileType: string): string {
    if (!fileType) return '📄';
    if (fileType.startsWith('image/')) return '🖼️';
    if (fileType.includes('pdf')) return '📕';
    if (fileType.includes('word') || fileType.includes('document')) return '📘';
    if (fileType.includes('sheet') || fileType.includes('excel')) return '📊';
    if (fileType.includes('zip') || fileType.includes('rar')) return '📦';
    return '📄';
  }

  formatFileSize(bytes: number): string {
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
  }
}