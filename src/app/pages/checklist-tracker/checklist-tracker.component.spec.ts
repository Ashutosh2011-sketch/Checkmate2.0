import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChecklistTrackerComponent } from './checklist-tracker.component';

describe('ChecklistTrackerComponent', () => {
  let component: ChecklistTrackerComponent;
  let fixture: ComponentFixture<ChecklistTrackerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChecklistTrackerComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChecklistTrackerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
