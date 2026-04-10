import { Component, OnInit } from '@angular/core';
import { ProfileService } from 'src/app/core/services/profile.service';

interface ProfileForm {
  fullName: string;
  email: string;
  jobTitle: string;
  department: string;
  inAppAlerts: boolean;
  emailNewAssignments: boolean;
  emailTaskOverdue: boolean;
  emailWorkflowApprovals: boolean;
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  constructor(private profileService: ProfileService){}

  user: ProfileForm = {
    fullName: 'John Doe',
    email: 'john.doe@company.com',
    jobTitle: 'IT Support',
    department: 'IT Department',
    inAppAlerts: true,
    emailNewAssignments: true,
    emailTaskOverdue: false,
    emailWorkflowApprovals: false,
    currentPassword:'',
    newPassword: '',
    confirmPassword: ''
  };

  ngOnInit(): void {
    this.loadUserProfile();
  }

  loadUserProfile() {
  this.profileService.getCurrentUser().subscribe({
    next: (data) => {
      console.log("Backend Entity Data:", data);
      
      this.user.fullName = data.name;         
      this.user.email = data.email;          
      this.user.jobTitle = data.designation;  
      this.user.department = data.department; 
    },
    error: (err) => {
      console.error("Profile load failed:", err);
    }
  });
}

  updateProfile() {

  if (this.user.newPassword && this.user.newPassword !== this.user.confirmPassword) {
    alert('New Password and Confirm Password do not match!');
    return;
  }

  this.profileService.updateProfile(this.user).subscribe({
    next: (res) => {
      alert(res); 
      this.user.currentPassword = '';
      this.user.newPassword = '';
      this.user.confirmPassword = '';
    },
    error: (err) => {
      alert('Error: ' + (err.error || 'Update failed'));
    }
  });
}
}