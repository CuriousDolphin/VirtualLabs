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
import { catchError, map, retry, tap } from "rxjs/operators";
import * as _ from "lodash";
import { environment } from "src/environments/environment";
import { Course } from "../models/course.model";
import { TeamProposal } from "../models/teamProposal.model";
import { NetErr } from "../models/error.model";
import { ToastService } from "./toast.service";
import { SolutionFormData } from "../models/formPaperSnapshotData.model";
import { PaperSnapshot } from "../models/papersnapshot.model";
import { Paper } from '../models/paper.model';
import { Assignment } from '../models/assignment.model';
import { VmModel } from "../models/vm-model.model";
import { VmInstance } from "../models/vm-instance.model";
import { Team } from "../models/team.model";
const BASE_PATH = environment.apiUrl;
const IMG_PATH = 'assets/VM_images/';

@Injectable({
  providedIn: "root",
})
export class CourseService {
  constructor(private http: HttpClient, private toastService: ToastService) { }

  // TODO MODIFY THIS TO GETALLCOURSE BY TEACHER
  getAllCourses(): Observable<Course[]> {
    const url = BASE_PATH + "courses";
    return this.http
      .get<Course[]>(url)
      .pipe(catchError((e) => this.handleError(e)));
  }

  getCoursesByTeacher(userId: String): Observable<Course[]> {
    const url = BASE_PATH + "courses/teacher/" + userId;
    return this.http
      .get<Course[]>(url)
      .pipe(catchError((e) => this.handleError(e)));
  }

  getCourse(name: string): Observable<Course> {
    const url = BASE_PATH + "courses/" + name;
    return this.http
      .get<Course>(url)
      .pipe(catchError((e) => this.handleError(e)));
  }

  updateCourse(course: Course, name: string, userId: String): Observable<Course> {
    const url = BASE_PATH + "courses/" + name;
    //return this.http
    //  .patch<Course>(url, course)
    //  .pipe(catchError((e) => this.handleError(e)));
    const body = {
      course,
      userId
    };
    return this.http.patch(url, body)
      .pipe(catchError((e) => this.handleError(e)));
  }

  addCourse(course: Course, userId: String) {
    const url = BASE_PATH + "courses/";
    const body = {
      course,
      userId
    };
    return this.http.post(url, body)
      .pipe(catchError((e) => this.handleError(e)));
    //return this.http
    //  .post<Course>(url, course)
    //  .pipe(catchError((e) => this.handleError(e)));
  }
  enrollOne(course: Course, student: Student): Observable<any> {
    const url = BASE_PATH + "courses/" + course.name + "/enrollOne";
    return this.http.post<void>(url, _.omit(student, "links")).pipe(
      map((e) => {
        if (e == null) {
          return "ok";
        }
        return e;
      }),
      catchError((e) => this.handleError(e))
    );
  }

  addAndEnrollFromCsv(course: Course, data: any): Observable<Array<boolean>> {
    const url = BASE_PATH + "courses/" + course.name + "/addAndEnroll";
    return this.http
      .post<void>(url, data)
      .pipe(catchError((e) => this.handleError(e)));
  }

  unEnrollMany(
    course: Course,
    studentIds: Array<string>
  ): Observable<Array<boolean>> {
    const url = BASE_PATH + "courses/" + course.name + "/unEnrollMany";
    return this.http
      .patch<Array<string>>(url, studentIds)
      .pipe(catchError((e) => this.handleError(e)));
  }

  getStudentsNotInTeam(courseName: string): Observable<Student[]> {
    const url = BASE_PATH + "courses/" + courseName + "/studentsNotInTeam";
    return this.http
      .get<Student[]>(url)
      .pipe(catchError((e) => this.handleError(e)));
  }
  proposeTeam(courseName: string, proposal: TeamProposal): Observable<any> {
    const url = BASE_PATH + "courses/" + courseName + "/proposeTeam";
    return this.http
      .post(url, proposal)
      .pipe(catchError((e) => this.handleError(e)));
  }
  confirmTeam(token: string): Observable<any> {
    const url = BASE_PATH + "notification/confirm/" + token;
    return this.http.get(url).pipe(catchError((e) => this.handleError(e)));
  }
  rejectTeam(token: string): Observable<any> {
    const url = BASE_PATH + "notification/reject/" + token;
    return this.http.get(url).pipe(catchError((e) => this.handleError(e)));
  }

