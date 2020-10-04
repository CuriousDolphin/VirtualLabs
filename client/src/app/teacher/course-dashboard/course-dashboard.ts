import { Component, OnDestroy, OnInit } from "@angular/core";
import { ActivatedRoute } from "@angular/router";
import { BehaviorSubject, Observable, Subscription } from "rxjs";
import { tap } from "rxjs/operators";
import { Course } from "src/app/models/course.model";
import { CourseService } from "src/app/services/course.service";
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
  private _currentCourse$: BehaviorSubject<Course> = new BehaviorSubject(null);
  private courseSubscription: Subscription;
  private routeSubscription: Subscription;
  currentCourse: Course;
  isLoading = false;
  constructor(
    private route: ActivatedRoute,
    private courseService: CourseService,
    private utilsService: UtilsService
  ) {}
  ngOnDestroy(): void {
    if (this.courseSubscription) this.courseSubscription.unsubscribe();
    if (this.routeSubscription) this.routeSubscription.unsubscribe();
  }

  ngOnInit(): void {
    console.log("tab on init");

    this.courseSubscription = this._currentCourse$.subscribe(
      (course: Course) => {
        this.currentCourse = course;
      }
    );

    this.routeSubscription = this.route.url.subscribe((evt) => {
      (this.currentPath = evt[0].path),
        console.log("tab route change", "data", this.currentPath);
      const name = evt[0].path;
      if (name) {
        this.isLoading = true;
        this.getCourse(name);
      }
    });
  }

  getCourse(name: string) {
    this.isLoading = true;
    this.courseSubscription = this.courseService
      .getCourse(name)
      .pipe(tap(() => (this.isLoading = false)))
      .subscribe((course) => {
        this.currentCourse = course;
        this.utilsService.setCurrentCourse(course);
      });
  }
}
