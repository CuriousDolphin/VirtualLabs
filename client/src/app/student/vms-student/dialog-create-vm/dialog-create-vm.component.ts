import { Component, OnInit, Inject, Output, EventEmitter } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { VmInstance } from 'src/app/models/vm-instance.model';

export interface DialogData {
  countVcpus: number;
  countRam: number;
  countDisk: number;
  owner: boolean;
  maxVcpus: number;
  maxRam: number;
  maxDisk: number;
}

@Component({
  selector: 'app-dialog-create-vm',
  templateUrl: './dialog-create-vm.component.html',
  styleUrls: ['./dialog-create-vm.component.sass']
})
export class DialogCreateVmComponent implements OnInit {

  ngOnInit(): void { }

  constructor(
    fb: FormBuilder,
    public dialogRef: MatDialogRef<DialogCreateVmComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData) { }

  onNoClick(): void {
    this.dialogRef.close();
  }

  confirmCreateVm(data: DialogData) {
    if (data.countVcpus <= data.maxVcpus &&
      data.countRam <= data.maxRam &&
      data.countDisk <= data.maxDisk &&
      data.countVcpus >= 1 &&
      data.countRam >= 1 &&
      data.countDisk >= 1) {
      this.dialogRef.close(<JSON><unknown>{
        "countVcpus": data.countVcpus,
        "countRam": data.countRam,
        "countDisks": data.countDisk,
        "owner": String(data.owner)
      });
    }
    //TODO: else mostrare errore
  }

}
