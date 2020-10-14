import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import {
  FormControl,
  Validators,
  FormGroup,
  FormBuilder,
} from '@angular/forms';
import { AuthService } from '../auth.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-register-dialog',
  templateUrl: './register-dialog.component.html',
  styleUrls: ['./register-dialog.component.sass']
})
export class RegisterDialogComponent implements OnInit {

  registerForm: FormGroup;
  isLoading = false;
  showError = false;
  authSubscription: Subscription;

  constructor(
    public dialogRef: MatDialogRef<RegisterDialogComponent>,
    public fb: FormBuilder,
    private authService: AuthService
  ) {
    this.registerForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(3)]],
      firstName: ['', [Validators.required, Validators.minLength(1)]],
      lastName: ['', [Validators.required, Validators.minLength(1)]],
    });
  }

  ngOnInit() { }

  attemptRegister() {
    if (this.authSubscription) this.authSubscription.unsubscribe();
    this.isLoading = true;
    this.showError = false;
    if (this.registerForm.valid)
      this.authSubscription = this.authService
        .register(
          this.registerForm.get('email').value,
          this.registerForm.get('password').value,
          this.registerForm.get('firstName').value,
          this.registerForm.get('lastName').value
        )
        .subscribe((evt) => {
          this.isLoading = false;
          if (evt == null) {
            // login failed
            this.showError = true;
          } else {
            this.dialogRef.close(true);
          }
        });
  }

  cancel() {
    this.registerForm.reset();
    this.dialogRef.close(false);
  }
  ngOnDestroy() {
    if (this.authSubscription) this.authSubscription.unsubscribe();
  }

}
