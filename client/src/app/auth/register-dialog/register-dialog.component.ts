import { Component, OnInit } from "@angular/core";
import { MatDialogRef } from "@angular/material/dialog";
import {
  FormControl,
  Validators,
  FormGroup,
  FormBuilder,
  ValidatorFn,
  AbstractControl,
} from "@angular/forms";
import { AuthService } from "../auth.service";
import { Subscription } from "rxjs";

@Component({
  selector: "app-register-dialog",
  templateUrl: "./register-dialog.component.html",
  styleUrls: ["./register-dialog.component.sass"],
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
    this.registerForm = this.fb.group(
      {
        email: [
          "",
          [Validators.required, Validators.email, isStudentOrTeacher()],
        ],
        password: ["", [Validators.required, Validators.minLength(3)]],
        confirmPassword: ["", [Validators.required]],
        firstName: ["", [Validators.required, Validators.minLength(1)]],
        lastName: ["", [Validators.required, Validators.minLength(1)]],
      },
      { validator: this.mustMatch("password", "confirmPassword") }
    );
  }

  ngOnInit() {}

  mustMatch(controlName: string, matchingControlName: string) {
    return (formGroup: FormGroup) => {
      const control = formGroup.controls[controlName];
      const matchingControl = formGroup.controls[matchingControlName];

      if (matchingControl.errors && !matchingControl.errors.mustMatch) {
        // return if another validator has already found an error on the matchingControl
        return;
      }

      // set error on matchingControl if validation fails
      if (control.value !== matchingControl.value) {
        matchingControl.setErrors({ mustMatch: true });
      } else {
        matchingControl.setErrors(null);
      }
    };
  }

  attemptRegister() {
    if (this.authSubscription) this.authSubscription.unsubscribe();
    this.isLoading = true;
    this.showError = false;
    if (this.registerForm.valid)
      this.authSubscription = this.authService
        .register(
          this.registerForm.get("email").value,
          this.registerForm.get("password").value,
          this.registerForm.get("confirmPassword").value,
          this.registerForm.get("firstName").value,
          this.registerForm.get("lastName").value
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
    (control.value?.toLowerCase().endsWith("@studenti.polito.it") &&
      control.value?.toLowerCase().startsWith("s")) ||
    (control.value?.toLowerCase().endsWith("@polito.it") &&
      control.value?.toLowerCase().startsWith("d"))
      ? //&& Number(control.value?.toLowerCase().split("@")[0].substr(1)) != NaN && !isNaN(Number('as'))
        null
      : { wrongDomain: control.value };
}
