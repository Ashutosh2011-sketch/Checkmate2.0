import { Component, OnInit } from '@angular/core';
import { DashboardService } from '../../core/services/dashboard.service';

@Component({
  selector: 'app-user-dashboard',
  templateUrl: './user-dashboard.component.html',
  styleUrls: ['./user-dashboard.component.css']
})
export class UserDashboardComponent implements OnInit {

  dashboard: any;

  // 🔥 TEMP USER (NO LOGIN YET)
  userName: string = '';   // ✅ MUST be inside class

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
    this.userName = localStorage.getItem('userName') || 'User';
    this.loadDashboard();
  }

  loadDashboard(): void {

    this.dashboardService.getDashboardData(this.userName)
      .subscribe({
        next: (data: any) => {
          console.log('Dashboard:', data);
          this.dashboard = data;
        },
        error: (err) => {
          console.error('Error:', err);
        }
      });
  }
}

