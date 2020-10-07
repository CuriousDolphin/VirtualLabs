import { Component, OnDestroy, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { BehaviorSubject, combineLatest, Observable, Subscription } from 'rxjs';
import { switchMap, tap, map } from 'rxjs/operators';
import { Course } from 'src/app/models/course.model';
import { Student } from 'src/app/models/student.model';
import { CourseService } from 'src/app/services/course.service';
import { StudentService } from 'src/app/services/student.service';
import { ToastService } from 'src/app/services/toast.service';
import { UtilsService } from 'src/app/services/utils.service';
import { CourseDialogComponent } from '../course-dialog/course-dialog.component';

@Component({
  selector: 'app-course-dashboard',
  templateUrl: './course-dashboard.html',
  styleUrls: ['./course-dashboard.sass'],
})
export class CourseDashboard implements OnInit, OnDestroy {
  /* tabs = [
    {
      value: 'students',
      path: 'students',
    },
    {
      value: 'vms',
      path: 'vms',
    },
  ]; */
  currentPath = '';
  private _currentCourseName$: BehaviorSubject<string> = new BehaviorSubject(
    null
  );
  private _reloadStudents$: BehaviorSubject<void> = new BehaviorSubject(null);

  private courseSubscription: Subscription;
  private routeSubscription: Subscription;
  private enrollSubscription: Subscription;
  private unenrollSubscription: Subscription;
  private dialogSubscription: Subscription;

  studentsDB$: Observable<Student[]>;
  currentCourse: Course;
  currentCourse$: Observable<Course>;
  enrolledStudents$: Observable<Student[]>;
  isLoading = false;
  constructor(
    private studentService: StudentService,
    private route: ActivatedRoute,
    private utilsService: UtilsService,
    private courseService: CourseService,
    public dialog: MatDialog,
    private toastService: ToastService,
    private router: Router,
  ) { }

  ngOnInit(): void {
    console.log('dashboard on init');
    this.studentsDB$ = this.studentService.getAllStudents();

    // take the name of the route (course name)
    this.routeSubscription = this.route.url.subscribe((evt) => {
      (this.currentPath = evt[0].path),
        console.log('tab route change', 'data', this.currentPath);
      const name = evt[0].path;
      if (name) {
        this._currentCourseName$.next(name);
      }
    });

    // get current course after name changes and reload course is triggered
    this.currentCourse$ =
      this._currentCourseName$

        .pipe(
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
      this._reloadStudents$,
    ]).pipe(
      tap(() => (this.isLoading = true)),
      switchMap(([course, v]) => {
        if (course && course.name)
          return this.studentService.getEnrolledStudents(course.name);
      }),
      tap(() => (this.isLoading = false))
    );
  }

  unEnrollStudents(studentsId: Array<string>) {
    console.log('going to unenroll students', studentsId)
    this.isLoading = true;
    this.unenrollSubscription = this.courseService
      .unEnrollMany(this.currentCourse, studentsId)
      .subscribe((evt: Array<boolean>) => {
        this.isLoading = false;
        if (evt !== null) {
          let countError = 0;
          evt.forEach(r => {
            if (!r)
              countError++;
          })

          if (countError === 0) {
            this.toastService.success(' students correctly unenrolled')
          } else {
            this.toastService.info(countError + ' students failed of ' + evt.length)

          }
          console.log('delete students', evt);
        }

        // trigger reload
        this._reloadStudents$.next(null);
      })

  }

  enrollStudent(student: Student) {
    this.isLoading = true;

    this.enrollSubscription = this.courseService
      .enrollOne(this.currentCourse, student)
      .subscribe((evt) => {
        this.isLoading = false;
        this.toastService.success('enroll success');
        // trigger reload
        this._reloadStudents$.next(null);

      });
  }

  openUpdateDialog() {
    if (this.dialogSubscription) this.dialogSubscription.unsubscribe();
    const dialogRef = this.dialog.open(CourseDialogComponent, { data: { mode: 'Update', course: this.currentCourse } },);

    this.dialogSubscription = dialogRef.afterClosed().subscribe((result: Course) => {
      console.log(`Dialog result: ${result}`);
      if (result !== null && result !== undefined) {
        this.toastService.success('update success!');
        // this._currentCourseName$.next(result.name);

        // reload data parent
        this.utilsService.reloadCurses();
        // reload data this
        this.router.navigate(['teacher/' + result.name]);


      }
    })



  }

  ngOnDestroy(): void {
    if (this.dialogSubscription) this.dialogSubscription.unsubscribe();
    if (this.courseSubscription) this.courseSubscription.unsubscribe();
    if (this.routeSubscription) this.routeSubscription.unsubscribe();
    if (this.enrollSubscription) this.enrollSubscription.unsubscribe();
  }
}
