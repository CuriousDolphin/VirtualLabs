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
import * as _ from 'lodash';
import { environment } from 'src/environments/environment';
import { ToastService } from './toast.service';
const BASE_PATH = environment.apiUrl;

@Injectable({
  providedIn: 'root',
})
export class StudentService {
  constructor(private http: HttpClient, private toastService: ToastService) { }

  getEnrolledStudents(courseName: string) {
    const url = BASE_PATH + 'courses/' + courseName + '/enrolled';
    return this.http.get<Student[]>(url).pipe(catchError((e) => this.handleError(e)));
  }
  getAllStudents() {
    const url = BASE_PATH + 'students/';
    return this.http.get<Student[]>(url).pipe(catchError((e) => this.handleError(e)));
  }

  /* Multiple  PATCH */
  // if course id= 0 unenrolled
  updateEnrolled(students: Student[], courseName: string) {
    const urls = [];
    const url = BASE_PATH + '/students/';
    students.forEach((student) => {
      urls.push(url + student.id);
    });

    return combineLatest(
      students.map((student) => {
        return this.http.patch(url + student.id, student);
      })
    ).pipe(
      tap((evt) => console.log('update enrolled')),
      catchError((e) => this.handleError(e))
    );
  }

  private handleError(error) {
    let errorMessage = '';
    if (error.error instanceof ErrorEvent) {
      // client-side error
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // server-side error
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
    }
    // this.toastService.error(err.message, err.status.toString())

    // window.alert(errorMessage);


    this.toastService.error(error.error.message, error.error.status.toString())
    console.log('HTTP ERROR', error);
    return of(null);
  }
}
