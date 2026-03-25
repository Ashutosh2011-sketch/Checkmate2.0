import { Component } from '@angular/core';

@Component({
  selector: 'app-notifications',
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.css']
})
export class AdminNotificationsComponent {

  notificationsList = [
    {
      icon: '🔔',
      title: "New Assignment: 'Complete IT Setup for John Doe'",
      description: "Admin has assigned this task to you for quick review.",
      borderColor: '#4ade80' 
    },
    {
      icon: '⚙️',
      title: "Overdue Task:",
      description: "'Quarterly Compliance Audit' deadline passed.",
      borderColor: '#4ade80'
    },
    {
      icon: '⚙️',
      title: "Overdue Task:",
      description: "'Quarterly Compliance Audit' deadline passed.",
      borderColor: '#4ade80'
    },
    {
      icon: '📋',
      title: "Workflow Approval",
      description: "Vendor IT Setup for new VPN Client.",
      borderColor: '#4ade80'
    },
    {
      icon: '💬',
      title: "New Comment:",
      description: "John Lee commented on 'Install VPN Client'.",
      borderColor: '#4ade80'
    }
  ];

  markAllAsRead() {
    console.log("All notifications marked as read!");
    this.notificationsList = [];
  }
}
