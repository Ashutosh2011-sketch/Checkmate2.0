import { Component } from '@angular/core';

@Component({
  selector: 'app-admin-profile',
  templateUrl: './admin-profile.component.html',
  styleUrls: ['./admin-profile.component.css']
})
export class AdminProfileComponent {

  role: 'Admin' | 'User' = 'Admin';

  activityLog: string[] = [];

  user = {
    profileImage: '',
    fullName: 'John Doe',
    email: 'john.doe@company.com',
    jobTitle: 'IT Support',
    department: 'IT Department',
    inAppAlerts: true,
    newAssignments: true,
    taskOverdue: false,
    workflowApprovals: false,
    smsNotifications: false,
    weeklySummary: true,
    newPassword: '',
    confirmPassword: ''
  };

  originalUser = { ...this.user };

  filters = {
    dateRange: '',
    department: '',
    owner: ''
  };

  dateOptions = ['Today', 'This Week', 'This Month', 'Custom'];
  departmentOptions = ['IT', 'HR', 'Marketing', 'Sales'];
  ownerOptions = ['Admin', 'Manager', 'User'];

  passwordError = '';

  applyFilters() {
    console.log('Filters Applied:', this.filters);
  }

  validatePassword(): boolean {

    if (!this.user.newPassword && !this.user.confirmPassword) {
      return true;
    }

    if (this.user.newPassword.length < 6) {
      this.passwordError = 'Password must be at least 6 characters.';
      return false;
    }

    if (this.user.newPassword !== this.user.confirmPassword) {
      this.passwordError = 'Passwords do not match.';
      return false;
    }

    this.passwordError = '';
    return true;
  }

  uploadProfileImage(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.user.profileImage = file.name;
      this.activityLog.push(`Profile image updated: ${file.name}`);
    }
  }

  saveChanges() {

    if (!this.validatePassword()) {
      return;
    }

    Object.keys(this.user).forEach(key => {
      if ((this.user as any)[key] !== (this.originalUser as any)[key]) {
        this.activityLog.push(`${key} updated.`);
      }
    });

    this.originalUser = { ...this.user };
    alert('Changes saved successfully!');
  }

  updateProfile() {
    alert('Profile updated successfully!');
  }

}