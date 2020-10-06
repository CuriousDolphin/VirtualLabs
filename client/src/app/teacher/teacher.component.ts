import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSidenav } from '@angular/material/sidenav';
import { BehaviorSubject, combineLatest, Observable, Subscription } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';
import { Course } from '../models/course.model';
import { CourseService } from '../services/course.service';
import { ToastService } from '../services/toast.service';
import { UtilsService } from '../services/utils.service';
import { CourseDialogComponent } from './course-dialog/course-dialog.component';

@Component({
  selector: 'app-teacher',
  templateUrl: './teacher.component.html',
  styleUrls: ['./teacher.component.sass']
})
export class TeacherComponent implements OnInit, OnDestroy {
  menuSubscription: Subscription;
  private _reloadSubject$: BehaviorSubject<void> = new BehaviorSubject(null);
  courses$: Observable<Course[]>;
  dialogSubscription: Subscription;
  isLoading = false;


  @ViewChild(MatSidenav) sidenav: MatSidenav;
  tabs = [
    {
      value: 'students',
      path: 'applicazioni-internet/students',
    },
    {
      value: 'vms',
      path: 'applicazioni-internet/vms',
    },
  ];
  constructor(
    private utilsService: UtilsService,
    private courseService: CourseService,
    public dialog: MatDialog,
    private toastService: ToastService) { }


  ngOnInit(): void {
    this.menuSubscription = this.utilsService.toggleMenu$.subscribe(() => {
      if (this.sidenav)
        this.sidenav.opened = !this.sidenav.opened;
    })

    // get all courses withouth subscription (async in template),combinelatest need for dynamic reload of courses
    this.courses$ =
      this._reloadSubject$.pipe(
        tap(() => this.isLoading = true),
        switchMap(() => this.courseService.getAllCourses()),
        tap(() => this.isLoading = false),
      )

  }

  ngOnDestroy(): void {
    if (this.menuSubscription) this.menuSubscription.unsubscribe();
  }

  createCourse() {
    if (this.dialogSubscription) this.dialogSubscription.unsubscribe();

    const dialogRef = this.dialog.open(CourseDialogComponent, { data: { mode: 'Create' } },);

    this.dialogSubscription = dialogRef.afterClosed().subscribe((result) => {
      console.log(`Dialog result: ${result}`);
      if (result === true) {
        this.toastService.success('Create success!');
        // reload data
        this._reloadSubject$.next(null);
      }
    })

  }

}
