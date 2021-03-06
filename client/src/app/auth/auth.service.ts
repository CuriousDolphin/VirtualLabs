import { Injectable } from "@angular/core";
import { Student } from "../models/student.model";
import { HttpClient } from "@angular/common/http";
import {
  BehaviorSubject,
  Observable,
  throwError,
  combineLatest,
  of,
} from "rxjs";
import { catchError, retry, tap } from "rxjs/operators";
import { User } from "../models/user.model";
import { environment } from "../../environments/environment";
import * as _ from "lodash";
import { ToastService } from "../services/toast.service";

const BASE_PATH = environment.apiUrl + "auth/";
@Injectable({
  providedIn: "root",
})
export class AuthService {
  private currentUserSubject$: BehaviorSubject<User> = new BehaviorSubject<User>(
    null
  );
  currentUser$ = this.currentUserSubject$.asObservable();
  constructor(private http: HttpClient, private toastService: ToastService) {
    if (this.isLogged() === true) {
      const token = this.getToken();
      const user: User = JSON.parse(atob(token.split(".")[1]));
      this.currentUserSubject$.next(user);
    }
  }
  login(username: string, password: string): Observable<any> {
    const body = {
      username,
      password,
    };
    return this.http.post(BASE_PATH + "login", body).pipe(
      tap((evt) => {
        if (_.get(evt, "token", null) != null) {
          // login
          const token = _.get(evt, "token");
          localStorage.setItem("token", token);

          const user: User = JSON.parse(atob(token.split(".")[1]));
          this.currentUserSubject$.next(user);
        }
      }),
      catchError((e) => {
        this.toastService.error("Login failed");
        return of(null);
      })
    );
  }
  getUserId(): string {
    return this.currentUserSubject$.value.userId;
  }

  hasRoleTeacher(): boolean {
    const token = this.getToken();
    if (token == null) {
      return false;
    } else {
      const user: User = JSON.parse(atob(token.split(".")[1]));
      let found = false;
      user.roles.forEach((role) => {
        if (role === "ROLE_PROF") {
          found = true;
        }
      });
      return found;
    }
  }
  hasRoleStudent(): boolean {
    const token = this.getToken();
    if (token == null) {
      return false;
    } else {
      const user: User = JSON.parse(atob(token.split(".")[1]));
      let found = false;
      user.roles.forEach((role) => {
        if (role === "ROLE_STUDENT") {
          found = true;
        }
      });
      return found;
    }
  }

  isLogged(): boolean {
    const token = this.getToken();
    if (token == null) {
      return false;
    } else {
      const now: number = Date.now() / 1000;
      const user: User = JSON.parse(atob(token.split(".")[1]));
      if (user.exp < now) {
        this.toastService.warning("Please login again", "Token expired");
        // scaduto
        this.logout();

        return false;
      }

      return true;
    }
  }
  public getToken(): string {
    return localStorage.getItem("token");
  }

  logout() {
    this.currentUserSubject$.next(null);
    localStorage.removeItem("token");
    // this.toastService.success('user logged out')
  }

  register(username: string, password: string, confirmPassword: string, name: string, lastName: string, photo: string): Observable<any> {
    const body = {
      username,
      password,
      confirmPassword,
      name,
      lastName,
      photo
    };
    return this.http.post(BASE_PATH + 'signin', body).pipe(
      catchError((e) => {
        return of(null);
      })
    );
  }
}
