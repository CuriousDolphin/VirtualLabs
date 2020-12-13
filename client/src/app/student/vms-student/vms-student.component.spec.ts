import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VmsStudentComponent } from './vms-student.component';

describe('VmsStudentComponent', () => {
  let component: VmsStudentComponent;
  let fixture: ComponentFixture<VmsStudentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ VmsStudentComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VmsStudentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
