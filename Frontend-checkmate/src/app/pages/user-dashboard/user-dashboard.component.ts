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
  userName: string = 'Ravi';   // ✅ MUST be inside class

  constructor(private dashboardService: DashboardService) {}

  ngOnInit(): void {
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

    /*
    ================= FUTURE =================
    After login system:

    const user = this.authService.getUser();

    this.dashboardService.getDashboardData(user.name)
      .subscribe(data => this.dashboard = data);
    =========================================
    */
  }
}
// import { Component, OnInit } from '@angular/core';
// import { DashboardService } from '../../core/services/dashboard.service';
// import { Dashboard } from '../../core/models/dashboard.model';

// @Component({
//   selector: 'app-user-dashboard',
//   templateUrl: './user-dashboard.component.html',
//   styleUrls: ['./user-dashboard.component.css']
// })
// export class UserDashboardComponent implements OnInit {

//   dashboard!: Dashboard;

//   constructor(private dashboardService: DashboardService) {}

//   ngOnInit(): void {
//     this.dashboardService.getDashboardData().subscribe((data: Dashboard) => {
//       this.dashboard = data;
//     });
//   }
// }
