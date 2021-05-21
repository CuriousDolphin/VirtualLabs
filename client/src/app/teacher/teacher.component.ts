import {
  AfterViewChecked,
  AfterViewInit,
  Component,
  OnChanges,
  OnDestroy,
  OnInit,
  SimpleChanges,
  ViewChild,
} from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatSidenav } from "@angular/material/sidenav";
import { ActivatedRoute, Params, Router } from "@angular/router";
import { BehaviorSubject, combineLatest, Observable, Subscription } from "rxjs";
import { map, skip, switchMap, tap } from "rxjs/operators";
import { AuthService } from '../auth/auth.service';
import { Course } from "../models/course.model";
import { User } from "../models/user.model";
import { CourseService } from "../services/course.service";
import { ToastService } from "../services/toast.service";
import { UtilsService } from "../services/utils.service";
import { CourseDialogComponent } from "./course-dialog/course-dialog.component";
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: "app-teacher",
  templateUrl: "./teacher.component.html",
  styleUrls: ["./teacher.component.sass"],
})
export class TeacherComponent implements OnInit, OnDestroy {
  menuSubscription: Subscription;
  private _reloadSubject$: BehaviorSubject<void> = new BehaviorSubject(null);
  courses$: Observable<Course[]>;
  dialogSubscription: Subscription;
  userSubscription: Subscription;
  isLoading = false;
  user: User = null;
  routeSubscription: Subscription;
  reloadCourseFromServiceSubscription: Subscription;

  @ViewChild(MatSidenav) sidenav: MatSidenav;

  constructor(
    private utilsService: UtilsService,
    private courseService: CourseService,
    public dialog: MatDialog,
    private toastService: ToastService,
    private route: ActivatedRoute,
    private authService: AuthService,
    private router: Router,
    private domSanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    this.userSubscription = this.authService.currentUser$.subscribe(
      (user: User) => {
        if (user != null) {
         
          this.user = user;

   
        } else {
          this.user = null;
        }
      }
    );


    
    this.routeSubscription = this.route.url.subscribe((params: Params) => {
    });

    // reload courses from service
    this.reloadCourseFromServiceSubscription = this.utilsService.reloadCurses$
      .pipe(skip(1))
      .subscribe(() => this._reloadSubject$.next(null));

    this.menuSubscription = this.utilsService.toggleMenu$.subscribe(() => {
      if (this.sidenav) this.sidenav.opened = !this.sidenav.opened;
    });

    // get all courses withouth subscription (async in template),combinelatest need for dynamic reload of courses
    this.courses$ = this._reloadSubject$.pipe(
      tap(() => (this.isLoading = true)),
      //TODO TAKE ONLY TEACHER COURSE
      switchMap(() => this.courseService.getCoursesByTeacher(this.authService.getUserId())),
      tap(() => (this.isLoading = false))
    );
  }

  ngOnDestroy(): void {
    if (this.menuSubscription) this.menuSubscription.unsubscribe();
  }

  renderTrustImage(base64: string) {
    return this.domSanitizer.bypassSecurityTrustUrl(base64)
  }

  createCourse() {
    if (this.dialogSubscription) this.dialogSubscription.unsubscribe();

    const dialogRef = this.dialog.open(CourseDialogComponent, {
      data: { mode: "Create" },
    });

    this.dialogSubscription = dialogRef
      .afterClosed()
      .subscribe((result: Course) => {
        if (result !== null && result !== undefined) {
          this.toastService.success("Create success!");
          // reload data
          this._reloadSubject$.next(null);
        }
      });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(["home"]);
  }
}
