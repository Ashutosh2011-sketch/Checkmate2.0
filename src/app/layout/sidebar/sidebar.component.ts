import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent {

  constructor(
    public router: Router,
    public authService: AuthService
  ) {}

  isActive(route: string): boolean {
    return this.router.url.includes(route);
  }

 isAdmin(): boolean {
    return this.router.url.includes('admin'); 
  }

  // Logout handler
  onLogout(): void {
    this.authService.logout();
  }
}
