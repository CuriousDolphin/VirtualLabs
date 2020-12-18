import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Team } from 'src/app/models/team.model';
import { VmModel } from 'src/app/models/vm-model.model';
import { VmInstance } from 'src/app/models/vm-instance.model';
import { BehaviorSubject } from 'rxjs';
import { Course } from 'src/app/models/course.model';
import * as _ from 'lodash';
import { DialogEditModelComponent } from './dialog-edit-model/dialog-edit-model.component';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { OpenVmComponent } from 'src/app/student/vms-student/open-vm/open-vm.component';

@Component({
  selector: 'app-vms-teacher',
  templateUrl: './vms-teacher.component.html',
  styleUrls: ['./vms-teacher.component.sass']
})
export class VmsTeacherComponent implements OnInit {

  @Output() editModel = new EventEmitter<JSON>();

  editModelDialog: MatDialogRef<DialogEditModelComponent, any>
  openVmDialog: MatDialogRef<OpenVmComponent, any>

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
  @Input() set currentCourse(currentCourse: Course) {
    if (currentCourse !== null)
      this.courseAcronym$.next(currentCourse.acronym)
  }

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
    this.editModelDialog = this.dialog.open(DialogEditModelComponent, {
      data: {
        maxVms: vmModel.maxVms,
        minVms: this._teams.length > 0 && this._teams.map((t) => { return t.vmInstances.length }).reduce((a, b) => a + b, 0) !== 0 ?
          this._teams.map((t) => { return t.vmInstances.length }).sort()[0] : 1,
        maxRunningVms: vmModel.maxRunningVms,
        minRunningVms: this._teams.length > 0 && this._teams.map((t) => { return t.vmInstances.length }).reduce((a, b) => a + b, 0) !== 0 ?
          this._teams.map((t) => { return t.vmInstances.filter((vm) => { return vm.state === 1 }).length }).sort()[0] : 1,
        maxVcpus: vmModel.maxVcpus,
        minVcpus: this._teams.length > 0 && this._teams.map((t) => { return t.vmInstances.length }).reduce((a, b) => a + b, 0) !== 0 ?
          this._teams.map((t) => { return t.vmInstances.map((vm) => { return vm.countVcpus }).reduce((a, b) => a + b, 0) }).sort()[0] : 1,
        maxRam: vmModel.maxRam,
        minRam: this._teams.length > 0 && this._teams.map((t) => { return t.vmInstances.length }).reduce((a, b) => a + b, 0) !== 0 ?
          this._teams.map((t) => { return t.vmInstances.map((vm) => { return vm.countRam }).reduce((a, b) => a + b, 0) }).sort()[0] : 1,
        maxDisk: vmModel.maxDisk,
        minDisk: this._teams.length > 0 && this._teams.map((t) => { return t.vmInstances.length }).reduce((a, b) => a + b, 0) !== 0 ?
          this._teams.map((t) => { return t.vmInstances.map((vm) => { return vm.countDisks }).reduce((a, b) => a + b, 0) }).sort()[0] : 1,
        courseAc: this.courseAcronym$.getValue(),
      },
      width: "22%",
    });
    this.editModelDialog.afterClosed().subscribe((newModel) => { this.emitEditModel(newModel) });
  }

  public emitEditModel(newModel: JSON): void{
    if (newModel !== undefined) {
      if (newModel['maxVms'] != this._vmModel.maxVms ||
        newModel['maxRunningVms'] != this._vmModel.maxRunningVms ||
        newModel['maxVcpus'] != this._vmModel.maxVcpus ||
        newModel['maxRam'] != this._vmModel.maxRam ||
        newModel['maxDisk'] != this._vmModel.maxDisk)
        this.editModel.emit(newModel);
    }
  }

  OpenVm(vm: VmInstance, team: String): void{
    this.openVmDialog = this.dialog.open(OpenVmComponent, {
      data: {
        countVcpus: vm.countVcpus,
        countRam: vm.countRam,
        countDisk: vm.countDisks,
        courseAc: this.courseAcronym$.getValue(),
        teamName: team,
        image: vm.image, //TODO: link
      },
    });
  }

}
