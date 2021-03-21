import { ComponentFactoryResolver, Injectable } from "@angular/core";
import { Student } from "../models/student.model";
import { HttpClient } from "@angular/common/http";
import {
  Observable,
  of,
} from "rxjs";
import { catchError, map, retry, tap } from "rxjs/operators";
import * as _ from "lodash";
import { environment } from "src/environments/environment";
import { ToastService } from "./toast.service";
import { Course } from "../models/course.model";
import { Team } from "../models/team.model";
import { VmInstance } from "../models/vm-instance.model";
import { VmModel } from '../models/vm-model.model';
import { Assignment } from "../models/assignment.model";
import { Paper } from "../models/paper.model";
import { StudentAssignment } from "../models/studentAssignment.model";

const BASE_PATH = environment.apiUrl;
const IMG_PATH = '/assets/VM_images/';

@Injectable({
  providedIn: "root",
})
export class StudentService {
  constructor(private http: HttpClient, private toastService: ToastService) { }

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

  getAllAssignmentsForCourseAndForStudent(courseName: string, studentId: string): Observable<StudentAssignment[]> {
    const url = BASE_PATH + "students/" + studentId + "/course/" + courseName + "/assignments";
    return this.http
      .get<StudentAssignment[]>(url)
      .pipe(catchError((e) => this.handleError(e)));
  }

  getAllPapersForCourseAndForStudent(courseName: string, studentId: string): Observable<Paper[]> {
    const url = BASE_PATH + "students/" + studentId + "/course" + courseName + "/papers"
    return this.http
      .get<Paper[]>(url)
      .pipe(catchError((e) => this.handleError(e)))
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
      .pipe(
        map((vms: VmInstance[]) => { return _.map(vms, (vm: VmInstance) => { vm.image = IMG_PATH + vm.image; return vm }) })
      )
      .pipe(catchError((e) => this.handleError(e)));
  }

  getCourseVmModel(studentId: String, teamName: String): Observable<VmModel> {
    const url = BASE_PATH + "students/" + studentId + "/" + teamName + "/vmmodel";
    return this.http
      .get<VmModel>(url)
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

  createVm(studentId: String, teamName: String, newVm: JSON): Observable<VmInstance[]> {
    const url = BASE_PATH + "students/" + studentId + "/" + teamName + "/createvminstance";
    var jsonToString: String = JSON.stringify(newVm);
    jsonToString = jsonToString.replace(',"owner":"true"', '');
    jsonToString = jsonToString.replace('false', <string>studentId);
    newVm = JSON.parse(<string>jsonToString)
    return this.http
      .post<VmInstance[]>(url, newVm)
      .pipe(catchError((e) => this.handleError(e)));
  }

  editVm(studentId: String, teamName: String, vmId: number, newVm: JSON): Observable<VmInstance[]> {
    const url = BASE_PATH + "students/" + studentId + "/" + teamName + "/editvminstance/" + vmId;
    var jsonToString: String = JSON.stringify(newVm);
    jsonToString = jsonToString.replace(',"owner":"true"', '');
    jsonToString = jsonToString.replace('false', <string>studentId);
    newVm = JSON.parse(<string>jsonToString)
    return this.http
      .post<VmInstance[]>(url, newVm)
      .pipe(catchError((e) => this.handleError(e)));
  }

  updatePaperStatus(studentId: String, assignmentId: number, status: String) {
    const url = BASE_PATH + "students/" + studentId + "/assignment/" + assignmentId + "/updatePaperStatus"
    return this.http
      .put<Paper>(url, status)
      .pipe(catchError((e) => this.handleError(e)))
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
