import { Component } from '@angular/core';

@Component({
  selector: 'app-not-found',
  template: `
    <div class="not-found-container">
      <h1>404</h1>
      <p>Page not found</p>
      <a routerLink="/dashboard" class="btn btn-primary">Go to Dashboard</a>
    </div>
  `,
  styles: [`
    .not-found-container {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      height: 100vh;
      text-align: center;
    }
    h1 { font-size: 72px; color: #3e3d8a; margin: 0; }
    p { font-size: 24px; color: #666; margin: 20px 0; }
  `]
})
export class NotFoundComponent {}
