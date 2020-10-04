import { Component, OnInit } from "@angular/core";
import { Student } from "../../models/student.model";
import * as _ from "lodash";
import { StudentService } from "../../services/student.service";
import { Observable, of, Subscription } from "rxjs";
import { tap } from "rxjs/internal/operators/tap";
import { distinctUntilChanged, startWith, switchMap } from "rxjs/operators";
import { ActivatedRoute } from "@angular/router";
import { UtilsService } from "src/app/services/utils.service";
import { Course } from "src/app/models/course.model";
import { CourseService } from "src/app/services/course.service";

@Component({
  selector: "app-students-cont",
  templateUrl: "./students-cont.component.html",
  styleUrls: ["./students-cont.component.sass"],
})
export class StudentsContComponent implements OnInit {
  private routeSubscription: Subscription;

  enrolledStudents$: Observable<Student[]>;
  studentsDB$: Observable<Student[]>;
  isLoading = false;
  currentCourse: Course;
  constructor(
    private studentService: StudentService,
    private route: ActivatedRoute,
    private utilsService: UtilsService,
    private courseService: CourseService
  ) {}

  ngOnInit() {
    //this.enrolledStudents$ = this.getEnrolledStudent();
    this.studentsDB$ = this.getAllStudent();

    // first take the current course, next take the enrolled students for course

    this.enrolledStudents$ = this.utilsService.currentCourse$.pipe(
      distinctUntilChanged(),
      tap((course) => {
        if (course) {
          this.currentCourse = course;
          console.log("current course", course);
        }
      }),
      switchMap((course) => {
        if (course) return this.studentService.getEnrolledStudents(course.name);
        else return of([]);
      })
    );
  }
  private getEnrolledStudent(): Observable<Student[]> {
    this.isLoading = true;
    return this.studentService
      .getEnrolledStudents(this.currentCourse.name)
      .pipe(tap(() => (this.isLoading = false)));
  }
  private getAllStudent(): Observable<Student[]> {
    return this.studentService.getAllStudents();
  }
  removeStudents(students: Student[]) {
    this.isLoading = true;
    /*this.enrolledStudents$ = this.studentService
      .updateEnrolled(students, 0)
      .pipe(
        tap(() => console.log('Remove students')),
        switchMap(() => this.getEnrolledStudent())
      );*/
  }
  addStudent(student: Student) {
    this.isLoading = true;
    this.enrolledStudents$ = this.studentService
      .updateEnrolled([student], this.currentCourse.name)
      .pipe(
        tap(() => console.log("add students")),
        switchMap(() => this.getEnrolledStudent()),
        tap(() => (this.isLoading = false))
      );
  }
  enrollStudent(student: Student) {
    this.isLoading = true;
    this.enrolledStudents$ = this.courseService
      .enrollOne(this.currentCourse, student)
      .pipe(
        tap(() => console.log("add students")),
        switchMap(() => this.getEnrolledStudent()),
        tap(() => (this.isLoading = false))
      );
  }
}
