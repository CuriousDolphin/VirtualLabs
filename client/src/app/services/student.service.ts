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
import * as _ from "lodash";
const BASE_PATH = "http://localhost:3000/";
@Injectable({
  providedIn: "root",
})
export class StudentService {
  constructor(private http: HttpClient) {}

  getEnrolledStudents(courseId: number) {
    const url = BASE_PATH + "course/" + courseId + "/students";
    return this.http.get<Student[]>(url).pipe(catchError(this.handleError));
  }
  getAllStudents() {
    const url = BASE_PATH + "students/";
    return this.http.get<Student[]>(url).pipe(catchError(this.handleError));
  }

  /* Multiple  PATCH */
  /* if course id= 0 unenrolled */
  updateEnrolled(students: Student[], courseId: number) {
    let urls = [];
    const url = BASE_PATH + "students/";
    students.forEach((student) => {
      urls.push(url + student.id);
      student.courseId = courseId;
    });

    return combineLatest(
      students.map((student) => {
        return this.http.patch(url + student.id, student);
      })
    ).pipe(
      tap((evt) => console.log("update enrolled")),
      catchError(this.handleError)
    );
  }

  private handleError(error) {
    let errorMessage = "";
    if (error.error instanceof ErrorEvent) {
      // client-side error
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // server-side error
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
    }
    //window.alert(errorMessage);
    console.log("HTTP ERROR", errorMessage);
    return of(null);
  }
}
