import { Component, Input, OnInit, Output, EventEmitter } from '@angular/core';
import { Team } from 'src/app/models/team.model';
import { VmInstance } from 'src/app/models/vm-instance.model';
import * as _ from "lodash";
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { DialogCreateVmComponent } from './dialog-create-vm/dialog-create-vm.component';
import { VmModel } from 'src/app/models/vm-model.model';
import { DialogEditVmComponent } from './dialog-edit-vm/dialog-edit-vm.component';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { OpenVmComponent } from './open-vm/open-vm.component';

@Component({
  selector: 'app-vms-student',
  templateUrl: './vms-student.component.html',
  styleUrls: ['./vms-student.component.sass']
})
export class VmsStudentComponent implements OnInit {

  canRun$: BehaviorSubject<boolean> = new BehaviorSubject(true);
  canCreate$: BehaviorSubject<number> = new BehaviorSubject(0);

  @Output() deleteVm = new EventEmitter<VmInstance>();
  @Output() startVm = new EventEmitter<VmInstance>();
  @Output() stopVm = new EventEmitter<VmInstance>();
  @Output() createVm = new EventEmitter<JSON>();
  @Output() editVm = new EventEmitter<JSON>();

  @Input() studentId: String;
  @Input() course: String;
  @Input() courseAc: String;
  @Input() set vmInstances(vmInstances: VmInstance[]) {
    if (vmInstances !== null) {
      this._vmInstances = vmInstances.sort((vm1: VmInstance, vm2: VmInstance) => {
        if (vm1.id > vm2.id) return 1;
        else return -1;
      });
      this.hasLoadedVmInstances = true;
    }
  }
  @Input() set vmModel(vmModel: VmModel) {
    if (vmModel !== null) {
      this._vmModel = vmModel;
      this.hasLoadedVmModel = true;
    }
  }
  @Input() set teams(teams: Team[]) {
    if (teams != null) {
      this.team = _.find(teams, (team) => team.status === 1);
      if (this.team != null) {
        this.hasTeam = true;
      } else {
        this.hasTeam = false;
      }
    }
  }
  dialogCreateRef: MatDialogRef<DialogCreateVmComponent, any>
  dialogEditRef: MatDialogRef<DialogEditVmComponent, any>
  openVmDialog: MatDialogRef<OpenVmComponent, any>
  hasTeam = false;
  team: Team;
  _vmInstances: VmInstance[];
  _vmModel: VmModel;
  canCreateNumber: number[];
  vcpusAvailable: String;
  ramAvailable: String;
  diskAvailable: String;
  hasLoadedVmModel = false;
  hasLoadedVmInstances = false;

  constructor(public dialog: MatDialog) { }

  ngOnInit(): void {
  }

  ngOnChanges(): void {
    if (this._vmModel !== undefined && this._vmInstances !== undefined) {
      this.canRun$.next(this._vmInstances.filter((vm) => (vm.state === 1)).length < this._vmModel.maxRunningVms);
      this.canCreate$.next(this._vmModel.maxVms - this._vmInstances.length);
      if (this.canCreate$.getValue() > 1)
        this.canCreateNumber = Array(this.canCreate$.getValue() - 1);
      else
        this.canCreateNumber = Array();
      this.vcpusAvailable = (this._vmModel.maxVcpus - _.sumBy(this._vmInstances, (vm) => { return vm.countVcpus })).toFixed();
      this.ramAvailable = (this._vmModel.maxRam - _.sumBy(this._vmInstances, (vm) => { return vm.countRam })).toFixed();
      this.diskAvailable = (this._vmModel.maxDisk - _.sumBy(this._vmInstances, (vm) => { return vm.countDisks })).toFixed();
    }
  }



  createVmDialog(): void {
    this.dialogCreateRef = this.dialog.open(DialogCreateVmComponent, {
      data: {
        countVcpus: ((this._vmModel.maxVcpus - _.sumBy(this._vmInstances, (vm) => { return vm.countVcpus })) / (this.canCreateNumber.length + 1)).toFixed(),
        countRam: ((this._vmModel.maxRam - _.sumBy(this._vmInstances, (vm) => { return vm.countRam })) / (this.canCreateNumber.length + 1)).toFixed(),
        countDisk: ((this._vmModel.maxDisk - _.sumBy(this._vmInstances, (vm) => { return vm.countDisks })) / (this.canCreateNumber.length + 1)).toFixed(),
        owner: false,
        maxVcpus: (this._vmModel.maxVcpus - _.sumBy(this._vmInstances, (vm) => { return vm.countVcpus })),
        maxRam: (this._vmModel.maxRam - _.sumBy(this._vmInstances, (vm) => { return vm.countRam })),
        maxDisk: (this._vmModel.maxDisk - _.sumBy(this._vmInstances, (vm) => { return vm.countDisks }))
      },
      width: "22%",
    });
    this.dialogCreateRef.afterClosed().subscribe((newVm) => { this.emitCreateVm(newVm) });
  }

  editVmDialog(vm: VmInstance): void {
    this.dialogEditRef = this.dialog.open(DialogEditVmComponent, {
      data: {
        countVcpus: vm.countVcpus,
        countRam: vm.countRam,
        countDisk: vm.countDisks,
        maxVcpus: (vm.countVcpus + this._vmModel.maxVcpus - _.sumBy(this._vmInstances, (vm) => { return vm.countVcpus })),
        maxRam: (vm.countRam + this._vmModel.maxRam - _.sumBy(this._vmInstances, (vm) => { return vm.countRam })),
        maxDisk: (vm.countDisks + this._vmModel.maxDisk - _.sumBy(this._vmInstances, (vm) => { return vm.countDisks })),
        id: vm.id,
        fakeId: this._vmInstances.indexOf(vm)
      },
      width: "22%",
    });
    this.dialogEditRef.afterClosed().subscribe((newVm) => {
      if (newVm !== undefined)
        if (newVm["countVcpus"] !== vm.countVcpus ||
          newVm["countRam"] !== vm.countRam ||
          newVm["countDisks"] !== vm.countDisks)
          this.emitEditVm(newVm)
    });
  }

  OpenVm(vm: VmInstance): void {
    this.openVmDialog = this.dialog.open(OpenVmComponent, {
      data: {
        countVcpus: vm.countVcpus,
        countRam: vm.countRam,
        countDisk: vm.countDisks,
        courseAc: this.courseAc,
        image: vm.image, //TODO: link
      },
    });
  }

  public emitDeleteVm(vm: VmInstance) {
    this.deleteVm.emit(vm);
  }

  public emitStartVm(vm: VmInstance) {
    this.startVm.emit(vm);
  }

  public emitStopVm(vm: VmInstance) {
    this.stopVm.emit(vm);
  }

  public emitCreateVm(newVm: JSON) {
    if (newVm !== undefined) {
      this.createVm.emit(newVm);
    }
  }

  public emitEditVm(newVm: JSON) {
    if (newVm !== undefined) {
      this.editVm.emit(newVm);
    }
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

}
