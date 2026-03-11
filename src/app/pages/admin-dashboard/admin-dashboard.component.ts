import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

interface DashboardStat {
  title: string;
  value: string;
  subtitle?: string;
  subtitleColor?: 'red' | 'green' | 'default';
  buttonText?: string;
  iconClass?: string;
  iconColor?: string;
}

interface ActivityItem {
  description: string;
}

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {

  // Top Cards Data
  stats: DashboardStat[] = [
    { title: 'Total Active Checklists', value: '450', subtitle: '• 120 (27%)', subtitleColor: 'red', iconClass: 'show_chart', iconColor: 'blue' },
    { title: 'Overdue Tasks', value: '120', buttonText: 'View Checklists', iconClass: 'warning', iconColor: 'red' },
    { title: 'Completion Rate', value: '73%', buttonText: 'View Reports', iconClass: 'check_circle', iconColor: 'green' },
    { title: 'Total Users', value: '1,000', buttonText: 'Manage Users' },
    { title: 'Active Workflows', value: '85', buttonText: 'View Workflows' }
  ];

  // Activity Feed Data
  activities: ActivityItem[] = [
    { description: "John Doe completed 'Q3 Compliance Audit'" },
    { description: "Workflow 'New Employee Onboarding' started" },
    { description: "Task 'Install MS Office' assigned" },
    { description: "Sarah Chen updated 'Security Policies'" }
  ];

  constructor(private router: Router) { }

  ngOnInit(): void {
    // Line 1
  console.log("The Admin Dashboard is ready!");

  // Line 2
  this.stats[0].value = "990";
  }

  // 1. Top Summary Cards Click Logic (Mapped to exact routes)
  onStatAction(actionText: string): void {
    if (actionText === 'View Checklists') {
      this.router.navigate(['/admin/checklists']); // Updated from all-checklists
    } else if (actionText === 'View Reports') {
      this.router.navigate(['/admin/reports']); 
    } else if (actionText === 'Manage Users') {
      this.router.navigate(['/admin/users']);
    } else if (actionText === 'View Workflows') {
      this.router.navigate(['/admin/workflow']); // Updated from workflow-designer
    }
  }

  // 2. Critical Alerts Action Buttons Logic (Mapped to exact routes)
  onQuickAction(actionType: string): void {
    if (actionType === 'Create Checklist') {
      this.router.navigate(['/admin/checklist-builder']); // Updated mapping
    } else if (actionType === 'Add User') {
      this.router.navigate(['/admin/users']);
    } else if (actionType === 'Create Template') {
      this.router.navigate(['/admin/templates']);
    } else if (actionType === 'Configure Workflow') {
      this.router.navigate(['/admin/workflow']); // Updated from workflow-designer
    }
  }

}