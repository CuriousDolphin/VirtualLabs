import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

export interface DialogData {
  countVcpus: number;
  countRam: number;
  countDisk: number;
  maxVcpus: number;
  maxRam: number;
  maxDisk: number;
  id: number;
}

@Component({
  selector: 'app-dialog-edit-vm',
  templateUrl: './dialog-edit-vm.component.html',
  styleUrls: ['./dialog-edit-vm.component.sass']
})
export class DialogEditVmComponent implements OnInit {

  ngOnInit(): void { }

  constructor(fb: FormBuilder,
    public dialogRef: MatDialogRef<DialogEditVmComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData) { }

  onNoClick(): void {
    this.dialogRef.close();
  }

  confirmEditVm(data: DialogData) {
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
        "id": data.id
      });
    }
    //TODO: else mostrare errore
  }

}
