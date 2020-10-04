import { Component, OnDestroy, OnInit } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { BehaviorSubject, combineLatest, Observable, Subscription } from "rxjs";
import { switchMap, tap } from "rxjs/operators";
import { Course } from "src/app/models/course.model";
import { Student } from "src/app/models/student.model";
import { CourseService } from "src/app/services/course.service";
import { StudentService } from "src/app/services/student.service";
import { UtilsService } from "src/app/services/utils.service";

@Component({
  selector: "app-course-dashboard",
  templateUrl: "./course-dashboard.html",
  styleUrls: ["./course-dashboard.sass"],
})
export class CourseDashboard implements OnInit, OnDestroy {
  tabs = [
    {
      value: "students",
      path: "students",
    },
    {
      value: "vms",
      path: "vms",
    },
  ];
  currentPath = "";
  private _currentCourseName$: BehaviorSubject<string> = new BehaviorSubject(
    null
  );
  private _reloadSubject$: BehaviorSubject<void> = new BehaviorSubject(null);
  private courseSubscription: Subscription;
  private routeSubscription: Subscription;
  private enrollSubscription: Subscription;
  studentsDB$: Observable<Student[]>;
  currentCourse: Course;
  currentCourse$: Observable<Course>;
  enrolledStudents$: Observable<Student[]>;
  isLoading = false;
  constructor(
    private studentService: StudentService,
    private route: ActivatedRoute,
    private utilsService: UtilsService,
    private courseService: CourseService
  ) {}

  ngOnInit(): void {
    console.log("dashboard on init");
    this.studentsDB$ = this.studentService.getAllStudents();

    // take the name of the route (course name)
    this.routeSubscription = this.route.url.subscribe((evt) => {
      (this.currentPath = evt[0].path),
        console.log("tab route change", "data", this.currentPath);
      const name = evt[0].path;
      if (name) {
        this._currentCourseName$.next(name);
      }
    });

    this.currentCourse$ = this._currentCourseName$.pipe(
      tap(() => (this.isLoading = true)),
      switchMap((name) => {
        if (name) return this.courseService.getCourse(name);
      }),
      tap((course) => {
        this.currentCourse = course;
      })
    );

    this.enrolledStudents$ = combineLatest([
      this.currentCourse$,
      this._reloadSubject$,
    ]).pipe(
      tap(() => (this.isLoading = true)),
      switchMap(([course, v]) => {
        if (course && course.name)
          return this.studentService.getEnrolledStudents(course.name);
      }),
      tap(() => (this.isLoading = false))
    );
  }

  enrollStudent(student: Student) {
    this.isLoading = true;

    this.enrollSubscription = this.courseService
      .enrollOne(this.currentCourse, student)
      .subscribe((evt) => {
        this.isLoading = false;
        // trigger reload
        this._reloadSubject$.next(null);
      });
  }

  ngOnDestroy(): void {
    if (this.courseSubscription) this.courseSubscription.unsubscribe();
    if (this.routeSubscription) this.routeSubscription.unsubscribe();
    if (this.enrollSubscription) this.enrollSubscription.unsubscribe();
  }
}
