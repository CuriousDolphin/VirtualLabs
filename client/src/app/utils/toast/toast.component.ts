import { Component, Inject, OnInit } from '@angular/core';
import { MAT_SNACK_BAR_DATA, MatSnackBarRef } from '@angular/material/snack-bar';

@Component({
  selector: 'toast',
  templateUrl: './toast.component.html',
  styleUrls: ['./toast.component.sass']
})
export class ToastComponent implements OnInit {

  constructor(@Inject(MAT_SNACK_BAR_DATA) public data, private snackBarRef: MatSnackBarRef<ToastComponent>) { }

  ngOnInit() {
  }

  dismiss() {
    this.snackBarRef.dismissWithAction();
  }
}
