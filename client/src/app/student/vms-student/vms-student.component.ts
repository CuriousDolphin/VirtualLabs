import { Component, Input, OnInit, Output, EventEmitter } from '@angular/core';
import { Team } from 'src/app/models/team.model';
import { VmInstance } from 'src/app/models/vm-instance.model';
import * as _ from "lodash";

@Component({
  selector: 'app-vms-student',
  templateUrl: './vms-student.component.html',
  styleUrls: ['./vms-student.component.sass']
})
export class VmsStudentComponent implements OnInit {

  @Output() deleteVm = new EventEmitter<VmInstance>();
  @Output() startVm = new EventEmitter<VmInstance>();
  @Output() stopVm = new EventEmitter<VmInstance>();

  @Input() vmInstances: VmInstance[];
  @Input() studentId: String;
  hasTeam = false;
  _teams: Team[];
  team: Team;
  @Input() set teams(teams: Team[]) {
    if (teams != null) {
      this._teams = teams;
      this.team = _.find(this._teams, (team) => team.status === 1);
      if (this.team != null) {
        this.hasTeam = true;
      } else {
        this.hasTeam = false;
      }
    }
  }

  constructor() {}

  ngOnInit(): void {}

  public emitDeleteVm(vm: VmInstance) {
    this.deleteVm.emit(vm);
  }

  public emitStartVm(vm: VmInstance) {
    this.startVm.emit(vm);
  }

  public emitStopVm(vm: VmInstance) {
    this.stopVm.emit(vm);
  }

}
