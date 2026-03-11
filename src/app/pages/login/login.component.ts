import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

interface LoginForm {
  email: string;
  password: string;
}

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

  loginObj: LoginForm = {
    email: '',
    password: ''
  };

  constructor(
    private router: Router,
    private authService: AuthService
  ) {}

  onLogin(): void {

    if (this.loginObj.email && this.loginObj.password) {

      const success = this.authService.login(
        this.loginObj.email,
        this.loginObj.password
      );

      if (success) {
        if (this.authService.isAdmin()) {
          this.router.navigate(['/admin-dashboard']);
        } else {
          this.router.navigate(['/dashboard']);
        }
      } else {
        alert('Login failed. Please try again.');
      }

    } else {
      alert('Please enter email and password');
    }
  }
}