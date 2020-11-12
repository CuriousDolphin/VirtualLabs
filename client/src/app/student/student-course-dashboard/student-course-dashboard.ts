import { Component, OnDestroy, OnInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { BehaviorSubject, combineLatest, Observable, Subscription } from "rxjs";
import { switchMap, tap, map, shareReplay } from "rxjs/operators";
import { Course } from "src/app/models/course.model";
import { CourseService } from "src/app/services/course.service";
import { ToastService } from "src/app/services/toast.service";
import { UtilsService } from "src/app/services/utils.service";

@Component({
  selector: "app-course-dashboard",
  templateUrl: "./student-course-dashboard.html",
  styleUrls: ["./student-course-dashboard.sass"],
})
export class StudentCourseDashboard implements OnInit, OnDestroy {
  currentPath = "";
  private _currentCourseName$: BehaviorSubject<string> = new BehaviorSubject(
    null
  );
  private _reloadCourse$: BehaviorSubject<void> = new BehaviorSubject(null);

  private courseSubscription: Subscription;
  private routeSubscription: Subscription;
  currentCourse: Course;
  currentCourse$: Observable<Course>;

  isLoading = false;
  constructor(
    private route: ActivatedRoute,
    private utilsService: UtilsService,
    private courseService: CourseService,
    private toastService: ToastService,
    private router: Router
  ) {}

  ngOnInit(): void {
    console.log("student dashboard on init");

    // take the name of the route (course name)
    this.routeSubscription = this.route.url.subscribe((evt) => {
      const name = evt[0].path;
      console.log("student course dashboard route change", "data", name);
      if (name) {
        this._currentCourseName$.next(name);
      }
    });
    // get current course after name changes and reload course is triggered
    this.currentCourse$ = this._currentCourseName$.pipe(
      tap(() => (this.isLoading = true)),
      switchMap((name) => {
        if (name) return this.courseService.getCourse(name);
      }),
      tap((course) => {
        this.currentCourse = course;
      }),
      tap(() => (this.isLoading = false))
    );
  }

  ngOnDestroy(): void {
    if (this.courseSubscription) this.courseSubscription.unsubscribe();
    if (this.routeSubscription) this.routeSubscription.unsubscribe();
  }
}
