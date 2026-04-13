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

  groupTasks(taskList: any[]) {
    const grouped: any = {};

    taskList.forEach(task => {
      const section = task.sectionName || 'General';

      if (!grouped[section]) grouped[section] = [];
      grouped[section].push(task);
    });

    this.groupedTasks = Object.keys(grouped).map(key => ({
      key,
      value: grouped[key],
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
}