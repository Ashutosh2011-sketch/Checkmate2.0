import { Component, OnInit, OnDestroy } from '@angular/core';
// ✅ Correct path — go up 2 levels from pages/reports/ to app/, then into core/services/
import { ReportService, AdminSummaryResponse, DepartmentStatsResponse, OverdueItemResponse, UserPerformanceResponse, CompletionTrendResponse, BottleneckResponse } from '../../core/services/reports.service';
import { Subscription } from 'rxjs';

// ─── Existing Interfaces (unchanged) ─────────────────────────────────────────
interface SummaryCard {
  icon: string;
  iconClass: string;
  value: string;
  label: string;
  trend: string;
  trendClass: string;
}

interface CompletionTrendItem {
  month: string;
  completed: number;
  overdue: number;
  completedHeight: number;
  overdueHeight: number;
}

interface DepartmentStat {
  name: string;
  completed: number;
  total: number;
  percentage: number;
  pctClass: string;
  barClass: string;
}

interface OverdueItem {
  taskName: string;
  owner: string;
  ownerInitials: string;
  department: string;
  dueDate: string;
  overdueBy: string;
  status: string;
  statusClass: string;
}

interface PerformanceMetric {
  name: string;
  initials: string;
  role: string;
  completionRate: number;
  tasksCompleted: number;
}

interface WorkflowInsight {
  icon: string;
  title: string;
  description: string;
  value: string;
  cardClass: string;
  valueClass: string;
}

@Component({
  selector: 'app-reports',
  templateUrl: './reports.component.html',
  styleUrls: ['./reports.component.css'],
})
export class ReportsComponent implements OnInit, OnDestroy {

  // ── Filters ────────────────────────────────────────────────────────────────
  selectedDepartment = '';
  filterFrom = '';
  filterTo = '';

  departments: string[] = [];

  // ── Toast ──────────────────────────────────────────────────────────────────
  showToast = false;

  // ── Loading State ──────────────────────────────────────────────────────────
  loading = false;

  // ── Y-axis grid labels ─────────────────────────────────────────────────────
  yGridLines: number[] = [0, 25, 50, 75, 100];

  // ── Summary Cards ──────────────────────────────────────────────────────────
  summaryCards: SummaryCard[] = [];

  // ── Completion Trend ────────────────────────────────────────────────────────
  private readonly MAX_BAR_HEIGHT = 130;
  completionTrend: CompletionTrendItem[] = [];

  // ── Department Stats ────────────────────────────────────────────────────────
  allDepartmentStats: DepartmentStat[] = [];
  departmentStats: DepartmentStat[] = [];

  // ── Overdue Items ───────────────────────────────────────────────────────────
  allOverdueItems: OverdueItem[] = [];
  overdueItems: OverdueItem[] = [];

  // ── Performance Metrics ─────────────────────────────────────────────────────
  performanceMetrics: PerformanceMetric[] = [];

  // ── Workflow Insights ───────────────────────────────────────────────────────
  workflowInsights: WorkflowInsight[] = [];

  // ── Subscriptions ──────────────────────────────────────────────────────────
  private subscriptions: Subscription[] = [];

  constructor(private reportService: ReportService) {}

  // ── Lifecycle ───────────────────────────────────────────────────────────────
  ngOnInit(): void {
    const now = new Date();
    const sixMonthsAgo = new Date();
    sixMonthsAgo.setMonth(sixMonthsAgo.getMonth() - 6);
    
    this.filterFrom = this.toDateString(sixMonthsAgo);
    this.filterTo = this.toDateString(now);

    this.loadAllData();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(s => s.unsubscribe());
  }

