import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

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

  createForm: FormGroup;

  ngOnInit(): void { }

  constructor(
    public fb: FormBuilder,
    public dialogRef: MatDialogRef<DialogCreateVmComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData) {
    this.createForm = this.fb.group({
      vcpus: [data.countVcpus, [Validators.min(1), Validators.max(this.data.maxVcpus)]],
      ram: [data.countRam, [Validators.min(1), Validators.max(this.data.maxRam)]],
      disk: [data.countDisk, [Validators.min(1), Validators.max(this.data.maxDisk)]],
      owner: [data.owner]
    });
  }

  onNoClick(): void {
    this.dialogRef.close();
  }


  confirmCreateVm(data: DialogData) {
    if (this.createForm.get("vcpus").value <= data.maxVcpus &&
      this.createForm.get("ram").value <= data.maxRam &&
      this.createForm.get("disk").value <= data.maxDisk &&
      this.createForm.get("vcpus").value >= 1 &&
      this.createForm.get("ram").value >= 1 &&
      this.createForm.get("disk").value >= 1)
      this.dialogRef.close(<JSON><unknown>{
        "countVcpus": this.createForm.get("vcpus").value,
        "countRam": this.createForm.get("ram").value,
        "countDisks": this.createForm.get("disk").value,
        "owner": String(this.createForm.get("owner").value)
      });
  }

}
