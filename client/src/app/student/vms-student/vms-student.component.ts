import { Component, Input, OnInit, Output, EventEmitter } from '@angular/core';
import { Team } from 'src/app/models/team.model';
import { VmInstance } from 'src/app/models/vm-instance.model';
import * as _ from "lodash";
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { DialogCreateVmComponent } from './dialog-create-vm/dialog-create-vm.component';
import { VmConfiguration } from 'src/app/models/vm-configuration.model';
import { DialogEditVmComponent } from './dialog-edit-vm/dialog-edit-vm.component';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { OpenVmComponent } from './open-vm/open-vm.component';

@Component({
  selector: 'app-vms-student',
  templateUrl: './vms-student.component.html',
  styleUrls: ['./vms-student.component.sass']
})
export class VmsStudentComponent implements OnInit {

  hasLoadedVmInstances$: Observable<VmInstance[]>;
  hasLoadedVmConfiguration$: Observable<VmConfiguration>;
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
    this._vmInstances = vmInstances.sort((vm1: VmInstance, vm2: VmInstance) => {
      if (vm1.id > vm2.id) return 1;
      else return -1;
    });
    this.hasLoadedVmInstances$ = of<VmInstance[]>(this._vmInstances);
  }
  @Input() set vmConfiguration(vmConfiguration: VmConfiguration) {
    if (vmConfiguration !== null) {
      this._vmConfiguration = vmConfiguration;
      this.hasLoadedVmConfiguration$ = of<VmConfiguration>(this._vmConfiguration);
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
  _vmConfiguration: VmConfiguration;
  canCreateNumber: number[];

  constructor(public dialog: MatDialog) { }

  ngOnInit(): void {
  }

  ngOnChanges(): void {
    if (this._vmConfiguration !== undefined && this._vmInstances !== undefined) {
      this.canRun$.next(this._vmInstances.filter((vm) => (vm.state === 1)).length < this._vmConfiguration.maxRunningVms);
      this.canCreate$.next(this._vmConfiguration.maxVms - this._vmInstances.length);
      if (this.canCreate$.getValue() > 1)
        this.canCreateNumber = Array(this.canCreate$.getValue() - 1);
      else
        this.canCreateNumber = Array();
    }
  }



  createVmDialog(): void {
    this.dialogCreateRef = this.dialog.open(DialogCreateVmComponent, {
      data: {
        countVcpus: (this._vmConfiguration.maxVcpusPerVm / 3).toFixed(),
        countRam: (this._vmConfiguration.maxRamPerVm / 3).toFixed(),
        countDisk: (this._vmConfiguration.maxDiskPerVm / 3).toFixed(),
        owner: false,
        maxVcpus: this._vmConfiguration.maxVcpusPerVm,
        maxRam: this._vmConfiguration.maxRamPerVm,
        maxDisk: this._vmConfiguration.maxDiskPerVm
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
        maxVcpus: this._vmConfiguration.maxVcpusPerVm,
        maxRam: this._vmConfiguration.maxRamPerVm,
        maxDisk: this._vmConfiguration.maxDiskPerVm,
        id: vm.id,
        mockId: this._vmInstances.indexOf(vm)
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
        fakeId: this._vmInstances.indexOf(vm) + 1,
        course: this.course,
        image: "http://localhost:8080/VM_images/" + this.courseAc + "/" + vm.image, //TODO: link
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

}
