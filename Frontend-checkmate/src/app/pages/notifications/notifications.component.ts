
import { Component, OnInit, OnDestroy } from '@angular/core';
import { NotificationService } from '../../core/services/notification.service';
import { interval, Subscription } from 'rxjs';

@Component({
  selector: 'app-notifications',
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.css']
})
export class AdminNotificationsComponent implements OnInit, OnDestroy {

  notificationsList: any[] = [];
  loading: boolean = true;
  private pollingSub?: Subscription;

  constructor(private notificationService: NotificationService) {}

  ngOnInit(): void {
    this.loadNotifications();
    // Poll every 30 seconds
    this.pollingSub = interval(30000).subscribe(() => this.loadNotifications());
  }

  loadNotifications(): void {
    this.notificationService.getAdminNotifications().subscribe({
      next: (data) => {
        this.notificationsList = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading admin notifications:', err);
        this.loading = false;
      }
    });
  }

  markAllAsRead(): void {
    this.notificationService.markAllAdminRead().subscribe({
      next: () => {
        this.notificationsList = [];
      },
      error: (err) => console.error('Error marking all read:', err)
    });
  }

  markOneAsRead(id: number): void {
    this.notificationService.markAsRead(id).subscribe({
      next: () => {
        this.notificationsList = this.notificationsList.filter(n => n.id !== id);
      },
      error: (err) => console.error('Error marking read:', err)
    });
  }

  getCleanMessage(message: string): string {
  return message.includes('| checklistId:')
    ? message.split('| checklistId:')[0].trim()
    : message;
}

getChecklistId(message: string): string | null {
  if (message.includes('| checklistId:')) {
    return message.split('| checklistId:')[1].trim();
  }
  return null;
}

 getIcon(type: string): string {
  switch (type) {
    case 'TASK_COMPLETE': return '✅';
    case 'CHECKLIST_COMPLETE': return '🎉';
    case 'COMMENT': return '💬';
    case 'ATTACHMENT': return '📎';
    case 'INFO': return '🔔';
    default: return '📋';
  }
}

  getTimeAgo(createdAt: string): string {
    const diff = Math.floor((Date.now() - new Date(createdAt).getTime()) / 1000);
    if (diff < 60) return 'Just now';
    if (diff < 3600) return Math.floor(diff / 60) + 'm ago';
    if (diff < 86400) return Math.floor(diff / 3600) + 'h ago';
    return Math.floor(diff / 86400) + 'd ago';
  }

  ngOnDestroy(): void {
    this.pollingSub?.unsubscribe();
  }
}