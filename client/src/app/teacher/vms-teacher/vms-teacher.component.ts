import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Team } from 'src/app/models/team.model';
import { VmModel } from 'src/app/models/vm-model.model';
import { VmInstance } from 'src/app/models/vm-instance.model';
import { BehaviorSubject } from 'rxjs';
import { Course } from 'src/app/models/course.model';
import * as _ from 'lodash';
import { DialogEditModelComponent } from './dialog-edit-model/dialog-edit-model.component';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-vms-teacher',
  templateUrl: './vms-teacher.component.html',
  styleUrls: ['./vms-teacher.component.sass']
})
export class VmsTeacherComponent implements OnInit {

  @Output() editModel = new EventEmitter<JSON>();

  editModelDialog: MatDialogRef<DialogEditModelComponent, any>

  courseAcronym$: BehaviorSubject<String> = new BehaviorSubject("");

  @Input() set teams(teams: Team[]) {
    if (teams !== null) {
      this._teams = teams;
      this.hasLoadedTeams = true;
    }
  }
  @Input() set vmInstances(vmInstances: VmInstance[]) {
    if (vmInstances !== null) {
      this._vmInstances = vmInstances;
      this.hasLoadedVmInstances = true;
    }
  }
  @Input() set vmModel(vmModel: VmModel) {
    if (vmModel !== null) {
      this._vmModel = vmModel;
      this.hasLoadedVmModel = true;
    }
  }
  @Input() currentCourse: Course;

  _teams: Team[];
  _vmInstances: VmInstance[];
  _vmModel: VmModel;
  hasLoadedVmInstances = false;
  hasLoadedVmModel = false;
  hasLoadedTeams = false;

  constructor(public dialog: MatDialog) { }

  ngOnInit(): void {
  }

  countRunningVms(vms: VmInstance[]): number {
    return _.sumBy(vms, (vm) => { return vm.state });
  }

  countVcpus(vms: VmInstance[]): number {
    return _.sumBy(vms, (vm) => { return vm.countVcpus });
  }

  countRam(vms: VmInstance[]): number {
    return _.sumBy(vms, (vm) => { return vm.countRam });
  }

  countDisk(vms: VmInstance[]): number {
    return _.sumBy(vms, (vm) => { return vm.countDisks });
  }

  openEditModel(vmModel: VmModel): void {
    console.log("----- "+this._teams.map((t) => {return t.vmInstances.length}).sort()[0])
    this.editModelDialog = this.dialog.open(DialogEditModelComponent, {
      data: {
        maxVms: vmModel.maxVms,
        minVms: this._teams.map((t) => {return t.vmInstances.length}).sort[0],
        maxRunningVms: vmModel.maxRunningVms,
        //minRunningVms: _.map(_.groupBy(this._teams.filter((t) => {t.state === 1}), 'group_by'), (t) => {summed: _.sumBy(t, 'sum_me')}),
        maxVcpus: vmModel.maxVcpus,
        maxRam: vmModel.maxRam,
        maxDisk: vmModel.maxDisk,
        courseAc: this.courseAcronym$.getValue(),
      },
      width: "22%",
    });
    this.editModelDialog.afterClosed().subscribe((newModel) => { this.emitEditModel(newModel) });
  }

  public emitEditModel(newModel: JSON) {
    if (newModel !== undefined) {
      if (newModel['maxVms'] != this._vmModel.maxVms ||
        newModel['maxRunningVms'] != this._vmModel.maxRunningVms ||
        newModel['maxVcpus'] != this._vmModel.maxVcpus ||
        newModel['maxRam'] != this._vmModel.maxRam ||
        newModel['maxDisk'] != this._vmModel.maxDisk)
        this.editModel.emit(newModel);
    }
  }

}
