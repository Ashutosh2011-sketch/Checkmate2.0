import { Component } from '@angular/core';
import { TaskService } from '../../services/task.service';

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
  dependsOn?: number;
}

interface Checklist {
  name: string;
  department: string;
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

  // 🔥 LOAD USER TASKS
  loadFromBackend() {

    const user = localStorage.getItem('userName') || 'Yuvraj';

    this.taskService.getTasksByUser(user)
      .subscribe((data: any[]) => {

        const tasks: Task[] = data.map((t: any): Task => ({
          id: Number(t.id),
          title: t.title,
          completed: t.status === 'Completed',
          workflowType: t.workflowType === 'SEQUENTIAL' ? 'Sequential' : 'Parallel',
          deadline: new Date().toISOString().split('T')[0],
          priority: t.priority as Priority,
          assignedTo: t.assignees?.[0],
          dependsOn: t.dependsOn ? Number(t.dependsOn) : undefined
        }));

        this.checklists = [{
          name: 'Employee Onboarding',
          department: 'HR',
          tasks
        }];
      });
  }

  // 🔥 DEPENDENCY LOCK CHECK
  isTaskLocked(task: Task, checklist: Checklist): boolean {

  if (!task.dependsOn) return false;

  let currentDependency: number | undefined = task.dependsOn;

  while (currentDependency !== undefined) {

    const prevTask = checklist.tasks.find(t => t.id === currentDependency);

    if (!prevTask) return false;

    // ❌ if ANY previous task incomplete → LOCK
    if (!prevTask.completed) return true;

    // move to next dependency safely
    currentDependency = prevTask.dependsOn;
  }

  return false;
}

  // 🔥 SORT → UNLOCKED TASK FIRST
  getOrderedTasks(checklist: Checklist): Task[] {
    return [...checklist.tasks].sort((a, b) => {
      const aLocked = this.isTaskLocked(a, checklist);
      const bLocked = this.isTaskLocked(b, checklist);

      if (aLocked === bLocked) return 0;
      return aLocked ? 1 : -1;
    });
  }

  // 🔥 TOGGLE TASK (DB + UI SYNC)
  toggleTask(checklist: Checklist, task: Task) {

    const prev = checklist.tasks.find(t => t.id === task.dependsOn);

    if (prev && !prev.completed) return;

    // UI update
    task.completed = !task.completed;

    // Backend update
    fetch(`http://localhost:8080/api/tasks/toggle/${task.id}`, {
      method: 'PUT'
    });
  }

  getProgress(checklist: Checklist): number {
    const completed = checklist.tasks.filter(t => t.completed).length;
    return Math.round((completed / checklist.tasks.length) * 100);
  }

  getStatus(checklist: Checklist): string {
    const progress = this.getProgress(checklist);
    if (progress === 100) return 'Completed';
    if (progress > 0) return 'In Progress';
    return 'Pending';
  }
}