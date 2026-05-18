import { Component } from '@angular/core';
import { TaskService } from '../../services/task.service';
import { environment } from '../../../../environments/environment';

type WorkflowType = 'Sequential' | 'Parallel';
type Priority = 'High' | 'Medium' | 'Low';

interface Task {
  id: number;
  title: string;
  completed: boolean;
  workflowType: WorkflowType;
  deadline: string;
  priority: Priority;
  assignedTo?: string;
  assignees: string[];
  dependsOn?: string;
  conditionDependentOn?: string;
  conditionExpectedOutcome?: string;
  sortOrder: number;
}

interface Checklist {
  id: number;
  name: string;
  department: string;
  visibility: string;
  workflowType: WorkflowType;
  tasks: Task[];
}

@Component({
  selector: 'app-checklist-tracker',
  templateUrl: './checklist-tracker.component.html',
  styleUrls: ['./checklist-tracker.component.css']
})
export class ChecklistTrackerComponent {

  currentUser = localStorage.getItem('userName') || 'User';
  checklists: Checklist[] = [];

  constructor(private taskService: TaskService) {
    this.loadFromBackend();
  }

  loadFromBackend(): void {
    const user = localStorage.getItem('userName') || this.currentUser;
    this.currentUser = user;

    this.taskService.getTasksByUser(user).subscribe((data: any[]) => {
      const grouped = new Map<number, Checklist>();
      const currentUserName = this.getAssigneeName(user);

      data
        .filter((t: any) =>
          this.cleanAssignees(t.assignees || [])
            .some(assignee => this.getAssigneeName(assignee) === currentUserName)
        )
        .forEach((t: any) => {
          const checklistId = Number(t.checklistId || 0);
          const workflowType = this.normalizeWorkflowType(t.workflowType);

          if (!grouped.has(checklistId)) {
            grouped.set(checklistId, {
              id: checklistId,
              name: t.checklistName || 'Checklist',
              department: t.department || 'General',
              visibility: t.visibility || 'Private',
              workflowType,
              tasks: []
            });
          }

          const assignees = this.cleanAssignees(t.assignees || []);

          grouped.get(checklistId)?.tasks.push({
            id: Number(t.id),
            title: t.title,
            completed: t.status === 'Completed',
            workflowType,
            deadline: new Date().toISOString().split('T')[0],
            priority: (t.priority || 'Medium') as Priority,
            assignedTo: assignees[0],
            assignees,
            dependsOn: this.cleanDependency(t.dependsOn),
            conditionDependentOn: this.cleanDependency(t.conditionDependentOn),
            conditionExpectedOutcome: this.cleanDependency(t.conditionExpectedOutcome),
            sortOrder: Number(t.sortOrder || 0)
          });
        });

      this.checklists = Array.from(grouped.values()).map(checklist => ({
        ...checklist,
        tasks: checklist.tasks.sort((a, b) => a.sortOrder - b.sortOrder || a.id - b.id)
      }));
    });
  }

  getOrderedTasks(checklist: Checklist): Task[] {
    return [...checklist.tasks].sort((a, b) => a.sortOrder - b.sortOrder || a.id - b.id);
  }

  isTaskAssignedToCurrentUser(task: Task): boolean {
    return task.assignees.some(assignee => this.getAssigneeName(assignee) === this.getCurrentUserName());
  }

  isTaskLocked(task: Task, checklist: Checklist): boolean {
    if (this.isBlockedBySequentialOrder(task, checklist)) {
      return true;
    }

    if (this.isBlockedByParallelCondition(task, checklist)) {
      return true;
    }

    return !this.isTaskAssignedToCurrentUser(task);
  }

  getLockMessage(task: Task, checklist: Checklist): string {
    if (this.isBlockedBySequentialOrder(task, checklist)) {
      return 'Complete previous task first';
    }

    if (this.isBlockedByParallelCondition(task, checklist)) {
      return 'Waiting for dependent task to complete';
    }

    if (!this.isTaskAssignedToCurrentUser(task)) {
      return `This task is assigned to ${task.assignees.join(', ') || 'another user'}`;
    }

    return '';
  }

  toggleTask(checklist: Checklist, task: Task): void {
    if (this.isTaskLocked(task, checklist)) {
      return;
    }

    task.completed = !task.completed;

    fetch(`${environment.apiUrl}/tasks/toggle/${task.id}`, {
      method: 'PUT'
    }).catch(() => {
      task.completed = !task.completed;
    });
  }

  getProgress(checklist: Checklist): number {
    if (!checklist.tasks.length) {
      return 0;
    }

    const completed = checklist.tasks.filter(t => t.completed).length;
    return Math.round((completed / checklist.tasks.length) * 100);
  }

  getStatus(checklist: Checklist): string {
    const progress = this.getProgress(checklist);
    if (progress === 100) return 'Completed';
    if (progress > 0) return 'In Progress';
    return 'Pending';
  }

  private isBlockedBySequentialOrder(task: Task, checklist: Checklist): boolean {
    if (checklist.workflowType !== 'Sequential') {
      return false;
    }

    const orderedTasks = this.getOrderedTasks(checklist);
    const taskIndex = orderedTasks.findIndex(t => t.id === task.id);

    if (taskIndex <= 0) {
      return false;
    }

    return orderedTasks.slice(0, taskIndex).some(previousTask => !previousTask.completed);
  }

  private isBlockedByParallelCondition(task: Task, checklist: Checklist): boolean {
    if (checklist.workflowType !== 'Parallel') {
      return false;
    }

    const dependencyTitle = task.conditionDependentOn || task.dependsOn;

    if (!dependencyTitle) {
      return false;
    }

    const dependencyTask = checklist.tasks.find(existingTask =>
      String(existingTask.id) === dependencyTitle ||
      existingTask.title.toLowerCase() === dependencyTitle.toLowerCase()
    );

    return dependencyTask ? !dependencyTask.completed : false;
  }

  private cleanAssignees(assignees: string[]): string[] {
    return assignees
      .map(assignee => String(assignee || '').trim())
      .filter(Boolean);
  }

  private getCurrentUserName(): string {
    return this.getAssigneeName(this.currentUser);
  }

  private getAssigneeName(assignee: string): string {
    return String(assignee || '')
      .split('(')[0]
      .trim()
      .toLowerCase();
  }

  private cleanDependency(value: any): string | undefined {
    const dependency = String(value || '').trim();

    if (!dependency || dependency.toLowerCase() === 'none') {
      return undefined;
    }

    return dependency;
  }

  private normalizeWorkflowType(value: string): WorkflowType {
    return String(value || '').toLowerCase() === 'parallel' ? 'Parallel' : 'Sequential';
  }
}
