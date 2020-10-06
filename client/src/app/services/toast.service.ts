import { Injectable } from '@angular/core';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';
import { ToastComponent } from '../utils/toast/toast.component'

@Injectable({
  providedIn: 'root'
})
export class ToastService {

  baseConfig: MatSnackBarConfig = {
    duration: 4000,
    verticalPosition: 'bottom',
    horizontalPosition: 'center'
  };


  constructor(private snackBar: MatSnackBar) { }

  private message(level: string, message: string, title?: string) {
    let toastBackground = '';
    switch (level) {
      case 'success':
        toastBackground = 'success-toast';
        break;
      case 'error':
        toastBackground = 'error-toast';
        break;
      case 'info':
        toastBackground = 'info-toast';
        break;
      case 'warning':
        toastBackground = 'warning-toast';
        break;
      default:
        break;
    }
    const data = {
      data: {
        level: level,
        message: message,
        title: title
      }
    };
    const config: MatSnackBarConfig = {
      panelClass: [toastBackground]
    };
    Object.assign(config, this.baseConfig, data);

    if (['warning', 'error'].includes(level)) {
      config.horizontalPosition = 'center';
      config.data.dismissable = true;
    }

    this.snackBar.openFromComponent(ToastComponent, config);
  }

  success(message: string, title?: string) {
    this.message('success', message, title);
  }

  info(message: string, title?: string) {
    this.message('info', message, title);
  }

  warning(message: string, title?: string) {
    this.message('warning', message, title);
  }

  error(message: string, title?: string) {
    this.message('error', message, title);
  }
}
