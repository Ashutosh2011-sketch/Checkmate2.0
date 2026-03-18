import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AllChecklistsComponent } from './all-checklists.component';

describe('AllChecklistsComponent', () => {
  let component: AllChecklistsComponent;
  let fixture: ComponentFixture<AllChecklistsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AllChecklistsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AllChecklistsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
