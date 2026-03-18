import { Component } from '@angular/core';

interface ProfileForm {
  fullName: string;
  email: string;
  jobTitle: string;
  department: string;
  inAppAlerts: boolean;
  emailNewAssignments: boolean;
  emailTaskOverdue: boolean;
  emailWorkflowApprovals: boolean;
  newPassword: string;
  confirmPassword: string;
}

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent {

  user: ProfileForm = {
    fullName: 'John Doe',
    email: 'john.doe@company.com',
    jobTitle: 'IT Support',
    department: 'IT Department',
    inAppAlerts: true,
    emailNewAssignments: true,
    emailTaskOverdue: false,
    emailWorkflowApprovals: false,
    newPassword: '',
    confirmPassword: ''
  };

  saveChanges() {
    alert('Changes saved successfully!');
  }

  updateProfile() {
    alert('Profile updated successfully!');
  }
}