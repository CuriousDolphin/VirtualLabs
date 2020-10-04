import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-course-dashboard',
  templateUrl: './course-dashboard.html',
  styleUrls: ['./course-dashboard.sass']
})
export class CourseDashboard implements OnInit {
  tabs = [
    {
      value: 'students',
      path: 'students',
    },
    {
      value: 'vms',
      path: 'vms',
    },
  ];
  currentPath: string = '';
  constructor(private route: ActivatedRoute,) { }

  ngOnInit(): void {
    console.log("tab on init");

    this.route.url.subscribe((evt) => { this.currentPath = evt[0].path });
  }

}