  // ── Load All Real Data ─────────────────────────────────────────────────────
  private loadAllData(): void {
    this.loading = true;

    // 1️⃣ Summary Cards
    const summarySub = this.reportService.getAdminSummary().subscribe({
      next: (data) => this.buildSummaryCards(data),
      error: (err) => console.error('Summary error:', err)
    });
    this.subscriptions.push(summarySub);

    // 2️⃣ Department Stats
    const deptSub = this.reportService.getDepartmentStats().subscribe({
      next: (data) => this.buildDepartmentStats(data),
      error: (err) => console.error('Dept stats error:', err)
    });
    this.subscriptions.push(deptSub);

    // 3️⃣ Overdue Items
    const overdueSub = this.reportService.getOverdueItems().subscribe({
      next: (data) => this.buildOverdueItems(data),
      error: (err) => console.error('Overdue error:', err)
    });
    this.subscriptions.push(overdueSub);

    // 4️⃣ User Performance
    const perfSub = this.reportService.getUserPerformance().subscribe({
      next: (data) => this.buildPerformanceMetrics(data),
      error: (err) => console.error('Performance error:', err)
    });
    this.subscriptions.push(perfSub);

    // 5️⃣ Completion Trends
    const trendSub = this.reportService.getCompletionTrends(this.filterFrom, this.filterTo, 'MONTH').subscribe({
      next: (data) => {
        this.buildCompletionTrend(data);
        this.loading = false;
      },
      error: (err) => {
        console.error('Trends error:', err);
        this.loading = false;
      }
    });
    this.subscriptions.push(trendSub);

    // 6️⃣ Bottlenecks → Workflow Insights
    const bottleSub = this.reportService.getBottlenecks().subscribe({
      next: (data) => this.buildWorkflowInsights(data),
      error: (err) => console.error('Bottlenecks error:', err)
    });
    this.subscriptions.push(bottleSub);
  }

  // ── Build Summary Cards ────────────────────────────────────────────────────
  private buildSummaryCards(data: AdminSummaryResponse): void {
    const completionRate = data.totalTasks > 0 
      ? Math.round((data.completedTasks / data.totalTasks) * 100) 
      : 0;

    this.summaryCards = [
      {
        icon: '✅',
        iconClass: 'blue',
        value: this.formatNumber(data.completedTasks),
        label: 'Total Tasks Completed',
        trend: `↑ ${completionRate}% completion rate`,
        trendClass: 'trend-up'
      },
      {
        icon: '⚠️',
        iconClass: 'red',
        value: this.formatNumber(data.pendingTasks),
        label: 'Pending Tasks',
        trend: 'Needs attention',
        trendClass: 'trend-neutral'
      },
      {
        icon: '📋',
        iconClass: 'green',
        value: this.formatNumber(data.totalChecklists),
        label: 'Active Checklists',
        trend: `${data.completedChecklists} completed`,
        trendClass: 'trend-up'
      },
      {
        icon: '📈',
        iconClass: 'orange',
        value: `${completionRate}%`,
        label: 'Avg Completion Rate',
        trend: '→ Overall progress',
        trendClass: 'trend-neutral'
      }
    ];
  }

  // ── Build Department Stats ─────────────────────────────────────────────────
  private buildDepartmentStats(data: DepartmentStatsResponse[]): void {
    this.allDepartmentStats = data.map(d => ({
      name: d.departmentName,
      completed: d.completedTasks,
      total: d.totalTasks,
      percentage: Math.round(d.completionRate),
      pctClass: this.getPctClass(d.completionRate),
      barClass: this.getBarClass(d.completionRate)
    }));

    this.departmentStats = [...this.allDepartmentStats];
    this.departments = data.map(d => d.departmentName);
  }

  // ── Build Overdue Items ────────────────────────────────────────────────────
  private buildOverdueItems(data: OverdueItemResponse[]): void {
    this.allOverdueItems = data.map(item => ({
      taskName: item.taskName,
      owner: item.assignedTo,
      ownerInitials: this.getInitials(item.assignedTo),
      department: item.departmentName,
      dueDate: this.formatDate(item.dueDate),
      overdueBy: `${item.daysOverdue} days`,
      status: item.daysOverdue > 7 ? 'Overdue' : 'In Progress',
      statusClass: item.daysOverdue > 7 ? 'pill-overdue' : 'pill-in-progress'
    }));

    this.overdueItems = [...this.allOverdueItems];
  }

  // ── Build Performance Metrics ──────────────────────────────────────────────
  private buildPerformanceMetrics(data: UserPerformanceResponse[]): void {
    const sorted = [...data].sort((a, b) => b.completionRate - a.completionRate).slice(0, 5);
    
    this.performanceMetrics = sorted.map(user => ({
      name: user.userName,
      initials: this.getInitials(user.userName),
      role: user.departmentName,
      completionRate: Math.round(user.completionRate),
      tasksCompleted: user.completed
    }));
  }

