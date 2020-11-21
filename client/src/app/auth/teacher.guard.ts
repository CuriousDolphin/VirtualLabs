import { Injectable } from "@angular/core";
import {
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  UrlTree,
  Router,
} from "@angular/router";
import { AuthService } from "./auth.service";

@Injectable({
  providedIn: "root",
})
export class TeacherGuard implements CanActivate {
  constructor(public auth: AuthService, public router: Router) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    // not logged
    if (!this.auth.isLogged) {
      this.router.navigate(["home"], {
        queryParams: { doLogin: true },
        state: { nextlink: state.url },
      });
    }

    if (this.auth.hasRoleTeacher() == true) return true;

    // not authorized
    this.router.navigate(["oh-no-page"], {});

    return false;
  }
}
