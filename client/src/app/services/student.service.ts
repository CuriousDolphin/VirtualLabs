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
import { environment } from "src/environments/environment";
import { ToastService } from "./toast.service";
import { Course } from "../models/course.model";
import { Team } from "../models/team.model";
import { VmInstance } from "../models/vm-instance.model";

const BASE_PATH = environment.apiUrl;

@Injectable({
  providedIn: "root",
})
export class StudentService {
  constructor(private http: HttpClient, private toastService: ToastService) {}

  getEnrolledStudents(courseName: string): Observable<Student[]> {
    const url = BASE_PATH + "courses/" + courseName + "/enrolled";
    return this.http
      .get<Student[]>(url)
      .pipe(catchError((e) => this.handleError(e)));
  }
  getAllStudents(): Observable<Student[]> {
    const url = BASE_PATH + "students/";
    return this.http
      .get<Student[]>(url)
      .pipe(catchError((e) => this.handleError(e)));
  }
  getCoursesByStudentId(studentId: string): Observable<Course[]> {
    const url = BASE_PATH + "students/" + studentId + "/courses";
    return this.http
      .get<Course[]>(url)
      .pipe(catchError((e) => this.handleError(e)));
  }

  getTeamsByStudentIdCourseName(
    studentId: string,
    courseName: string
  ): Observable<Team[]> {
    const url = BASE_PATH + "courses/" + courseName + "/teams/" + studentId;
    return this.http
      .get<Team[]>(url)
      .pipe(catchError((e) => this.handleError(e)));
  }

  getVmInstancesPerTeam(studentId: String, teamName: String): Observable<VmInstance[]> {
    const url = BASE_PATH + "students/" + studentId + "/" + teamName + "/vminstances";
    return this.http
      .get<VmInstance[]>(url)
      .pipe(catchError((e) => this.handleError(e)));
  }

  deleteVm(studentId: String, teamName: String, vm: VmInstance): Observable<VmInstance[]> {
    const url = BASE_PATH + "students/" + studentId + "/" + teamName + "/deletevminstance/" + vm.id;
    return this.http
      .get<VmInstance[]>(url)      
      .pipe(catchError((e) => this.handleError(e)));
  }

  startVm(studentId: String, teamName: String, vm: VmInstance): Observable<VmInstance[]> {
    const url = BASE_PATH + "students/" + studentId + "/" + teamName + "/startvminstance/" + vm.id;
    return this.http
      .get<VmInstance[]>(url)      
      .pipe(catchError((e) => this.handleError(e)));
  }

  stopVm(studentId: String, teamName: String, vm: VmInstance): Observable<VmInstance[]> {
    const url = BASE_PATH + "students/" + studentId + "/" + teamName + "/stopvminstance/" + vm.id;
    return this.http
      .get<VmInstance[]>(url)      
      .pipe(catchError((e) => this.handleError(e)));
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
    // this.toastService.error(err.message, err.status.toString())

    // window.alert(errorMessage);

    this.toastService.error(error.error.message, error.error.status.toString());
    console.log("HTTP ERROR", error);
    return of(null);
  }
}