  // ── Build Completion Trend ─────────────────────────────────────────────────
  private buildCompletionTrend(data: CompletionTrendResponse[]): void {
    const maxValue = 100;
    
    this.completionTrend = data.map(item => ({
      month: item.period,
      completed: item.completed,
      overdue: item.total - item.completed,
      completedHeight: Math.round((item.completed / maxValue) * this.MAX_BAR_HEIGHT),
      overdueHeight: Math.round(((item.total - item.completed) / maxValue) * this.MAX_BAR_HEIGHT)
    }));
  }

  // ── Build Workflow Insights ────────────────────────────────────────────────
  private buildWorkflowInsights(data: BottleneckResponse[]): void {
    const insights: WorkflowInsight[] = [];

    const critical = data.filter(b => b.daysStuck > 7);
    if (critical.length > 0) {
      insights.push({
        icon: '🔴',
        title: 'Approval Bottleneck',
        description: `${critical[0].checklistName} stuck at ${critical[0].currentLevel} for ${critical[0].daysStuck} days`,
        value: `+${critical[0].daysStuck} days`,
        cardClass: 'card-danger',
        valueClass: 'val-danger'
      });
    }

    const totalPending = data.reduce((sum, b) => sum + b.pendingTasksCount, 0);
    if (totalPending > 0) {
      insights.push({
        icon: '🟡',
        title: 'Dependency Delay',
        description: `${totalPending} tasks blocked due to unresolved dependencies`,
        value: `${totalPending} tasks`,
        cardClass: 'card-warn',
        valueClass: 'val-warn'
      });
    }

    const deptCounts: { [key: string]: number } = {};
    data.forEach(b => deptCounts[b.departmentName] = (deptCounts[b.departmentName] || 0) + 1);
    const topDept = Object.entries(deptCounts).sort((a, b) => b[1] - a[1])[0];
    if (topDept) {
      insights.push({
        icon: '🟣',
        title: 'High Workload',
        description: `${topDept[0]} has ${topDept[1]} bottlenecks — reassignment recommended`,
        value: `${topDept[1]} issues`,
        cardClass: 'card-info',
        valueClass: 'val-info'
      });
    }

    insights.push({
      icon: '🟢',
      title: 'Automation Savings',
      description: 'Workflow automation prevented manual escalations',
      value: 'Active',
      cardClass: 'card-success',
      valueClass: 'val-success'
    });

    this.workflowInsights = insights.slice(0, 6);
  }

  // ── Apply Filters ──────────────────────────────────────────────────────────
  applyFilters(): void {
    if (this.selectedDepartment) {
      this.departmentStats = this.allDepartmentStats.filter(d => d.name === this.selectedDepartment);
      this.overdueItems = this.allOverdueItems.filter(o => o.department === this.selectedDepartment);
    } else {
      this.departmentStats = [...this.allDepartmentStats];
      this.overdueItems = [...this.allOverdueItems];
    }
  }

  // ── Export Report ──────────────────────────────────────────────────────────
  exportReport(): void {
    this.showToast = true;
    setTimeout(() => (this.showToast = false), 3000);
    console.log('Exporting report with filters:', {
      department: this.selectedDepartment,
      from: this.filterFrom,
      to: this.filterTo
    });
  }

  // ── View All Insights ──────────────────────────────────────────────────────
  viewAllInsights(): void {
    console.log('Navigate to full workflow insights view');
  }

  // ── Utility Methods ────────────────────────────────────────────────────────
  private formatNumber(num: number): string {
    return num >= 1000 ? (num / 1000).toFixed(1) + 'k' : num.toString();
  }

  private formatDate(dateStr: string): string {
    const date = new Date(dateStr);
    return date.toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' });
  }

  private getInitials(name: string): string {
    if (!name) return 'NA';
    return name.split(' ').map(n => n[0]).join('').toUpperCase().slice(0, 2);
  }

  private getPctClass(rate: number): string {
    if (rate >= 80) return 'pct-high';
    if (rate >= 60) return 'pct-medium';
    return 'pct-low';
  }

  private getBarClass(rate: number): string {
    if (rate >= 80) return 'bar-green';
    if (rate >= 60) return 'bar-blue';
    if (rate >= 40) return 'bar-orange';
    return 'bar-red';
  }

  private toDateString(date: Date): string {
    return date.toISOString().split('T')[0];
  }
}