import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { UserDashboardComponent } from './user-dashboard.component';
import { DashboardService } from '../../core/services/dashboard.service';

describe('UserDashboardComponent', () => {

  let component: UserDashboardComponent;
  let fixture: ComponentFixture<UserDashboardComponent>;

  const mockDashboardService = {
    getDashboardData: jasmine.createSpy('getDashboardData').and.returnValue(
      of({
        progress: 65,
        assignedChecklists: ['Test Checklist'],
        claimedTasks: ['Test Task'],
        notifications: ['Test Notification']
      })
    )
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserDashboardComponent],
      providers: [
        { provide: DashboardService, useValue: mockDashboardService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(UserDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should call getDashboardData on init', () => {
    expect(mockDashboardService.getDashboardData).toHaveBeenCalled();
  });

  it('should load dashboard data correctly', () => {
    expect(component.dashboard).toBeTruthy();
    expect(component.dashboard.progress).toBe(65);
  });

});
