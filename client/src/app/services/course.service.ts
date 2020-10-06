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
  providedIn: 'root',
})
export class CourseService {
  constructor(private http: HttpClient) { }

  // TODO MODIFY THIS TO GETALLCOURSE BY TEACHER
  getAllCourses(): Observable<Course[]> {
    const url = BASE_PATH + 'courses';
    return this.http.get<Course[]>(url).pipe(catchError(this.handleError));
  }
  getCourse(name: string): Observable<Course> {
    const url = BASE_PATH + 'courses/' + name;
    return this.http.get<Course>(url).pipe(catchError(this.handleError));
  }

  updateCourse(course: Course, name: string): Observable<Course> {
    const url = BASE_PATH + 'courses/' + name;
    return this.http.patch<Course>(url, course).pipe(catchError(this.handleError));
  }

  addCourse(course: Course) {
    const url = BASE_PATH + 'courses/';
    return this.http.post<Course>(url, course).pipe(catchError(this.handleError));

  }
  enrollOne(course: Course, student: Student): Observable<void> {
    const url = BASE_PATH + 'courses/' + course.name + '/enrollOne';
    return this.http
      .post<void>(url, _.omit(student, 'links'))
      .pipe(catchError(this.handleError));
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
