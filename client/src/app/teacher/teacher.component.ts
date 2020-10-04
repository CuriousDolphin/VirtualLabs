import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { MatSidenav } from '@angular/material/sidenav';
import { BehaviorSubject, combineLatest, Observable, Subscription } from 'rxjs';
import { map } from 'rxjs/operators';
import { Course } from '../models/course.model';
import { CourseService } from '../services/course.service';
import { UtilsService } from '../services/utils.service';

@Component({
  selector: 'app-teacher',
  templateUrl: './teacher.component.html',
  styleUrls: ['./teacher.component.sass']
})
export class TeacherComponent implements OnInit,OnDestroy {
  menuSubscription:Subscription;
  reloadData:BehaviorSubject<void> = new BehaviorSubject(null);
  courses$:Observable<Course[]>;

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
  constructor(private utilsService: UtilsService,private courseService:CourseService) { }


  ngOnInit(): void {
    this.menuSubscription=this.utilsService.toggleMenu$.subscribe(() => {
      if(this.sidenav)
        this.sidenav.opened = !this.sidenav.opened;
    })

    // get all courses withouth subscription (async in template),combinelatest need for dynamic reload of courses
    this.courses$ =
      combineLatest([this.reloadData,this.courseService.getAllCourses()])
      .pipe(map(([reaload,courses])=>courses))
  }

  ngOnDestroy(): void {
    if(this.menuSubscription) this.menuSubscription.unsubscribe();
  }

}
