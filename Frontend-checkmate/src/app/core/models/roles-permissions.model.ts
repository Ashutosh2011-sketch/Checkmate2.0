// roles-permissions.model.ts

export interface Permission {
  id: string;
  name: string; // e.g., 'Create Checklists'
  category: string; // e.g., 'Checklist Permissions'
  isEnabled: boolean;
}

export interface Role {
  id: string;
  name: string;
  description?: string;
  permissions: Permission[];
}

// A helper to create a deep copy of permissions for change tracking
export function clonePermissions(permissions: Permission[]): Permission[] {
  return permissions.map(p => ({ ...p }));
}

// Mock data for initial state
export const MOCK_ALL_PERMISSIONS: Permission[] = [
  { id: 'p1', name: 'Create Checklists', category: 'Checklist Permissions', isEnabled: false },
  { id: 'p2', name: 'Publish Workflows', category: 'Checklist Permissions', isEnabled: false },
  { id: 'p3', name: 'Manage Users', category: 'User Management Permissions', isEnabled: false },
  { id: 'p4', name: 'Access Audit Logs', category: 'User Management Permissions', isEnabled: false },
  { id: 'p5', name: 'View All Reports', category: 'Reporting Permissions', isEnabled: false },
  { id: 'p6', name: 'Publish Workflows', category: 'Workflow Permissions', isEnabled: false },
];

export const MOCK_ROLES: Role[] = [
  {
    id: 'r1',
    name: 'Approver',
    permissions: clonePermissions(MOCK_ALL_PERMISSIONS).map(p => {
      if (['p1', 'p2', 'p3', 'p5', 'p6'].includes(p.id)) p.isEnabled = true;
      return p;
    })
  },
  {
    id: 'r2',
    name: 'Reviewer',
    permissions: clonePermissions(MOCK_ALL_PERMISSIONS).map(p => {
      if (['p1', 'p5'].includes(p.id)) p.isEnabled = true;
      return p;
    })
  },
  {
    id: 'r3',
    name: 'Executor',
    permissions: clonePermissions(MOCK_ALL_PERMISSIONS).map(p => {
      if (['p1'].includes(p.id)) p.isEnabled = true;
      return p;
    })
  }
];