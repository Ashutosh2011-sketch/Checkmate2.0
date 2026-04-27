import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { ChecklistService } from './services/checklist.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'checkmate';
  isLandingPage = false;
  private sub: any;

  constructor(private router: Router, private checklistService: ChecklistService ) {}

  ngOnInit(): void {
    this.updateLandingFlag();
    this.sub = this.router.events
      .pipe(filter((e): e is NavigationEnd => e instanceof NavigationEnd))
      .subscribe(() => this.updateLandingFlag());
         this.checklistService.getAllChecklists().subscribe({
      next: (data) => console.log('Checklists ✅', data),
      error: (err) => console.log('Error ❌', err)
    });
  }

  ngOnDestroy(): void {
    if (this.sub) this.sub.unsubscribe();
  }

private updateLandingFlag(): void {
  const url = this.router.url.split('?')[0].split('#')[0];
  
  this.isLandingPage = url === '/' || url === '/landing' || url === '/login';
}
}
