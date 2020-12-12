import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import {
  FormControl,
  Validators,
  FormGroup,
  FormBuilder,
  ValidatorFn,
  AbstractControl
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
  regex = new RegExp('^s\d+@studenti.polito.it|^d\d+@polito.it');

  constructor(
    public dialogRef: MatDialogRef<RegisterDialogComponent>,
    public fb: FormBuilder,
    private authService: AuthService
  ) {
    this.registerForm = this.fb.group({
      email: ['', [Validators.required, Validators.email, Validators.pattern(new RegExp('^s\\d+@studenti.polito.it$|^d\\d+@polito.it$'))]],// [Validators.required, Validators.email,isStudentOrTeacher()]],
      password: ['', [Validators.required, Validators.minLength(3)]],
      confirmPassword: ['',[Validators.required, Validators.minLength(3) ]],
      firstName: ['', [Validators.required,removeSpaces]],
      lastName: ['', [Validators.required,removeSpaces]],
    },{validator: this.passwordConfirming});
  }

  ngOnInit() { }

  passwordConfirming(c: AbstractControl): { invalid: boolean } {
    if (c.get('password').value !== c.get('confirmPassword').value) {
        return {invalid: true};
    }
  }

  attemptRegister() {
    if (this.authSubscription) this.authSubscription.unsubscribe();
    this.isLoading = true;
    this.showError = false;
    if (this.registerForm.valid)
      this.authSubscription = this.authService
        .register(
          this.registerForm.get('email').value,
          this.registerForm.get('password').value,
          this.registerForm.get('confirmPassword').value,
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

//TODO validatore matricosa (s o d succeduta da solo numeri)
export function isStudentOrTeacher(): ValidatorFn {  
  return (control: AbstractControl): { [key: string]: any } | null =>  
      //((studente and start with s) or (docente and start with d)) and (check solo numeri dopo lettera)
      //( (control.value?.toLowerCase().endsWith('@studenti.polito.it') && control.value?.toLowerCase().startsWith("s") ) 
      //|| (control.value?.toLowerCase().endsWith('@polito.it') && control.value?.toLowerCase().startsWith("d")) 
      //)
      new RegExp('^s\d+@studenti.polito.it|^d\d+@polito.it').test(control.value?.toLowerCase()) ? null : {wrongDomain: control.value};
}

export function removeSpaces(control: AbstractControl) {
  if (control && control.value && !control.value.trim().length) {
    control.setValue('');
  }
  return null;
}






