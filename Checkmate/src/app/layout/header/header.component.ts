import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {
  constructor(public router: Router, private authService: AuthService) {}

  logout(): void {
    this.authService.logout();
  }

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  getPageTitle(): string {
    const url = this.router.url;
    
    // Admin routes
    if (url.includes('/admin-dashboard')) return 'Admin Dashboard';
    if (url.includes('/admin/checklists')) return 'All Checklists';
    if (url.includes('/admin/tasks')) return 'My Tasks';
    if (url.includes('/admin/users')) return 'User Management';
    if (url.includes('/admin/roles')) return 'Roles & Permissions';
    if (url.includes('/admin/departments')) return 'Departments';
    if (url.includes('/admin/templates')) return 'Templates';
    if (url.includes('/admin/workflow')) return 'Workflow Designer';
    if (url.includes('/admin/reports')) return 'Reports';
    if (url.includes('/admin/security')) return 'Security & Compliance';
    if (url.includes('/admin/notifications')) return 'Notifications';
    if (url.includes('/admin/profile')) return 'Admin Profile';
    if (url === '/admin') return 'Admin Dashboard';
    
    // User routes
    if (url.includes('/dashboard')) return 'My Dashboard';
    if (url.includes('/checklists')) return 'Checklists';
    if (url.includes('/checklist-detail')) return 'Checklist Detail';
    if (url.includes('/checklist-builder')) return 'Checklist Builder';
    if (url.includes('/checklist-tracker')) return 'Checklist Tracker';
    if (url.includes('/reports')) return 'Reports';
    if (url.includes('/profile')) return 'Profile';
    
    return 'Dashboard';
  }
}
