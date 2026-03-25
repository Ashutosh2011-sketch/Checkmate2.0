import { Component } from '@angular/core';

@Component({
  selector: 'app-admin-tasks',
  template: `
    <div class="page-container">
      <h1>My Tasks</h1>
      <div class="card">
        <p>Task management coming soon...</p>
      </div>
    </div>
  `,
  styles: [`.page-container { padding: 20px; } h1 { margin-bottom: 20px; color: #333; }`]
})
export class AdminTasksComponent {}
