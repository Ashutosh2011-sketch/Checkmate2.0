import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Role, Permission, clonePermissions } from '../../../core/models/roles-permissions.model';
import { RolesPermissionsService } from '../../../core/services/roles-permissions.service';

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

  // All available permissions loaded from API
  allPermissions: Permission[] = [];

  constructor(private rolesService: RolesPermissionsService) {}

  ngOnInit(): void {
    this.loadPermissions();
  }

  loadPermissions(): void {
    this.rolesService.getAllPermissions().subscribe({
      next: (permissions) => {
        this.allPermissions = permissions;
        this.resetForm();
      },
      error: (err) => {
        console.error('Error loading permissions:', err);
        this.resetForm();
      }
    });
  }

  resetForm(): void {
    this.newRoleName = '';
    this.newRoleDescription = '';
    this.selectedCloneRole = null;
    // Initialize with all permissions disabled
    this.newRolePermissions = this.allPermissions.map(p => ({ ...p, isEnabled: false }));
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
    const id = Number(roleId);
    const role = this.existingRoles.find(r => r.id === id);
    if (role) {
      this.selectedCloneRole = role;
      // Copy permissions from the selected role
      this.newRolePermissions = clonePermissions(role.permissions);
      this.groupPermissions();
    } else {
      this.selectedCloneRole = null;
      this.newRolePermissions = this.allPermissions.map(p => ({ ...p, isEnabled: false }));
      this.groupPermissions();
    }
  }

  onCreateRole(): void {
    if (!this.newRoleName.trim()) {
      alert('Role Name is required.');
      return;
    }

    const newRole: Role = {
      id: 0, // Backend generates the ID
      name: this.newRoleName,
      description: this.newRoleDescription,
      permissions: this.newRolePermissions
    };

    this.roleCreated.emit(newRole);
    this.resetForm();
  }
}