  getAllAssignments(courseName: string): Observable<Assignment[]> {
    const url = BASE_PATH + "courses/" + courseName + "/assignments";
    return this.http
      .get<Assignment[]>(url)
      .pipe(catchError((e) => this.handleError(e)));
  }

  getAssignment(assignmentId: number): Observable<Assignment> {
    const url = BASE_PATH + "courses/" + "assignments/" + assignmentId;
    return this.http
      .get<Assignment>(url)
      .pipe(catchError((e) => this.handleError(e)))
  }


  getAllPapersForAssignment(assignmentId: number): Observable<Paper[]> {
    const url = BASE_PATH + "courses/" + "assignments/" + assignmentId + "/papers";
    return this.http
      .get<Paper[]>(url)
      .pipe(catchError((e) => this.handleError(e)))
  }

  getAllPapersnapshotsForPaper(paperId: number): Observable<PaperSnapshot[]> {
    const url = BASE_PATH + "courses/" + "assignments/" + "papers/" + paperId + "/papersnapshots"
    return this.http
      .get<PaperSnapshot[]>(url)
      .pipe(catchError((e) => this.handleError(e)))
  }

  getPaper(paperId: number): Observable<Paper> {
    const url = BASE_PATH + "courses/" + "papers/" + paperId
    return this.http
    .get<Paper>(url)
    .pipe(catchError((e) => this.handleError(e)))
  }

  addPapersnapshot(paperId: Number, formData: SolutionFormData) {
    const url = BASE_PATH + "courses/" + "assignments/" + "papers/" + paperId + "/papersnapshots" + "/addPapersnapshot"
    return this.http
      .post<String>(url, formData)
      .pipe(catchError((e) => this.handleError(e)))
  }

  addAssignmentToCourse(courseName: string, assignment: Assignment) {
    const url = BASE_PATH + "courses/" + courseName + "/assignments" + "/addAssignment"
    return this.http
      .post<String>(url, assignment)
      .pipe(catchError((e) => this.handleError(e)))
  }

  getTeamsPerCourse(courseName: String): Observable<Team[]> {
    const url = BASE_PATH + "courses/" + courseName + "/teams";
    return this.http
      .get(url)
      .pipe(
        map((teams: Team[]) =>
          _.map(teams, (team: Team) => {
            team.vmInstances.forEach((instance) =>
              _.set(instance, "image", IMG_PATH + instance.image)
            );
            console.log(team);

            return team;
          })
        ),
        tap((vm) => {
          console.log(vm);
        })
      )
      .pipe(catchError((e) => this.handleError(e)));
  }

  getCourseVmModel(courseName: String): Observable<VmModel> {
    const url = BASE_PATH + "courses/" + courseName + "/vmmodel";
    return this.http
      .get<VmModel>(url)
      .pipe(
        map((vm: VmModel) => {
          vm.image = IMG_PATH + vm.image;
          return vm;
        })
      )
      .pipe(catchError((e) => this.handleError(e)));
  }

  editModel(course: String, newModel: JSON): Observable<VmModel> {
    const url = BASE_PATH + "courses/" + course + "/editvmmodel/";
    return this.http
      .post<VmModel>(url, newModel)
      .pipe(catchError((e) => this.handleError(e)));
  }

  private handleError(error) {
    console.log(error)
    let errorMessage = "";
    if (error.error instanceof ErrorEvent) {
      // client-side error
      errorMessage = `Error: ${error.message}`;
    } else {
      // server-side error
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
    }
    // this.toastService.error(err.message, err.status.toString())

    // window.alert(errorMessage);

    this.toastService.error(error.message, error.status.toString());
    console.log("HTTP ERROR", error);
    return of(null);
  }
}
