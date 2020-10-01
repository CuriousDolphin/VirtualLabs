import { Injectable } from '@angular/core';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class ToastService {

  baseConfig: MatSnackBarConfig = {
    duration: 2000,
    // verticalPosition: 'top',
    // horizontalPosition: 'right'
  };


  constructor(private snackBar: MatSnackBar) { }

  private message(level: string, message: string, action?: string) {


    this.snackBar.open(message, action, this.baseConfig);
  }

  success(message: string, action?: string) {
    this.message('success', message, action);
  }

  info(message: string, action?: string) {
    this.message('info', message, action);
  }

  warning(message: string, action?: string) {
    this.message('warning', message, action);
  }

  error(message: string, action?: string) {
    this.message('error', message, action);
  }
}
