export interface ChecklistInfo {
  checklistId: number;
  name: string;
  progress: number;
  totalTasks: number;
  completedTasks: number;
  status: string; // "In Progress" | "Completed"
}

export interface TaskInfo {
  id: number;
  title: string;
  status: string;
  priority: string;
  checklistName: string;
  completionPercent: number;
  completed: boolean;
}

export interface Dashboard {
  progress: number;
  assignedChecklists: ChecklistInfo[];
  completedChecklists: ChecklistInfo[];
  claimedTasks: TaskInfo[];
  notifications: string[];
}

export interface AdminDashboardSummary {
  totalChecklists: number;
  totalTasks: number;
  completedTasks: number;
  pendingTasks: number;
  completedChecklists: number;
}
