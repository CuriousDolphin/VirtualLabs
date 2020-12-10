import { Component, Input, OnInit } from '@angular/core';
import { Team } from 'src/app/models/team.model';
import { VmModel } from 'src/app/models/vm-model.model';
import { VmInstance } from 'src/app/models/vm-instance.model';

@Component({
  selector: 'app-vms-teacher',
  templateUrl: './vms-teacher.component.html',
  styleUrls: ['./vms-teacher.component.sass']
})
export class VmsTeacherComponent implements OnInit {

  @Input() set teams(teams: Team[]) {
    this._teams = teams;
  }
  @Input() set vmInstances(vmInstances: VmInstance[]) {
    this._vmInstances = vmInstances;
  }
  @Input() set vmModel(vmModel: VmModel[]) {
    this._vmModel = vmModel;
  }

_teams: Team[];
_vmInstances: VmInstance[];
_vmModel: VmModel[];

  constructor() { }

  ngOnInit(): void {
  }

}
