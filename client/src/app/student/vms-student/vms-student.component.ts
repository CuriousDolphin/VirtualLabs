import { Component, Input, OnInit } from '@angular/core';
import { Team } from 'src/app/models/team.model';
import { VmInstance } from 'src/app/models/vm-instance.model';

@Component({
  selector: 'app-vms-student',
  templateUrl: './vms-student.component.html',
  styleUrls: ['./vms-student.component.sass']
})
export class VmsStudentComponent implements OnInit {

  @Input() vmInstances: VmInstance[];
  @Input() studentId: String;

  constructor() { }

  ngOnInit(): void {
    
  }

}
