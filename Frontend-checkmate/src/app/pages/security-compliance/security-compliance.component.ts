import { Component, OnInit, OnDestroy } from '@angular/core';
import { interval, Subscription } from 'rxjs';

interface AccessLog {
  user: string;
  activityType: 'Login' | 'Logout' | 'Task Completed' | 'Task Updated';
  taskName: string;
  timestamp: Date;
  ipAddress: string;
}

interface AuditLogEntry {
  user: string;
  action: string;
  time: string;
  ip: string;
  icon: string;
}

interface ComplianceStatus {
  name: string;
  status: boolean;
  description: string;
}

@Component({
  selector: 'app-security-compliance',
  templateUrl: './security-compliance.component.html',
  styleUrls: ['./security-compliance.component.css']
})
export class SecurityComplianceComponent implements OnInit, OnDestroy {
  
  // Mock data arrays
  accessLogs: AccessLog[] = [
    { user: 'John Smith', activityType: 'Login', taskName: '-', timestamp: new Date(), ipAddress: '192.168.1.101' },
    { user: 'Sarah Johnson', activityType: 'Task Completed', taskName: 'Q4 Compliance Audit', timestamp: new Date(Date.now() - 3600000), ipAddress: '192.168.1.102' },
    { user: 'Mike Wilson', activityType: 'Task Updated', taskName: 'IT Infrastructure Review', timestamp: new Date(Date.now() - 7200000), ipAddress: '192.168.1.103' },
    { user: 'Emily Brown', activityType: 'Logout', taskName: '-', timestamp: new Date(Date.now() - 10800000), ipAddress: '192.168.1.104' },
    { user: 'David Lee', activityType: 'Task Completed', taskName: 'Security Assessment', timestamp: new Date(Date.now() - 14400000), ipAddress: '192.168.1.105' }
  ];

  auditLogs: AuditLogEntry[] = [
    { user: 'John Smith', action: 'Logged into system', time: '2 mins ago', ip: '192.168.1.101', icon: 'login' },
    { user: 'Sarah Johnson', action: 'Completed checklist: Q4 Audit', time: '15 mins ago', ip: '192.168.1.102', icon: 'check_circle' },
    { user: 'Admin', action: 'Updated user permissions', time: '1 hour ago', ip: '192.168.1.1', icon: 'admin_panel_settings' },
    { user: 'Mike Wilson', action: 'Modified task priority', time: '2 hours ago', ip: '192.168.1.103', icon: 'edit' },
    { user: 'Emily Brown', action: 'Exported report', time: '3 hours ago', ip: '192.168.1.104', icon: 'download' },
    { user: 'David Lee', action: 'Created new template', time: '5 hours ago', ip: '192.168.1.105', icon: 'add_circle' }
  ];

  complianceItems: ComplianceStatus[] = [
    { name: 'GDPR Compliance', status: false, description: 'Data protection regulation' },
    { name: 'ISO 27001 Certified', status: true, description: 'Information security management' },
    { name: 'Data Encryption', status: true, description: 'AES-256 encryption at rest' },
    { name: 'Security Audits', status: true, description: 'Monthly security reviews' }
  ];

  securitySettings = {
    twoFactorAuth: false,
    loginAlerts: true,
    activityMonitoring: true,
    dataEncryption: true
  };

  private logSubscription?: Subscription;
  private users = ['John Smith', 'Sarah Johnson', 'Mike Wilson', 'Emily Brown', 'David Lee', 'Lisa Anderson'];
  private activities: Array<'Login' | 'Logout' | 'Task Completed' | 'Task Updated'> = ['Login', 'Logout', 'Task Completed', 'Task Updated'];
  private tasks = ['Q4 Compliance Audit', 'IT Infrastructure Review', 'Security Assessment', 'Monthly Report', 'User Onboarding'];

  ngOnInit(): void {
    // Simulate real-time log updates
    this.logSubscription = interval(5000).subscribe(() => {
      this.addNewLog();
    });
  }

  ngOnDestroy(): void {
    if (this.logSubscription) {
      this.logSubscription.unsubscribe();
    }
  }

  private addNewLog(): void {
    const randomUser = this.users[Math.floor(Math.random() * this.users.length)];
    const randomActivity = this.activities[Math.floor(Math.random() * this.activities.length)];
    const randomTask = this.tasks[Math.floor(Math.random() * this.tasks.length)];
    const randomIp = `192.168.1.${Math.floor(Math.random() * 255)}`;

    const newLog: AccessLog = {
      user: randomUser,
      activityType: randomActivity,
      taskName: randomActivity === 'Login' || randomActivity === 'Logout' ? '-' : randomTask,
      timestamp: new Date(),
      ipAddress: randomIp
    };

    this.accessLogs.unshift(newLog);
    
    // Keep only last 20 logs
    if (this.accessLogs.length > 20) {
      this.accessLogs.pop();
    }
  }

  toggleSetting(setting: keyof typeof this.securitySettings): void {
    this.securitySettings[setting] = !this.securitySettings[setting];
  }

formatTimestamp(date: Date): string {
    return date.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', second: '2-digit' });
  }

  getActivityBadgeClass(activityType: string): string {
    switch (activityType) {
      case 'Login':
        return 'login';
      case 'Logout':
        return 'logout';
      case 'Task Completed':
        return 'task-completed';
      case 'Task Updated':
        return 'task-updated';
      default:
        return '';
    }
  }
}
