import { Component, OnInit } from '@angular/core';
import { DashboardService } from '../../core/services/dashboard.service';
import { Dashboard, ChecklistInfo, TaskInfo } from '../../core/models/dashboard.model';

import {OnDestroy } from '@angular/core';
import { NotificationService } from '../../core/services/notification.service'; // Service ka sahi path dena
import { interval, Subscription } from 'rxjs';

@Component({
  selector: 'app-user-dashboard',
  templateUrl: './user-dashboard.component.html',
  styleUrls: ['./user-dashboard.component.css']
})
// export class UserDashboardComponent implements OnInit {

//   dashboard: Dashboard | null = null;
//   userName: string = '';

//   constructor(private dashboardService: DashboardService) {}

//   ngOnInit(): void {
//     this.userName = localStorage.getItem('userName') || 'User';
//     this.loadDashboard();
//   }

//   loadDashboard(): void {
//     this.dashboardService.getDashboardData(this.userName)
//       .subscribe({
//         next: (data: Dashboard) => {
//           console.log('Dashboard:', data);
//           this.dashboard = data;
//         },
//         error: (err) => {
//           console.error('Error:', err);
//         }
//       });
//   }

//   // Dynamic conic-gradient for progress circle
//   getProgressGradient(): string {
//     const progress = this.dashboard?.progress || 0;
//     return `conic-gradient(#2a5298 0% ${progress}%, #e6e6e6 ${progress}% 100%)`;
//   }

//   // Badge class based on priority
//   getPriorityClass(priority: string): string {
//     if (!priority) return 'badge-default';
//     switch (priority.toLowerCase()) {
//       case 'high': return 'badge-high';
//       case 'medium': return 'badge-medium';
//       case 'low': return 'badge-low';
//       default: return 'badge-default';
//     }
//   }

//   // Status badge class
//   getStatusClass(status: string): string {
//     if (!status) return 'status-pending';
//     switch (status.toLowerCase()) {
//       case 'completed': return 'status-completed';
//       case 'in progress': return 'status-inprogress';
//       case 'pending': return 'status-pending';
//       default: return 'status-pending';
//     }
//   }
// }

export class UserDashboardComponent implements OnInit, OnDestroy {

  dashboard: Dashboard | null = null;
  userName: string = '';
  notifications: any[] = []; // Nayi notifications ki list
  private pollingSub?: Subscription; // Polling ko control karne ke liye

  constructor(
    private dashboardService: DashboardService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.userName = localStorage.getItem('userName') || 'User';
    
    // 1. Pehli baar data load karo
    this.loadDashboard();
    this.loadNotifications();

    // 2. 🕒 POLLING: Har 30 second (30000ms) mein data refresh hoga
    this.pollingSub = interval(30000).subscribe(() => {
      this.loadDashboard();
      this.loadNotifications();
      console.log('Polling: Data refreshed from backend.');
    });
  }

  loadDashboard(): void {
    this.dashboardService.getDashboardData(this.userName)
      .subscribe({
        next: (data: Dashboard) => {
          this.dashboard = data;
        },
        error: (err) => console.error('Dashboard Error:', err)
      });
  }

  loadNotifications(): void {
    this.notificationService.getNotifications().subscribe({
      next: (data) => {
        this.notifications = data;
        console.log('Notifications loaded:', data);
      },
      error: (err) => console.error('Notification Error:', err)
    });
  }

  // Conic-gradient for progress circle
  getProgressGradient(): string {
    const progress = this.dashboard?.progress || 0;
    return `conic-gradient(#2a5298 0% ${progress}%, #e6e6e6 ${progress}% 100%)`;
  }

  // Priority badge logic
  getPriorityClass(priority: string): string {
    if (!priority) return 'badge-default';
    switch (priority.toLowerCase()) {
      case 'high': return 'badge-high';
      case 'medium': return 'badge-medium';
      case 'low': return 'badge-low';
      default: return 'badge-default';
    }
  }

  // Status badge logic
  getStatusClass(status: string): string {
    if (!status) return 'status-pending';
    switch (status.toLowerCase()) {
      case 'completed': return 'status-completed';
      case 'in progress': return 'status-inprogress';
      case 'pending': return 'status-pending';
      default: return 'status-pending';
    }
  }

  // user-dashboard.component.ts mein:

markAsRead(id: number): void {
  this.notificationService.markAsRead(id).subscribe({
    next: () => {
      // Local array mein bhi update kar do taaki turant UI badal jaye
      const note = this.notifications.find(n => n.id === id);
      if (note) note.isRead = true;
      
      // Optional: Agar read hone par hatana hai toh:
      this.notifications = this.notifications.filter(n => n.id !== id);
      
      console.log(`Notification ${id} marked as read`);
    },
    error: (err) => console.error("Error marking as read", err)
  });
}



  ngOnDestroy(): void {
    // 🛑 Sabse Zaruri: Jab user page se bahar jaye toh polling band kar do
    if (this.pollingSub) {
      this.pollingSub.unsubscribe();
      console.log('Polling stopped.');
    }
  }
}
