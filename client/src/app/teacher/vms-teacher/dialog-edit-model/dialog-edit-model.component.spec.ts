import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogEditModelComponent } from './dialog-edit-model.component';

describe('DialogEditModelComponent', () => {
  let component: DialogEditModelComponent;
  let fixture: ComponentFixture<DialogEditModelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DialogEditModelComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogEditModelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
