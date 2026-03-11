export interface Checklist {
  id: string;
  title: string;
  description?: string;
  category?: string;
  status: 'draft' | 'active' | 'completed' | 'archived';
  createdBy: string;
  assignedTo?: string[];
  dueDate?: Date;
  createdAt: Date;
  updatedAt: Date;
  items: ChecklistItem[];
}

export interface ChecklistItem {
  id: string;
  text: string;
  completed: boolean;
  completedAt?: Date;
  completedBy?: string;
  order: number;
}

export interface ChecklistTemplate {
  id: string;
  name: string;
  category: string;
  description: string;
  items: string[];
  createdAt: Date;
}
