import { Component, OnInit } from "@angular/core";
import { MatDialogRef } from "@angular/material/dialog";
import { MatDialog, MAT_DIALOG_DATA } from "@angular/material/dialog";
import {
  FormControl,
  Validators,
  FormGroup,
  FormBuilder,
} from "@angular/forms";
import { AuthService } from "../auth.service";
import { Subscription } from "rxjs";

@Component({
  selector: "app-login-dialog",
  templateUrl: "./login-dialog.component.html",
  styleUrls: ["./login-dialog.component.sass"],
})
export class LoginDialogComponent implements OnInit {
  loginForm: FormGroup;
  isLoading = false;
  showError = false;
  authSubscription: Subscription;
  constructor(
    public dialogRef: MatDialogRef<LoginDialogComponent>,
    public fb: FormBuilder,
    private authService: AuthService
  ) {
    this.loginForm = this.fb.group({
      email: ["", [Validators.required, Validators.email]],
      password: ["", [Validators.required, Validators.minLength(3)]],
    });
  }

  ngOnInit() {}
  attempLogin() {
    if (this.authSubscription) this.authSubscription.unsubscribe();
    this.isLoading = true;
    this.showError = false;
    if (this.loginForm.valid)
      this.authSubscription = this.authService
        .login(
          this.loginForm.get("email").value,
          this.loginForm.get("password").value
        )
        .subscribe((evt) => {
          this.isLoading = false;
          if (evt == null) {
            //login failed
            this.showError = true;
          } else {
            this.dialogRef.close(true);
          }
        });
  }
  cancel() {
    this.loginForm.reset();
    this.dialogRef.close(false);
  }
  ngOnDestroy() {
    if (this.authSubscription) this.authSubscription.unsubscribe();
  }
}
