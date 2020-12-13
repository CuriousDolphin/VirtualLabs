import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OpenVmComponent } from './open-vm.component';

describe('OpenVmComponent', () => {
  let component: OpenVmComponent;
  let fixture: ComponentFixture<OpenVmComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ OpenVmComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OpenVmComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
