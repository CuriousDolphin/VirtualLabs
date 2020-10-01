import { Component, OnInit } from "@angular/core";
import { Student } from "../models/student.model";
import * as _ from "lodash";
import { StudentService } from "../services/student.service";
import { Observable } from "rxjs";
import { tap } from "rxjs/internal/operators/tap";
import { startWith, switchMap } from "rxjs/operators";

@Component({
  selector: "app-students-cont",
  templateUrl: "./students-cont.component.html",
  styleUrls: ["./students-cont.component.sass"],
})
export class StudentsContComponent implements OnInit {
  private courseId = 1;
  enrolledStudents$: Observable<Student[]>;
  studentsDB$: Observable<Student[]>;
  isLoading = false;
  constructor(private studentService: StudentService) {}

  ngOnInit() {
    this.enrolledStudents$ = this.getEnrolledStudent();
    this.studentsDB$ = this.getAllStudent();
  }
  private getEnrolledStudent(): Observable<Student[]> {
    this.isLoading = true;
    return this.studentService
      .getEnrolledStudents(this.courseId)
      .pipe(tap(() => (this.isLoading = false)));
  }
  private getAllStudent(): Observable<Student[]> {
    return this.studentService.getAllStudents();
  }
  removeStudents(students: Student[]) {
    this.isLoading = true;
    this.enrolledStudents$ = this.studentService
      .updateEnrolled(students, 0)
      .pipe(
        tap(() => console.log("Remove students")),
        switchMap(() => this.getEnrolledStudent())
      );
  }
  addStudent(student: Student) {
    this.isLoading = true;
    this.enrolledStudents$ = this.studentService
      .updateEnrolled([student], this.courseId)
      .pipe(
        tap(() => console.log("add students")),
        switchMap(() => this.getEnrolledStudent())
      );
  }
}
