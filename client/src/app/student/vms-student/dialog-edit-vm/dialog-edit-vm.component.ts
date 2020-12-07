import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

export interface DialogData {
  countVcpus: number;
  countRam: number;
  countDisk: number;
  maxVcpus: number;
  maxRam: number;
  maxDisk: number;
  id: number;
  fakeId: number;
}

@Component({
  selector: 'app-dialog-edit-vm',
  templateUrl: './dialog-edit-vm.component.html',
  styleUrls: ['./dialog-edit-vm.component.sass']
})
export class DialogEditVmComponent implements OnInit {

  editForm: FormGroup;

  ngOnInit(): void { }

  constructor(
    public fb: FormBuilder,
    public dialogRef: MatDialogRef<DialogEditVmComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData) {
    this.editForm = this.fb.group({
      vcpus: [data.countVcpus, [Validators.min(1), Validators.max(this.data.maxVcpus)]],
      ram: [data.countRam, [Validators.min(1), Validators.max(this.data.maxRam)]],
      disk: [data.countDisk, [Validators.min(1), Validators.max(this.data.maxDisk)]],
    });
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  confirmEditVm(data: DialogData) {
    if (this.editForm.get("vcpus").value <= data.maxVcpus &&
      this.editForm.get("ram").value <= data.maxRam &&
      this.editForm.get("disk").value <= data.maxDisk &&
      this.editForm.get("vcpus").value >= 1 &&
      this.editForm.get("ram").value >= 1 &&
      this.editForm.get("disk").value >= 1)
      this.dialogRef.close(<JSON><unknown>{
        "countVcpus": this.editForm.get("vcpus").value,
        "countRam": this.editForm.get("ram").value,
        "countDisks": this.editForm.get("disk").value,
        "id": data.id
      });
  }

}
