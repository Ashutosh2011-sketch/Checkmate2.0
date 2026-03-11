import { Component, OnInit } from '@angular/core';

import { Role, Permission, MOCK_ROLES, clonePermissions } from '../../core/models/roles-permissions.model'

@Component({
  selector: 'app-roles-permissions',
  templateUrl: './roles-permissions.component.html',
  styleUrls: ['./roles-permissions.component.css']
})
export class RolesPermissionsComponent implements OnInit {
  roles: Role[] = MOCK_ROLES;
  selectedRole: Role | null = null;
  
  // To track changes for the "Save" button
  originalPermissions: Permission[] = [];
  hasUnsavedChanges = false;

  // Controls the visibility of the "Add Role" drawer
  isAddRoleDrawerOpen = false;

  // Grouped permissions for display
  groupedPermissions: { [category: string]: Permission[] } = {};

  ngOnInit(): void {
    // Select the first role by default
    if (this.roles.length > 0) {
      this.selectRole(this.roles[0]);
    }
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
    // In a real app, you would make an API call here.
    console.log('Saving changes for role:', this.selectedRole.name, this.selectedRole.permissions);
    
    // Update the original permissions to the new state
    this.originalPermissions = clonePermissions(this.selectedRole.permissions);
    this.hasUnsavedChanges = false;
    alert('Changes saved successfully!');
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
    this.roles.push(newRole);
    this.selectRole(newRole);
    this.closeAddRoleDrawer();
    alert(`Role "${newRole.name}" created successfully!`);
  }
}