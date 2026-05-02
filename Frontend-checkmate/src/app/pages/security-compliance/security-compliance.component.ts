import { Component, OnInit, OnDestroy } from '@angular/core';
import { interval, Subscription, switchMap, startWith } from 'rxjs';
import { AccessLogEntry, SecurityLogsService } from '../../core/services/security-logs.service';

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

type SecuritySettingKey = 'twoFactorAuth' | 'loginAlerts' | 'activityMonitoring' | 'dataEncryption';

@Component({
  selector: 'app-security-compliance',
  templateUrl: './security-compliance.component.html',
  styleUrls: ['./security-compliance.component.css']
})
export class SecurityComplianceComponent implements OnInit, OnDestroy {

  accessLogs: AccessLogEntry[] = [];
  auditLogs: AuditLogEntry[] = [];
  logsLoadError = '';
  isLoadingLogs = true;

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

  constructor(private securityLogsService: SecurityLogsService) {}

  ngOnInit(): void {
    this.logSubscription = interval(8000)
      .pipe(
        startWith(0),
        switchMap(() => this.securityLogsService.getAccessLogs(200))
      )
      .subscribe({
        next: (rows) => {
          this.accessLogs = rows;
          this.auditLogs = this.mapToAuditTrail(rows);
          this.logsLoadError = '';
          this.isLoadingLogs = false;
        },
        error: () => {
          this.logsLoadError = 'Could not load access logs.';
          this.isLoadingLogs = false;
        }
      });
  }

  ngOnDestroy(): void {
    this.logSubscription?.unsubscribe();
  }

  private mapToAuditTrail(rows: AccessLogEntry[]): AuditLogEntry[] {
    return rows.slice(0, 15).map((log) => ({
      user: log.username,
      action: this.formatAuditAction(log),
      time: this.formatRelative(log.occurredAt),
      ip: log.ipAddress,
      icon: this.iconForActivity(log.activityType)
    }));
  }

  private formatAuditAction(log: AccessLogEntry): string {
    const detail = log.resourceDetail && log.resourceDetail !== '—' ? log.resourceDetail : '';
    switch (log.activityType) {
      case 'Login':
        return 'Logged in';
      case 'Logout':
        return 'Logged out';
      case 'Checklist created':
        return detail ? `Created checklist: ${detail}` : 'Created checklist';
      case 'Task Completed':
        return detail ? `Completed task: ${detail}` : 'Completed task';
      case 'Task Updated':
        return detail ? `Updated task: ${detail}` : 'Updated task progress';
      default:
        return log.activityType;
    }
  }

  private iconForActivity(activityType: string): string {
    switch (activityType) {
      case 'Login':
        return 'login';
      case 'Logout':
        return 'logout';
      case 'Checklist created':
        return 'playlist_add_check';
      case 'Task Completed':
        return 'check_circle';
      case 'Task Updated':
        return 'edit';
      default:
        return 'history';
    }
  }

  private formatRelative(iso: string): string {
    if (!iso) {
      return '—';
    }
    const d = new Date(iso);
    if (Number.isNaN(d.getTime())) {
      return iso;
    }
    const diffMs = Date.now() - d.getTime();
    const sec = Math.floor(diffMs / 1000);
    if (sec < 45) {
      return 'Just now';
    }
    const mins = Math.floor(sec / 60);
    if (mins < 60) {
      return `${mins} min ago`;
    }
    const hrs = Math.floor(mins / 60);
    if (hrs < 24) {
      return `${hrs} hr ago`;
    }
    const days = Math.floor(hrs / 24);
    return `${days} day${days === 1 ? '' : 's'} ago`;
  }

  toggleSetting(setting: SecuritySettingKey): void {
    this.securitySettings[setting] = !this.securitySettings[setting];
  }

  formatTimestamp(iso: string): string {
    if (!iso) {
      return '—';
    }
    const d = new Date(iso);
    if (Number.isNaN(d.getTime())) {
      return iso;
    }
    return d.toLocaleString(undefined, {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    });
  }

  getActivityBadgeClass(activityType: string): string {
    switch (activityType) {
      case 'Login':
        return 'login';
      case 'Logout':
        return 'logout';
      case 'Checklist created':
        return 'checklist-created';
      case 'Task Completed':
        return 'task-completed';
      case 'Task Updated':
        return 'task-updated';
      default:
        return '';
    }
  }
}
