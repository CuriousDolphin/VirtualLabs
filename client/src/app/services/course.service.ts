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
import { Course } from '../models/course.model';
const BASE_PATH = environment.apiUrl;

@Injectable({
  providedIn: 'root'
})
export class CourseService {

  constructor(private http: HttpClient) { }


  getAllCourses():Observable<Course[]> {
    const url = BASE_PATH + 'courses';
    return this.http.get<Course[]>(url).pipe(catchError(this.handleError));
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
    // window.alert(errorMessage);
    console.log('HTTP ERROR', errorMessage);
    return of(null);
  }
}
