import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ChecklistService } from '../../core/services/checklist.service';

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

  constructor(
    private route: ActivatedRoute,
    private checklistService: ChecklistService
  ) {}

  ngOnInit() {
    this.checklistId = Number(this.route.snapshot.paramMap.get('id'));
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
}