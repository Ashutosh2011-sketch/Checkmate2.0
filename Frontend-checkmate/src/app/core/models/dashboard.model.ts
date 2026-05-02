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
  completedAt?: string | null;
  completedBy?: string | null;
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

// ==================== COLLABORATION ====================

export interface TaskComment {
  id: number;
  taskId: number;
  authorName: string;
  content: string;
  createdAt: string;
}

export interface TaskAttachment {
  id: number;
  taskId: number;
  fileName: string;
  fileType: string;
  fileSize: number;
  uploadedBy: string;
  uploadedAt: string;
}

export interface CollaborationCounts {
  commentCount: number;
  attachmentCount: number;
}
