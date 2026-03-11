import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddRoleDrawerComponent } from './add-role-drawer.component';

describe('AddRoleDrawerComponent', () => {
  let component: AddRoleDrawerComponent;
  let fixture: ComponentFixture<AddRoleDrawerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AddRoleDrawerComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddRoleDrawerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
