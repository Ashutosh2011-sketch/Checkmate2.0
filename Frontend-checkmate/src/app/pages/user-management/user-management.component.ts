import { Component, OnInit } from '@angular/core';
import { UserService } from '../../services/user.service';

interface User {
  id?: number;
  name: string;
  department: string;
  role: string;
  active: boolean;
  tasks: string[];
}

@Component({
  selector: 'app-user-management',
  templateUrl: './user-management.component.html',
  styleUrls: ['./user-management.component.css']
})
export class UserManagementComponent implements OnInit {

  searchText = '';
  newTask = '';
  initialTask = '';

  isEditing = false;
  isAdding = false;

  users: User[] = [];
  selectedUser: User | null = null;

  formUser: User = this.getEmptyUser();

  constructor(private userService: UserService) {}

  ngOnInit() {
    this.loadUsers();
  }

  // 🔄 LOAD USERS FROM BACKEND
  loadUsers() {
    this.userService.getAll().subscribe((res: any[]) => {
      console.log('Users:', res); // DEBUG
      this.users = [...res]; // 🔥 force UI refresh
    });
  }

  getEmptyUser(): User {
    return {
      name: '',
      department: '',
      role: '',
      active: true,
      tasks: []
    };
  }

  get filteredUsers() {
    return this.users.filter(user =>
      user.name.toLowerCase().includes(this.searchText.toLowerCase())
    );
  }

  selectUser(user: User) {
    this.selectedUser = user;
    this.isEditing = false;
    this.isAdding = false;
    this.formUser = { ...user };
  }

  openAddUser() {
    this.isAdding = true;
    this.isEditing = false;
    this.selectedUser = null;
    this.formUser = this.getEmptyUser();
    this.initialTask = '';
  }

  openEditUser() {
    if (!this.selectedUser) return;
    this.isEditing = true;
    this.isAdding = false;
    this.formUser = { ...this.selectedUser };
  }

  saveUser() {

    // ➕ ADD USER
    if (this.isAdding) {

      if (this.initialTask.trim()) {
        this.formUser.tasks.push(this.initialTask.trim());
      }

      this.userService.create(this.formUser).subscribe((newUser: any) => {

        // ✅ Reload users from DB
        this.loadUsers();

        // ✅ Select new user after slight delay (ensures list updated)
        setTimeout(() => {
          this.selectedUser = newUser;
        }, 100);

        this.isAdding = false;
        this.initialTask = '';
      });
    }

    // ✏️ EDIT USER
    if (this.isEditing && this.selectedUser?.id) {

      this.userService.update(this.selectedUser.id, this.formUser)
        .subscribe((updatedUser: any) => {

          this.loadUsers();

          // keep selection updated
          this.selectedUser = updatedUser;

          this.isEditing = false;
        });
    }
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

  // ➕ ADD TASK
  allocateTask() {
    if (this.selectedUser?.id && this.newTask.trim()) {

      const updatedUser = {
        ...this.selectedUser,
        tasks: [...this.selectedUser.tasks, this.newTask.trim()]
      };

      this.userService.update(this.selectedUser.id, updatedUser)
        .subscribe((res: any) => {
          this.selectedUser = res;
          this.newTask = '';
          this.loadUsers();
        });
    }
  }

  // ❌ REMOVE TASK
  removeTask(task: string) {
    if (!this.selectedUser?.id) return;

    const updatedUser = {
      ...this.selectedUser,
      tasks: this.selectedUser.tasks.filter(t => t !== task)
    };

    this.userService.update(this.selectedUser.id, updatedUser)
      .subscribe((res: any) => {
        this.selectedUser = res;
        this.loadUsers();
      });
  }

  // 🗑 DELETE USER
  deleteUser(id: number) {
    if (confirm('Delete user?')) {
      this.userService.delete(id).subscribe(() => {
        this.selectedUser = null;
        this.loadUsers();
      });
    }
  }
}