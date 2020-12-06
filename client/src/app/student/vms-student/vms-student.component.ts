import { Component, Input, OnInit, Output, EventEmitter, Inject } from '@angular/core';
import { Team } from 'src/app/models/team.model';
import { VmInstance } from 'src/app/models/vm-instance.model';
import * as _ from "lodash";
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DialogCreateVmComponent } from './dialog-create-vm/dialog-create-vm.component';
import { VmConfiguration } from 'src/app/models/vm-configuration.model';
import { DialogEditVmComponent } from './dialog-edit-vm/dialog-edit-vm.component';

@Component({
  selector: 'app-vms-student',
  templateUrl: './vms-student.component.html',
  styleUrls: ['./vms-student.component.sass']
})
export class VmsStudentComponent implements OnInit {

  @Output() deleteVm = new EventEmitter<VmInstance>();
  @Output() startVm = new EventEmitter<VmInstance>();
  @Output() stopVm = new EventEmitter<VmInstance>();
  @Output() createVm = new EventEmitter<JSON>();
  @Output() editVm = new EventEmitter<JSON>();

  @Input() vmInstances: VmInstance[];
  @Input() studentId: String;
  @Input() vmConfiguration: VmConfiguration;
  dialogCreateRef: MatDialogRef<DialogCreateVmComponent, any>
  dialogEditRef: MatDialogRef<DialogEditVmComponent, any>
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

  constructor(public dialog: MatDialog) { }

  ngOnInit(): void { }

  createVmDialog(): void {
    this.dialogCreateRef = this.dialog.open(DialogCreateVmComponent, {
      data: {
        countVcpus: (this.vmConfiguration.maxVcpusPerVm / 4),
        countRam: (this.vmConfiguration.maxRamPerVm / 4),
        countDisk: (this.vmConfiguration.maxDiskPerVm / 4),
        owner: true,
        maxVcpus: this.vmConfiguration.maxVcpusPerVm,
        maxRam: this.vmConfiguration.maxRamPerVm,
        maxDisk: this.vmConfiguration.maxDiskPerVm
      }
    });
    this.dialogCreateRef.afterClosed().subscribe((newVm) => { this.emitCreateVm(newVm) });
  }

  editVmDialog(vm: VmInstance): void {
    this.dialogEditRef = this.dialog.open(DialogEditVmComponent, {
      data: {
        countVcpus: vm.countVcpus,
        countRam: vm.countRam,
        countDisk: vm.countDisks,
        owner: vm.owner === null ? true : false,
        maxVcpus: this.vmConfiguration.maxVcpusPerVm,
        maxRam: this.vmConfiguration.maxRamPerVm,
        maxDisk: this.vmConfiguration.maxDiskPerVm,
        id: vm.id
      }
    });
    this.dialogEditRef.afterClosed().subscribe((newVm) => {
      if (newVm !== undefined)
        if (newVm["countVcpus"] !== vm.countVcpus ||
          newVm["countRam"] !== vm.countRam ||
          newVm["countDisks"] !== vm.countDisks)
          this.emitEditVm(newVm)
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
