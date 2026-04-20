import { Component, OnInit } from '@angular/core';
import { RolesPermissionsService, UserPermissionInfo } from '../../core/services/roles-permissions.service';
import { Role, Permission, clonePermissions } from '../../core/models/roles-permissions.model';

@Component({
  selector: 'app-roles-permissions',
  templateUrl: './roles-permissions.component.html',
  styleUrls: ['./roles-permissions.component.css']
})
export class RolesPermissionsComponent implements OnInit {
  roles: Role[] = [];
  selectedRole: Role | null = null;

  // Role-level permission tracking
  originalPermissions: Permission[] = [];
  hasUnsavedChanges = false;

  // Users panel
  roleUsers: UserPermissionInfo[] = [];
  selectedUser: UserPermissionInfo | null = null;
  userPermissions: Permission[] = [];
  originalUserPermissions: Permission[] = [];
  hasUnsavedUserChanges = false;
  userGroupedPermissions: { [category: string]: Permission[] } = {};

  // View mode: 'role' = editing role permissions, 'user' = editing user permissions
  viewMode: 'role' | 'user' = 'role';

  // Drawer
  isAddRoleDrawerOpen = false;

  // Grouped permissions for role view
  groupedPermissions: { [category: string]: Permission[] } = {};

  isLoading = true;

  constructor(private rolesService: RolesPermissionsService) {}

  ngOnInit(): void {
    this.loadRoles();
  }

  loadRoles(): void {
    this.isLoading = true;
    this.rolesService.getAllRoles().subscribe({
      next: (roles) => {
        this.roles = roles;
        this.isLoading = false;
        if (this.roles.length > 0) {
          this.selectRole(this.roles[0]);
        }
      },
      error: (err) => {
        console.error('Error loading roles:', err);
        this.isLoading = false;
      }
    });
  }

  selectRole(role: Role): void {
    if (this.hasUnsavedChanges || this.hasUnsavedUserChanges) {
      const confirmDiscard = confirm('You have unsaved changes. Discard?');
      if (!confirmDiscard) return;
    }
    this.selectedRole = role;
    this.originalPermissions = clonePermissions(role.permissions);
    this.hasUnsavedChanges = false;
    this.viewMode = 'role';
    this.selectedUser = null;
    this.hasUnsavedUserChanges = false;
    this.groupPermissions();
    this.loadUsersForRole(role.name);
  }

  // Load users who have this role
  loadUsersForRole(roleName: string): void {
    this.rolesService.getUsersByRole(roleName).subscribe({
      next: (users) => {
        this.roleUsers = users;
      },
      error: (err) => {
        console.error('Error loading users for role:', err);
        this.roleUsers = [];
      }
    });
  }

  // Select a specific user to view/edit their permissions
  selectUser(user: UserPermissionInfo): void {
    if (this.hasUnsavedUserChanges) {
      const confirmDiscard = confirm('You have unsaved user changes. Discard?');
      if (!confirmDiscard) return;
    }
    this.selectedUser = user;
    this.viewMode = 'user';
    this.hasUnsavedUserChanges = false;

    this.rolesService.getUserPermissions(user.userId).subscribe({
      next: (result) => {
        this.userPermissions = result.permissions;
        this.originalUserPermissions = clonePermissions(result.permissions);
        this.groupUserPermissions();
      },
      error: (err) => {
        console.error('Error loading user permissions:', err);
      }
    });
  }

  // Back to role view
  backToRoleView(): void {
    if (this.hasUnsavedUserChanges) {
      const confirmDiscard = confirm('You have unsaved user changes. Discard?');
      if (!confirmDiscard) return;
    }
    this.viewMode = 'role';
    this.selectedUser = null;
    this.hasUnsavedUserChanges = false;
  }

  groupPermissions(): void {
    if (!this.selectedRole) return;
    this.groupedPermissions = this.selectedRole.permissions.reduce(
      (groups: { [key: string]: Permission[] }, p: Permission) => {
        if (!groups[p.category]) groups[p.category] = [];
        groups[p.category].push(p);
        return groups;
      }, {} as { [category: string]: Permission[] }
    );
  }

  groupUserPermissions(): void {
    this.userGroupedPermissions = this.userPermissions.reduce(
      (groups: { [key: string]: Permission[] }, p: Permission) => {
        if (!groups[p.category]) groups[p.category] = [];
        groups[p.category].push(p);
        return groups;
      }, {} as { [category: string]: Permission[] }
    );
  }

  get permissionCategories(): string[] {
    return Object.keys(this.groupedPermissions);
  }

  get userPermissionCategories(): string[] {
    return Object.keys(this.userGroupedPermissions);
  }

  // Role-level toggle
  onPermissionToggle(permission: Permission): void {
    permission.isEnabled = !permission.isEnabled;
    this.checkForUnsavedChanges();
  }

  // User-level toggle
  onUserPermissionToggle(permission: Permission): void {
    permission.isEnabled = !permission.isEnabled;
    this.hasUnsavedUserChanges = JSON.stringify(this.userPermissions) !== JSON.stringify(this.originalUserPermissions);
  }

  checkForUnsavedChanges(): void {
    if (!this.selectedRole) return;
    this.hasUnsavedChanges = JSON.stringify(this.selectedRole.permissions) !== JSON.stringify(this.originalPermissions);
  }

  // Save role-level permissions
  saveChanges(): void {
    if (!this.selectedRole) return;
    this.rolesService.updateRolePermissions(this.selectedRole.id, this.selectedRole.permissions)
      .subscribe({
        next: (updatedRole) => {
          const index = this.roles.findIndex(r => r.id === updatedRole.id);
          if (index !== -1) this.roles[index] = updatedRole;
          this.selectedRole = updatedRole;
          this.originalPermissions = clonePermissions(updatedRole.permissions);
          this.hasUnsavedChanges = false;
          this.groupPermissions();
          alert('Role permissions saved!');
        },
        error: (err) => {
          console.error('Error saving:', err);
          alert('Error saving. Please try again.');
        }
      });
  }

  // Save user-level permissions
  saveUserChanges(): void {
    if (!this.selectedUser) return;
    this.rolesService.updateUserPermissions(this.selectedUser.userId, this.userPermissions)
      .subscribe({
        next: (result) => {
          this.userPermissions = result.permissions;
          this.originalUserPermissions = clonePermissions(result.permissions);
          this.hasUnsavedUserChanges = false;
          this.groupUserPermissions();
          alert('User permissions saved!');
        },
        error: (err) => {
          console.error('Error saving user permissions:', err);
          alert('Error saving. Please try again.');
        }
      });
  }

  discardChanges(): void {
    if (!this.selectedRole) return;
    this.selectedRole.permissions = clonePermissions(this.originalPermissions);
    this.groupPermissions();
    this.hasUnsavedChanges = false;
  }

  discardUserChanges(): void {
    this.userPermissions = clonePermissions(this.originalUserPermissions);
    this.groupUserPermissions();
    this.hasUnsavedUserChanges = false;
  }

  openAddRoleDrawer(): void { this.isAddRoleDrawerOpen = true; }
  closeAddRoleDrawer(): void { this.isAddRoleDrawerOpen = false; }

  onRoleCreated(newRole: Role): void {
    this.rolesService.createRole(newRole).subscribe({
      next: (createdRole) => {
        this.roles.push(createdRole);
        this.selectRole(createdRole);
        this.closeAddRoleDrawer();
        alert(`Role "${createdRole.name}" created!`);
      },
      error: (err) => {
        console.error('Error creating role:', err);
        alert('Error: ' + (err.error?.error || 'Please try again.'));
      }
    });
  }
}