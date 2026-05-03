import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

interface SidebarItem {
  label: string;
  icon: string;
  route: string;
  permission?: string;
  adminOnly?: boolean;
  userOnly?: boolean;
}

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent {
  menuItems: SidebarItem[] = [
    { label: 'My Dashboard', icon: 'dashboard', route: '/dashboard', userOnly: true },
    { label: 'Checklists', icon: 'checklist', route: '/checklists', userOnly: true },
    { label: 'Checklist Tracker', icon: 'assignment', route: '/checklist-tracker', userOnly: true },

    { label: 'Checklist Builder', icon: 'build', route: '/checklist-builder', permission: 'Create Checklists', userOnly: true },
    { label: 'User Management', icon: 'group', route: '/user-management', permission: 'Manage Users', userOnly: true },
    { label: 'Reports', icon: 'bar_chart', route: '/reports', permission: 'View All Reports', userOnly: true },

    { label: 'Profile', icon: 'person', route: '/profile', userOnly: true },

    { label: 'Dashboard', icon: 'home', route: '/admin-dashboard', adminOnly: true },
    { label: 'All Checklists', icon: 'assignment', route: '/admin/checklists', adminOnly: true },
    { label: 'Checklist Builder', icon: 'checklist', route: '/admin/checklist-builder', adminOnly: true },
    { label: 'User Management', icon: 'group', route: '/admin/users', adminOnly: true },
    { label: 'Roles & Permissions', icon: 'verified_user', route: '/admin/roles-permissions', adminOnly: true },
    { label: 'Reports', icon: 'analytics', route: '/admin/reports', adminOnly: true },
    { label: 'Security & Compliance', icon: 'security', route: '/admin/security', adminOnly: true },
    { label: 'Notifications', icon: 'notifications', route: '/admin/notifications', adminOnly: true },
    { label: 'Profile', icon: 'person', route: '/admin/profile', adminOnly: true }
  ];

  constructor(
    public router: Router,
    public authService: AuthService
  ) {}

  get visibleMenuItems(): SidebarItem[] {
    return this.menuItems.filter(item => {
      if (item.adminOnly && !this.authService.isAdmin()) {
        return false;
      }

      if (item.userOnly && this.authService.isAdmin()) {
        return false;
      }

      if (item.permission && !this.authService.hasPermission(item.permission)) {
        return false;
      }

      return true;
    });
  }

  isActive(route: string): boolean {
    return this.router.url.includes(route);
  }

  isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  hasPermission(permission: string): boolean {
    return this.authService.hasPermission(permission);
  }

  onLogout(): void {
    this.authService.logout();
  }
}
