import { Injectable } from '@angular/core';
import { Student } from '../models/student.model';
import { HttpClient } from '@angular/common/http';
import {
  BehaviorSubject,
  Observable,
  throwError,
  combineLatest,
  of,
} from 'rxjs';
import { catchError, retry, tap } from 'rxjs/operators';
import { User } from '../models/user.model';
import { environment } from '../../environments/environment';

const BASE_PATH = environment.authUrl;
@Injectable({
  providedIn: 'root',
})

export class AuthService {
  private currentUserSubject$: BehaviorSubject<User> = new BehaviorSubject<
    User
  >(null);
  currentUser$ = this.currentUserSubject$.asObservable();
  constructor(private http: HttpClient) {
    if (this.isLogged() === true) {
      const token = this.getToken();
      const user: User = JSON.parse(atob(token.split('.')[1]));
      this.currentUserSubject$.next(user);
    }
  }
  login(username: string, password: string): Observable<any> {
    const body = {
      username,
      password,
    };
    return this.http.post(BASE_PATH + 'signin', body).pipe(
      tap((evt) => {
        if (evt['token'] != null) {
          // login
          const token = evt['token'];
          localStorage.setItem('token', token);

          const user: User = JSON.parse(atob(token.split('.')[1]));
          console.log('LOGGED ', user);
          this.currentUserSubject$.next(user);
        }
      }),
      catchError((e) => {
        console.log('ERRORE LOGIN');
        return of(null);
      })
    );
  }
  isLogged(): boolean {
    const token = this.getToken();
    if (token == null) {
      return false;
    } else {
      const now: number = Date.now() / 1000;
      const user: User = JSON.parse(atob(token.split('.')[1]));
      if (user.exp < now) {
        console.log('TOKEN SCADUTO', user.exp, now);
        // scaduto
        this.logout();

        return false;
      }

      return true;
    }
  }
  public getToken(): string {
    return localStorage.getItem('token');
  }

  logout() {
    this.currentUserSubject$.next(null);
    localStorage.removeItem('token');
  }
}
