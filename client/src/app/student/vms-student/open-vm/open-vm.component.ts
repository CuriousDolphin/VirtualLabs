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

  imageName: String;

  path: String;

  constructor(
    public dialogRef: MatDialogRef<DialogCreateVmComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData) {
      dialogRef.disableClose = true;
  }

  ngOnInit(): void {
    this.imageName = this.data.image.replace(".png", "");
    this.imageName = this.imageName.replace("http://localhost:8080/VM_images/", "");
    this.imageName = this.imageName.replace("/", ": ");
  }

  closeVm(): void {
    this.dialogRef.close();
  }

}
