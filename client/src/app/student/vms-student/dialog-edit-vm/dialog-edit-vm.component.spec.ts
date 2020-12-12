import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogEditVmComponent } from './dialog-edit-vm.component';

describe('DialogEditVmComponent', () => {
  let component: DialogEditVmComponent;
  let fixture: ComponentFixture<DialogEditVmComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DialogEditVmComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogEditVmComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
