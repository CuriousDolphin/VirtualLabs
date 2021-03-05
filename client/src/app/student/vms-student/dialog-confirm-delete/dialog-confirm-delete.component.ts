import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { VmInstance } from 'src/app/models/vm-instance.model';

export interface DialogData {
  vm: VmInstance;
}

@Component({
  selector: 'app-dialog-confirm-delete',
  templateUrl: './dialog-confirm-delete.component.html',
  styleUrls: ['./dialog-confirm-delete.component.sass']
})
export class DialogConfirmDeleteComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<DialogConfirmDeleteComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData
  ) { }

  ngOnInit(): void {
  }

  onNoClick(): void {
    this.dialogRef.close(null);
  }

  confirmDelete() {
    this.dialogRef.close(this.data.vm);
  }
}
