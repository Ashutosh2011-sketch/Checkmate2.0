
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router'; 

@Component({
  selector: 'app-checklist-detail',
  templateUrl: './checklist-detail.component.html',
  styleUrls: ['./checklist-detail.component.css']
})
export class ChecklistDetailComponent implements OnInit {
  
  checklist: any; 

  constructor(private route: ActivatedRoute) {}

  ngOnInit() {
    const idFromUrl = Number(this.route.snapshot.paramMap.get('id'));

    const allChecklistsData = [
      {
        id: 1,
        title: 'Employee Onboarding - John Doe',
        status: 'In Progress',
        progress: 65,
        sections: [
          {
            title: 'Hardware Setup (Completed)',
            tasks: [
              { name: 'Laptop & Monitor Allocation', assignee: 'Sarah Chen', due: 'Oct 25', status: 'Completed', avatar: 'S' },
              { name: 'Desk Phone & Headset', assignee: 'Admin', due: 'Oct 26', status: 'Completed', avatar: 'A' }
            ]
          },
          {
            title: 'Software Installation (In Progress)',
            tasks: [
              { name: 'Install MS Office & VPN Client', assignee: 'John Lee', due: 'Oct 27', status: 'Completed', avatar: 'J' },
              { name: 'Setup Slack & Teams', status: 'In Progress', avatar: '🔒' }
            ]
          }
        ],
        auditLogs: [
          { user: 'Sarah Chen', action: 'attached Laptop_Serial.pdf', icon: '📎', isFile: false },
          { name: 'Laptop_Serial.pdf', icon: '📄', isFile: true }
        ],
        files: [{ name: 'Software_Licenses.docx', icon: '📄' }]
      },
      {
        id: 2,
        title: 'IT Asset Allocation - Q3',
        status: 'In Progress',
        progress: 20,
        sections: [
          {
            title: 'Inventory Check (Pending)',
            tasks: [
              { name: 'Count Laptops in Stock', assignee: 'Pratik', due: 'Oct 15', status: 'In Progress', avatar: 'P' },
              { name: 'Order new MacBooks', assignee: 'Pratik', due: 'Oct 18', status: 'Pending', avatar: 'P' }
            ]
          }
        ],
        auditLogs: [
          { user: 'Pratik', action: 'started inventory check', icon: '📝', isFile: false }
        ],
        files: [{ name: 'Vendor_Quotes.pdf', icon: '📄' }]
      },
      {
        id: 3,
        title: 'Quarterly Security Audit',
        status: 'Completed',
        progress: 100,
        sections: [
          {
            title: 'Server Audits (Completed)',
            tasks: [
              { name: 'Check Firewall Rules', assignee: 'Rahul', due: 'Oct 1', status: 'Completed', avatar: 'R' }
            ]
          }
        ],
        auditLogs: [
          { user: 'Rahul', action: 'approved the final report', icon: '✅', isFile: false },
          { name: 'Audit_Report_Q3.pdf', icon: '📄', isFile: true }
        ],
        files: [{ name: 'Audit_Report_Q3.pdf', icon: '📄' }]
      },
      {
        id: 4,
        title: 'Server Migration & Backup',
        status: 'In Progress',
        progress: 85,
        sections: [
          {
            title: 'Pre-Migration Tasks (Completed)',
            tasks: [
              { name: 'Full Database Backup', assignee: 'Vikram', due: 'Nov 1', status: 'Completed', avatar: 'V' },
              { name: 'Provision New AWS Servers', assignee: 'Vikram', due: 'Nov 2', status: 'Completed', avatar: 'V' }
            ]
          },
          {
            title: 'Data Transfer (In Progress)',
            tasks: [
              { name: 'Migrate User Storage', assignee: 'Vikram', due: 'Nov 4', status: 'In Progress', avatar: 'V' },
              { name: 'Update DNS Records', status: 'Pending', avatar: '🌐' }
            ]
          }
        ],
        auditLogs: [
          { user: 'Vikram', action: 'initiated AWS provisioning', icon: '☁️', isFile: false },
          { name: 'DB_Backup_Logs.txt', icon: '📄', isFile: true }
        ],
        files: [{ name: 'Migration_Architecture.pdf', icon: '📄' }]
      },
      {
        id: 5,
        title: 'Annual Performance Appraisals',
        status: 'Pending',
        progress: 0,
        sections: [
          {
            title: 'Preparation Phase (Pending)',
            tasks: [
              { name: 'Send Reminder Emails to Managers', assignee: 'Neha', due: 'Dec 1', status: 'Pending', avatar: 'N' },
              { name: 'Unlock Review Portal', assignee: 'IT Admin', due: 'Dec 5', status: 'Pending', avatar: 'I' }
            ]
          },
          {
            title: 'Review Phase (Locked)',
            tasks: [
              { name: 'Employee Self-Assessments', status: 'Sequential Dependency', avatar: '🔒' },
              { name: 'Manager 1-on-1 Meetings', status: 'Sequential Dependency', avatar: '🔒' }
            ]
          }
        ],
        auditLogs: [
          { user: 'Neha', action: 'created the appraisal schedule', icon: '📅', isFile: false }
        ],
        files: [
          { name: 'Appraisal_Guidelines_2026.pdf', icon: '📄' },
          { name: 'Rating_Scale_Matrix.xlsx', icon: '📊' }
        ]
      },
      {
        id: 6,
        title: 'Vendor Contract Renewal',
        status: 'Completed',
        progress: 100,
        sections: [
          {
            title: 'Legal Review (Completed)',
            tasks: [
              { name: 'Review SLA Terms', assignee: 'Suresh', due: 'Sep 20', status: 'Completed', avatar: 'S' },
              { name: 'Negotiate Pricing', assignee: 'Suresh', due: 'Sep 25', status: 'Completed', avatar: 'S' }
            ]
          },
          {
            title: 'Final Approvals (Completed)',
            tasks: [
              { name: 'Finance Director Sign-off', assignee: 'Finance Team', due: 'Sep 27', status: 'Completed', avatar: 'F' },
              { name: 'Upload Signed Copy', assignee: 'Suresh', due: 'Sep 28', status: 'Completed', avatar: 'S' }
            ]
          }
        ],
        auditLogs: [
          { user: 'Suresh', action: 'approved the revised SLA', icon: '⚖️', isFile: false },
          { user: 'Finance Team', action: 'cleared the budget', icon: '💰', isFile: false },
          { name: 'Signed_Contract_Final.pdf', icon: '📄', isFile: true }
        ],
        files: [
          { name: 'Signed_Contract_Final.pdf', icon: '📄' },
          { name: 'Vendor_KYC_Docs.zip', icon: '📁' }
        ]
      }
    ];

    this.checklist = allChecklistsData.find(item => item.id === idFromUrl);
  }

  getStatusClass(status: string) {
    if (status === 'Completed') return 'badge-green';
    if (status === 'In Progress') return 'badge-blue';
    return 'badge-grey';
  }
}