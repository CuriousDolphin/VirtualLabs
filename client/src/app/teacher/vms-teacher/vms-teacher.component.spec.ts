import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VmsTeacherComponent } from './vms-teacher.component';

describe('VmsTeacherComponent', () => {
  let component: VmsTeacherComponent;
  let fixture: ComponentFixture<VmsTeacherComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ VmsTeacherComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VmsTeacherComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
