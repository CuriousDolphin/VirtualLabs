import { Component, OnInit, ChangeDetectorRef } from "@angular/core";
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
  imageSrc: ArrayBuffer;


  constructor(
    public dialogRef: MatDialogRef<RegisterDialogComponent>,
    public fb: FormBuilder,
    private authService: AuthService,
    private changeDetector: ChangeDetectorRef,
  ) {
    this.registerForm = this.fb.group({
      email: ['', [Validators.required, Validators.email, Validators.pattern(new RegExp('^s\\d+@studenti.polito.it$|^d\\d+@polito.it$'))]],// [Validators.required, Validators.email,isStudentOrTeacher()]],
      password: ['', [Validators.required, Validators.minLength(3)]],
      confirmPassword: ['',[Validators.required, Validators.minLength(3) ]],
      firstName: ['', [Validators.required,removeSpaces]],
      lastName: ['', [Validators.required,removeSpaces]],
      photo: ['',[]],
    },{validator: this.mustMatch("password", "confirmPassword") });

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
          this.registerForm.get("lastName").value,
          this.imageSrc != null ? this.imageSrc.toString() : ""
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

  onFileChange(event) {

    let reader = new FileReader();

    if (event.target.files && event.target.files.length) {
      const [file] = event.target.files
      reader.readAsDataURL(file)
      reader.onload = () => {
        this.imageSrc = reader.result as ArrayBuffer;
        
        //this.registerForm.patchValue({
        //  solutionFileSource: reader.result
        //})
        this.changeDetector.markForCheck()
      }
    }
  }
}



//TODO validatore matricola (s o d succeduta da solo numeri)
export function removeSpaces(control: AbstractControl) {
  if (control && control.value && !control.value.trim().length) {
    control.setValue('');
  }
  return null;

}
