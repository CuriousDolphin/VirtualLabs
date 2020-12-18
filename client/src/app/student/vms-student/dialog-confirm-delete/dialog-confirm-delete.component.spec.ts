import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogConfirmDeleteComponent } from './dialog-confirm-delete.component';

describe('DialogConfirmDeleteComponent', () => {
  let component: DialogConfirmDeleteComponent;
  let fixture: ComponentFixture<DialogConfirmDeleteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DialogConfirmDeleteComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogConfirmDeleteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
