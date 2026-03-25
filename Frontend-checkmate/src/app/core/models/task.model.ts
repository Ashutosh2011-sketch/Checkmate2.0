export interface Task {
  id: string;
  title: string;
  description?: string;
  priority: 'low' | 'medium' | 'high' | 'critical';
  status: 'pending' | 'in_progress' | 'completed' | 'cancelled';
  assignedTo: string;
  assignedBy: string;
  dueDate?: Date;
  startDate?: Date;
  completedAt?: Date;
  checklistId?: string;
  createdAt: Date;
  updatedAt: Date;
}

export interface AuditLog {
  id: string;
  action: string;
  entityType: 'checklist' | 'task' | 'user' | 'template' | 'workflow';
  entityId: string;
  userId: string;
  userName: string;
  timestamp: Date;
  details?: string;
  changes?: Record<string, { old: unknown; new: unknown }>;
}

export interface Notification {
  id: string;
  userId: string;
  title: string;
  message: string;
  type: 'info' | 'success' | 'warning' | 'error';
  read: boolean;
  createdAt: Date;
  link?: string;
}
