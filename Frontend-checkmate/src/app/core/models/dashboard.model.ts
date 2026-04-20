export interface ChecklistInfo {
  name: string;
  progress: number;
  totalTasks: number;
  completedTasks: number;
}

export interface TaskInfo {
  id: number;
  title: string;
  status: string;
  priority: string;
  checklistName: string;
}

export interface Dashboard {
  progress: number;
  assignedChecklists: ChecklistInfo[];
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
