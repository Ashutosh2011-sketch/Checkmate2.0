import { Component } from '@angular/core';

interface Task {
  id: number;
  title: string;
  completed: boolean;
  workflowType: 'Sequential' | 'Parallel';
  deadline: string;
  comments: string[];
  attachments: string[];
}

interface Checklist {
  name: string;
  department: string;
  tasks: Task[];
  activityLog: string[];
}

@Component({
  selector: 'app-checklist-tracker',
  templateUrl: './checklist-tracker.component.html',
  styleUrls: ['./checklist-tracker.component.css']
})
export class ChecklistTrackerComponent {

  checklists: Checklist[] = [
    {
      name: 'Employee Onboarding',
      department: 'HR',
      activityLog: [],
      tasks: [
        {
          id: 1,
          title: 'Collect Documents',
          completed: true,
          workflowType: 'Sequential',
          deadline: '2026-03-01',
          comments: [],
          attachments: []
        },
        {
          id: 2,
          title: 'System Access Setup',
          completed: false,
          workflowType: 'Sequential',
          deadline: '2026-03-10',
          comments: [],
          attachments: []
        }
      ]
    }
  ];

  // Calculate dynamic progress
  getProgress(checklist: Checklist): number {
    const completed = checklist.tasks.filter(t => t.completed).length;
    return Math.round((completed / checklist.tasks.length) * 100);
  }

  // Determine Status
  getStatus(checklist: Checklist): string {

    const progress = this.getProgress(checklist);

    const hasOverdue = checklist.tasks.some(t =>
      !t.completed && new Date(t.deadline) < new Date()
    );

    if (hasOverdue) return 'Overdue';
    if (progress === 100) return 'Completed';
    if (progress > 0) return 'In Progress';
    return 'Pending';
  }

  toggleTask(checklist: Checklist, task: Task) {

    // Sequential lock logic
    if (task.workflowType === 'Sequential') {
      const previousTask = checklist.tasks
        .filter(t => t.workflowType === 'Sequential')
        .find(t => t.id === task.id - 1);

      if (previousTask && !previousTask.completed) {
        alert("Complete previous task first!");
        return;
      }
    }

    task.completed = !task.completed;

    checklist.activityLog.push(
      `Task "${task.title}" marked as ${task.completed ? 'Completed' : 'Pending'}`
    );
  }

  addComment(task: Task, comment: string) {
    if (!comment) return;
    task.comments.push(comment);
  }

  addAttachment(task: Task, fileName: string) {
    if (!fileName) return;
    task.attachments.push(fileName);
  }

}