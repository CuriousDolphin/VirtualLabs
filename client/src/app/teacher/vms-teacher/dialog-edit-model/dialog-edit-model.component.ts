import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import * as _ from 'lodash';

export interface DialogData {
  maxVms: number;
  minVms: number;
  maxRunningVms: number;
  minRunningVms: number;
  maxVcpus: number;
  minVcpus: number;
  maxRam: number;
  minRam: number;
  maxDisk: number;
  minDisk: number;
  courseAc: String;
}

@Component({
  selector: 'app-dialog-edit-model',
  templateUrl: './dialog-edit-model.component.html',
  styleUrls: ['./dialog-edit-model.component.sass']
})
export class DialogEditModelComponent implements OnInit {

  editForm: FormGroup;

  ngOnInit(): void { }

  constructor(
    public fb: FormBuilder,
    public dialogRef: MatDialogRef<DialogEditModelComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData) {
    this.editForm = this.fb.group({
      maxVms: [data.maxVms, [Validators.min(data.minVms)]],
      maxRunningVms: [data.maxRunningVms, [Validators.min(data.minRunningVms)]],
      maxVcpus: [data.maxVcpus, [Validators.min(data.minVcpus)]],
      maxRam: [data.maxRam, [Validators.min(data.minRam)]],
      maxDisk: [data.maxDisk, [Validators.min(data.minDisk)]],
    },
      { validator: this.mustMinMaxVms() });
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  mustMinMaxVms() {
    return (formGroup: FormGroup) => {
      if (formGroup.controls['maxRunningVms'].value > formGroup.controls['maxVms'].value || formGroup.controls['maxRunningVms'].value < this.data.minRunningVms)
        formGroup.controls['maxRunningVms'].setErrors({ mustMinMaxVms: true });
      else
        formGroup.controls['maxRunningVms'].setErrors(null);
    }
  }

  confirmEditModel(data: DialogData) {
    this.dialogRef.close(<JSON><unknown>{
      "maxVms": this.editForm.get("maxVms").value,
      "maxRunningVms": this.editForm.get("maxRunningVms").value,
      "maxVcpus": this.editForm.get("maxVcpus").value,
      "maxRam": this.editForm.get("maxRam").value,
      "maxDisk": this.editForm.get("maxDisk").value,
    });
  }

}