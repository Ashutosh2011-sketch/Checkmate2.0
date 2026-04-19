// roles-permissions.model.ts

export interface Permission {
  id: number;
  name: string;       // e.g., 'Create Checklists'
  category: string;   // e.g., 'Checklist Permissions'
  isEnabled: boolean;
}

export interface Role {
  id: number;
  name: string;
  description?: string;
  permissions: Permission[];
}

// A helper to create a deep copy of permissions for change tracking
export function clonePermissions(permissions: Permission[]): Permission[] {
  return permissions.map(p => ({ ...p }));
}