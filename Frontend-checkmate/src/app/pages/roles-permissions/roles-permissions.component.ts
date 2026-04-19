import { Component, OnInit } from '@angular/core';
import { RolesPermissionsService } from '../../core/services/roles-permissions.service';
import { Role, Permission, clonePermissions } from '../../core/models/roles-permissions.model';

@Component({
  selector: 'app-roles-permissions',
  templateUrl: './roles-permissions.component.html',
  styleUrls: ['./roles-permissions.component.css']
})
export class RolesPermissionsComponent implements OnInit {
  roles: Role[] = [];
  selectedRole: Role | null = null;

  // To track changes for the "Save" button
  originalPermissions: Permission[] = [];
  hasUnsavedChanges = false;

  // Controls the visibility of the "Add Role" drawer
  isAddRoleDrawerOpen = false;

  // Grouped permissions for display
  groupedPermissions: { [category: string]: Permission[] } = {};

  // Loading state
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
        // Select the first role by default
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
    if (this.hasUnsavedChanges) {
      const confirmDiscard = confirm('You have unsaved changes. Do you want to discard them and switch roles?');
      if (!confirmDiscard) return;
    }
    this.selectedRole = role;
    // Create a deep copy to compare against for changes
    this.originalPermissions = clonePermissions(role.permissions);
    this.hasUnsavedChanges = false;
    this.groupPermissions();
  }

  groupPermissions(): void {
    if (!this.selectedRole) return;
    this.groupedPermissions = this.selectedRole.permissions.reduce((groups: { [key: string]: Permission[] }, permission: Permission) => {
      const category = permission.category;
      if (!groups[category]) {
        groups[category] = [];
      }
      groups[category].push(permission);
      return groups;
    }, {} as { [category: string]: Permission[] });
  }

  get permissionCategories(): string[] {
    return Object.keys(this.groupedPermissions);
  }

  onPermissionToggle(permission: Permission): void {
    permission.isEnabled = !permission.isEnabled;
    this.checkForUnsavedChanges();
  }

  checkForUnsavedChanges(): void {
    if (!this.selectedRole) return;
    this.hasUnsavedChanges = JSON.stringify(this.selectedRole.permissions) !== JSON.stringify(this.originalPermissions);
  }

  saveChanges(): void {
    if (!this.selectedRole) return;

    this.rolesService.updateRolePermissions(this.selectedRole.id, this.selectedRole.permissions)
      .subscribe({
        next: (updatedRole) => {
          // Update the role in the list
          const index = this.roles.findIndex(r => r.id === updatedRole.id);
          if (index !== -1) {
            this.roles[index] = updatedRole;
          }
          this.selectedRole = updatedRole;
          this.originalPermissions = clonePermissions(updatedRole.permissions);
          this.hasUnsavedChanges = false;
          this.groupPermissions();
          alert('Changes saved successfully!');
        },
        error: (err) => {
          console.error('Error saving changes:', err);
          alert('Error saving changes. Please try again.');
        }
      });
  }

  discardChanges(): void {
    if (!this.selectedRole) return;
    // Revert permissions to the original state
    this.selectedRole.permissions = clonePermissions(this.originalPermissions);
    this.groupPermissions();
    this.hasUnsavedChanges = false;
  }

  openAddRoleDrawer(): void {
    this.isAddRoleDrawerOpen = true;
  }

  closeAddRoleDrawer(): void {
    this.isAddRoleDrawerOpen = false;
  }

  onRoleCreated(newRole: Role): void {
    this.rolesService.createRole(newRole).subscribe({
      next: (createdRole) => {
        this.roles.push(createdRole);
        this.selectRole(createdRole);
        this.closeAddRoleDrawer();
        alert(`Role "${createdRole.name}" created successfully!`);
      },
      error: (err) => {
        console.error('Error creating role:', err);
        alert('Error creating role: ' + (err.error?.error || 'Please try again.'));
      }
    });
  }
}