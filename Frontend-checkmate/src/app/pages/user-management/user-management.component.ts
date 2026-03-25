import { Component } from '@angular/core';

interface User {
  id: number;
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
export class UserManagementComponent {

  searchText = '';
  newTask = '';
  initialTask = '';   // NEW for Add User

  isEditing = false;
  isAdding = false;

  users: User[] = [
    { id: 1, name: 'Ram', department: 'Admin', role: 'Reviewer', active: true, tasks: ['Audit Review'] },
    { id: 2, name: 'Garv', department: 'HR', role: 'HR', active: true, tasks: [] },
    { id: 3, name: 'John Doe', department: 'Engineering', role: 'Engineer', active: true, tasks: ['System Setup'] }
  ];

  selectedUser: User | null = null;

  formUser: User = this.getEmptyUser();

  getEmptyUser(): User {
    return {
      id: 0,
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

    //  ADD USER LOGIC
    if (this.isAdding) {

      if (this.initialTask.trim() !== '') {
        this.formUser.tasks.push(this.initialTask.trim());
      }

      this.formUser.id = Date.now();
      this.users.push({ ...this.formUser });

      this.isAdding = false;
      this.initialTask = '';
    }

    //  EDIT USER LOGIC
    if (this.isEditing && this.selectedUser) {
      Object.assign(this.selectedUser, this.formUser);
      this.isEditing = false;
    }
  }

  cancel() {
    this.isEditing = false;
    this.isAdding = false;
  }

  disableAccount() {
    if (this.selectedUser) {
      this.selectedUser.active = false;
    }
  }

  allocateTask() {
    if (this.selectedUser && this.newTask.trim() !== '') {
      this.selectedUser.tasks.push(this.newTask.trim());
      this.newTask = '';
    }
  }

  removeTask(task: string) {
    if (!this.selectedUser) return;
    this.selectedUser.tasks =
      this.selectedUser.tasks.filter(t => t !== task);
  }
}