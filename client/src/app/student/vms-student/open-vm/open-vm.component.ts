import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DialogCreateVmComponent } from '../dialog-create-vm/dialog-create-vm.component';


export interface DialogData {
  countVcpus: number;
  countRam: number;
  countDisk: number;
  fakeId: number;
  image: String;
  course: String;
  courseAc: String;
}

@Component({
  selector: 'app-open-vm',
  templateUrl: './open-vm.component.html',
  styleUrls: ['./open-vm.component.sass']
})
export class OpenVmComponent implements OnInit {

  imageLink: String = "http://localhost:8080/VM_images/" + this.data.image;

  path: String;

  constructor(
    public dialogRef: MatDialogRef<DialogCreateVmComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData) {
      dialogRef.disableClose = true;
  }

  ngOnInit(): void {
  }

  closeVm(): void {
    this.dialogRef.close();
  }

}
