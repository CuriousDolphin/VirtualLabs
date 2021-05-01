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
export class HomeGuard implements CanActivate {
  constructor(public auth: AuthService, public router: Router) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    if (this.auth.isLogged())
      if (this.auth.hasRoleTeacher() == true) {
        this.router.navigate(["teacher"], {});
      } else {
        this.router.navigate(["student"], {});
      }

    return true;
  }
}
