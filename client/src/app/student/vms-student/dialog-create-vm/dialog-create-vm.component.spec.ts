import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DialogCreateVmComponent } from './dialog-create-vm.component';

describe('DialogCreateVmComponent', () => {
  let component: DialogCreateVmComponent;
  let fixture: ComponentFixture<DialogCreateVmComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DialogCreateVmComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogCreateVmComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
