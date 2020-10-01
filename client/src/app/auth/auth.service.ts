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
const BASE_PATH = "http://localhost:3000/";
@Injectable({
  providedIn: "root",
})
export class AuthService {
  private currentUserSubject$: BehaviorSubject<User> = new BehaviorSubject<
    User
  >(null);
  currentUser$ = this.currentUserSubject$.asObservable();
  constructor(private http: HttpClient) {
    console.log("AUTH SERVICE CONSTRUCTOR");
    if (this.isLogged() == true) {
      const accessToken = this.getToken();
      const user: User = JSON.parse(atob(accessToken.split(".")[1]));
      this.currentUserSubject$.next(user);
    }
  }
  login(username: string, password: string): Observable<any> {
    const body = {
      email: username,
      password: password,
    };
    return this.http.post(BASE_PATH + "login", body).pipe(
      tap((evt) => {
        if (evt["accessToken"] != null) {
          //logiin
          let accessToken = evt["accessToken"];
          localStorage.setItem("accessToken", accessToken);

          let user: User = JSON.parse(atob(accessToken.split(".")[1]));
          console.log("LOGGED ", user);
          this.currentUserSubject$.next(user);
        }
      }),
      catchError((e) => {
        console.log("ERRORE LOGIN");
        return of(null);
      })
    );
  }
  isLogged(): boolean {
    const accessToken = this.getToken();
    if (accessToken == null) {
      return false;
    } else {
      const now: number = Date.now() / 1000;
      const user: User = JSON.parse(atob(accessToken.split(".")[1]));
      if (user.exp < now) {
        console.log("TOKEN SCADUTO", user.exp, now);
        // scaduto
        this.logout();

        return false;
      }

      return true;
    }
  }
  public getToken(): string {
    return localStorage.getItem("accessToken");
  }

  logout() {
    this.currentUserSubject$.next(null);
    localStorage.removeItem("accessToken");
  }
}
