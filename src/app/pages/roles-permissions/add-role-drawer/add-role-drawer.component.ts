import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Role, Permission, MOCK_ROLES,MOCK_ALL_PERMISSIONS, clonePermissions } from '../../../core/models/roles-permissions.model';

import { v4 as uuidv4 } from 'uuid'; // You might need to install 'uuid' and '@types/uuid'

@Component({
  selector: 'app-add-role-drawer',
  templateUrl: './add-role-drawer.component.html',
  styleUrls: ['./add-role-drawer.component.css']
})
export class AddRoleDrawerComponent implements OnInit {
  @Input() isOpen = false;
  @Input() existingRoles: Role[] = [];
  @Output() close = new EventEmitter<void>();
  @Output() roleCreated = new EventEmitter<Role>();

  newRoleName = '';
  newRoleDescription = '';
  selectedCloneRole: Role | null = null;
  
  // Permissions for the new role being created
  newRolePermissions: Permission[] = [];
  groupedPermissions: { [category: string]: Permission[] } = {};

  ngOnInit(): void {
    this.resetForm();
  }

  resetForm(): void {
    this.newRoleName = '';
    this.newRoleDescription = '';
    this.selectedCloneRole = null;
    // Initialize with all permissions disabled
    this.newRolePermissions = clonePermissions(MOCK_ALL_PERMISSIONS);
    this.groupPermissions();
  }

  groupPermissions(): void {
    this.groupedPermissions = this.newRolePermissions.reduce((groups: { [key: string]: Permission[] }, permission: Permission) => {
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

  onCloneRoleSelect(roleId: string): void {
    const role = this.existingRoles.find(r => r.id === roleId);
    if (role) {
      this.selectedCloneRole = role;
      // Copy permissions from the selected role
      this.newRolePermissions = clonePermissions(role.permissions);
      this.groupPermissions();
    } else {
      this.selectedCloneRole = null;
      this.resetForm(); // Or just reset permissions
    }
  }

  onCreateRole(): void {
    if (!this.newRoleName.trim()) {
      alert('Role Name is required.');
      return;
    }

    const newRole: Role = {
      id: uuidv4(), // Generate a new ID
      name: this.newRoleName,
      description: this.newRoleDescription,
      permissions: this.newRolePermissions
    };

    this.roleCreated.emit(newRole);
    this.resetForm();
  }
}