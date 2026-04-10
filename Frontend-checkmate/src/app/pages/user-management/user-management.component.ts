import { Component, OnInit } from '@angular/core';
import { UserService } from '../../core/services/user.service';
import { AuthService } from '../../core/services/auth.service';

interface User {
  id?: number;
  name: string;
  department: string;
  role: string;
  active: boolean;
}

@Component({
  selector: 'app-user-management',
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.css']
})
export class UserManagementComponent implements OnInit {

  searchText = '';
  newTask = ''; // (optional, can remove later if not needed)

  isEditing = false;
  isAdding = false;

  users: User[] = [];
  selectedUser: User | null = null;

  showSuccessPopup = false;
generatedEmail = '';
generatedPassword = '';

  // 🔥 tasks now come from checklist
  userTasks: string[] = [];

  formUser: User = this.getEmptyUser();


  constructor(
    private userService: UserService, 
    private authService: AuthService  
  ) {}

  ngOnInit() {
    this.loadUsers();
  }

  // 🔄 LOAD USERS
  loadUsers() {
    this.userService.getAll().subscribe((res: any[]) => {
      console.log('Users:', res);
      this.users = [...res];
    });
  }

  getEmptyUser(): User {
    return {
      name: '',
      department: '',
      role: '',
      active: true
    };
  }

  get filteredUsers() {
    return this.users.filter(user =>
      user.name.toLowerCase().includes(this.searchText.toLowerCase())
    );
  }

  // 🔥 SELECT USER + FETCH TASKS FROM CHECKLIST
  selectUser(user: User) {
    this.selectedUser = user;
    this.isEditing = false;
    this.isAdding = false;
    this.formUser = { ...user };

    // 🔥 fetch tasks from backend (checklist)
    this.userService.getUserTasks(user.name).subscribe((tasks: string[]) => {
      this.userTasks = tasks || [];
    });
  }

  openAddUser() {
    this.isAdding = true;
    this.isEditing = false;
    this.selectedUser = null;
    this.formUser = this.getEmptyUser();
  }

  openEditUser() {
    if (!this.selectedUser) return;
    this.isEditing = true;
    this.isAdding = false;
    this.formUser = { ...this.selectedUser };
  }

  saveUser() {

    if (this.isAdding) {
      this.authService.registerNewEmployee(this.formUser).subscribe({
        next: (response: any) => {
          // 1. Pehle Popup dikhao
          this.generatedEmail = response.email;
        this.generatedPassword = response.password;
        this.showSuccessPopup = true;

        this.loadUsers();
        this.selectedUser = { ...this.formUser };
        this.isAdding = false;
        },
        error: (err) => {
          alert('Error: ' + (err.error?.error || 'Could not add user.'));
        }
      });
    }

    // ✏️ EDIT USER
    if (this.isEditing && this.selectedUser?.id) {
      this.userService.update(this.selectedUser.id, this.formUser)
        .subscribe((updatedUser: any) => {

          this.loadUsers();
          this.selectedUser = updatedUser;

          this.isEditing = false;
        });
    }
  }



closeSuccessPopup() {
  this.showSuccessPopup = false;
}

  cancel() {
    this.isEditing = false;
    this.isAdding = false;
  }

  disableAccount() {
    if (this.selectedUser?.id) {

      const updatedUser = {
        ...this.selectedUser,
        active: false
      };

      this.userService.update(this.selectedUser.id, updatedUser)
        .subscribe((res: any) => {
          this.selectedUser = res;
          this.loadUsers();
        });
    }
  }

  //  REMOVE TASK (optional UI only, no DB change)
  removeTask(task: string) {
    this.userTasks = this.userTasks.filter(t => t !== task);
  }

  // 🗑 DELETE USER
  deleteUser(id: number) {
    if (confirm('Delete user?')) {
      this.userService.delete(id).subscribe(() => {
        this.selectedUser = null;
        this.userTasks = [];
        this.loadUsers();
      });
    }
  }
}