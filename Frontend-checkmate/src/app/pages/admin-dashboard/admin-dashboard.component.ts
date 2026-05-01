import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subject, interval, startWith, switchMap, takeUntil } from 'rxjs';
import { AdminDashboardSummary } from 'src/app/core/models/dashboard.model';
import { DashboardService } from 'src/app/core/services/dashboard.service';

interface DashboardStat {
  title: string;
  value: number;
  helperText: string;
  iconClass: string;
}

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit, OnDestroy {
  private readonly destroy$ = new Subject<void>();
  private readonly refreshMs = 10000;

  isLoading = true;
  loadError = '';
  lastUpdated: Date | null = null;
  summary: AdminDashboardSummary = {
    totalChecklists: 0,
    totalTasks: 0,
    completedTasks: 0,
    pendingTasks: 0,
    completedChecklists: 0
  };

  constructor(
    private router: Router,
    private dashboardService: DashboardService
  ) { }

  ngOnInit(): void {
    interval(this.refreshMs)
      .pipe(
        startWith(0),
        switchMap(() => this.dashboardService.getAdminSummary()),
        takeUntil(this.destroy$)
      )
      .subscribe({
        next: (summary) => {
          this.summary = summary;
          this.lastUpdated = new Date();
          this.isLoading = false;
          this.loadError = '';
        },
        error: () => {
          this.isLoading = false;
          this.loadError = 'Unable to fetch live dashboard data.';
        }
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  get completionRate(): number {
    if (this.summary.totalTasks === 0) {
      return 0;
    }
    return Math.round((this.summary.completedTasks / this.summary.totalTasks) * 100);
  }

  get stats(): DashboardStat[] {
    return [
      {
        title: 'Checklists Created',
        value: this.summary.totalChecklists,
        helperText: 'Total checklists in database',
        iconClass: 'fact_check'
      },
      {
        title: 'Pending Tasks',
        value: this.summary.pendingTasks,
        helperText: 'Tasks still pending completion',
        iconClass: 'pending_actions'
      },
      {
        title: 'Completed Tasks',
        value: this.summary.completedTasks,
        helperText: 'Tasks completed in total',
        iconClass: 'task_alt'
      },
      {
        title: 'Completed Checklists',
        value: this.summary.completedChecklists,
        helperText: `${this.completionRate}% task completion rate`,
        iconClass: 'check_circle'
      }
    ];
  }

  onQuickAction(route: string): void {
    this.router.navigate([route]);
  }
}