
import { Component, OnDestroy, OnInit, ViewChild } from "@angular/core";
import { MatSidenav } from "@angular/material/sidenav";
import { MatDialog } from "@angular/material/dialog";
import { LoginDialogComponent } from "../auth/login-dialog/login-dialog.component";
import { Subscription } from "rxjs";
import { AuthService } from "../auth/auth.service";
import { User } from "../models/user.model";
import { ActivatedRoute, Params, Router } from "@angular/router";
import * as _ from "lodash";
import { ToastService } from "../services/toast.service";
import { UtilsService } from "../services/utils.service";
import { RegisterDialogComponent } from "../auth/register-dialog/register-dialog.component";


@Component({
  selector: "app-home",
  templateUrl: "./home.component.html",
  styleUrls: ["./home.component.sass"],
})
export class HomeComponent implements OnInit {
  userSubscription: Subscription;
  routeSubscription: Subscription;
  dialogSubscription: Subscription;
  isLogged = false;
  user: User = null;

  constructor(
    public dialog: MatDialog,
    private authService: AuthService,
    private toastService: ToastService,
    private route: ActivatedRoute,
    private router: Router,
    private utilsService: UtilsService
  ) {}
  ngOnInit() {
    // if route have param "do login" open login dialog
    this.routeSubscription = this.route.queryParams.subscribe(
      (params: Params) => {
        if (params.doLogin === "true") {
          this.openLoginDialog();
        }
      }
    );
    this.userSubscription = this.authService.currentUser$.subscribe(
      (user: User) => {
        if (user != null) {
          this.isLogged = true;
          this.user = user;

          // REDIRECT
          let nextLink = "";
          if (this.authService.hasRoleTeacher()) {
            nextLink = "teacher";
          } else {
            nextLink = "student";
          }
          //this.toastService.success("redirect to " + nextLink);
          // this.router.navigate([nextLink]);
        } else {
          this.isLogged = false;
          this.user = null;
        }
      }
    );
  }
  ngOnDestroy() {
    this.userSubscription.unsubscribe();
    this.routeSubscription.unsubscribe();
    if (this.dialogSubscription) this.dialogSubscription.unsubscribe();
  }
  toggleForMenuClick() {
    this.utilsService.toggleMenu();
    // this.sidenav.opened = !this.sidenav.opened;
  }
  goToLogin() {
    this.openLoginDialog();
    // this.router.navigate(['home'], { queryParams: { doLogin: true } });
  }
  goToRegister() {
    this.openRegisterDialog();
  }
  private openLoginDialog(redirectTo?: string) {
    if (this.dialogSubscription) this.dialogSubscription.unsubscribe();

    const dialogRef = this.dialog.open(LoginDialogComponent);

    this.dialogSubscription = dialogRef.afterClosed().subscribe((result) => {

      // se non c'e' il campo nextlink nello state default home
      // const nextLink = _.get(history.state, 'nextlink', 'home');
      if (result === true) {
        let nextLink = "";
        if (this.authService.hasRoleTeacher()) {
          nextLink = "teacher";
        } else {
          nextLink = "student";
        }
        this.toastService.success("Login success,redirect to " + nextLink);
        // se il login e' andato bene e devo ridiriggere verso un altra pagina
        this.router.navigate([nextLink]);
      } else {
        this.router.navigate(["home"]);
      }
    });
  }
  logout() {
    this.authService.logout();
    this.router.navigate(["home"]);
  }
  private openRegisterDialog() {
    //Se ha già una subscription faccio un unsubscribe
    if (this.dialogSubscription) this.dialogSubscription.unsubscribe();

    const dialogRef = this.dialog.open(RegisterDialogComponent);

    this.dialogSubscription = dialogRef.afterClosed().subscribe((result) => {
      if (result === true) {
        this.toastService.success('Register with success! Please confirm your email.')
        this.router.navigate(['home']);
      } else {
        //this.router.navigate(['home']);
      }
    });

  }
}
