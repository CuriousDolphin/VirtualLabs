import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StudentAssignmentAssignmentComponent } from './student-assignment-assignment.component';

describe('StudentAssignmentAssignmentComponent', () => {
  let component: StudentAssignmentAssignmentComponent;
  let fixture: ComponentFixture<StudentAssignmentAssignmentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StudentAssignmentAssignmentComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StudentAssignmentAssignmentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
