
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
      
      this.authService.login(this.loginObj.email, this.loginObj.password).subscribe({
        
        next: (response: any) => {
          
          this.authService.saveTokenAndRole(response.token, response.role);
          this.authService.savePermissions(response.permissions || []);

          localStorage.setItem('userName', response.name);
          localStorage.setItem('designation', response.designation || '');

          if (this.authService.isAdmin()) {
            this.router.navigate(['/admin-dashboard']);
          } else {
            this.router.navigate(['/dashboard']);
          }
        },

        error: (err) => {
          alert('Error: Invalid Email or Password!');
          console.error(err);
        }

      });

    } else {
      alert('Please enter both email and password!');
    }
  }
}