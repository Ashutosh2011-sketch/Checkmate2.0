import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'checkmate';
  isLandingPage = false;
  private sub: any;

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.updateLandingFlag();
    this.sub = this.router.events
      .pipe(filter((e): e is NavigationEnd => e instanceof NavigationEnd))
      .subscribe(() => this.updateLandingFlag());
  }

  ngOnDestroy(): void {
    if (this.sub) this.sub.unsubscribe();
  }

private updateLandingFlag(): void {
  const url = this.router.url.split('?')[0].split('#')[0];
  
  this.isLandingPage = url === '/' || url === '/landing' || url === '/login';
}
}
