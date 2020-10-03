import { Component, OnInit, ViewChild } from '@angular/core';
import { MatSidenav } from '@angular/material/sidenav';
import { UtilsService } from '../services/utils.service';

@Component({
  selector: 'app-teacher',
  templateUrl: './teacher.component.html',
  styleUrls: ['./teacher.component.sass']
})
export class TeacherComponent implements OnInit {
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
  constructor(private utilsService: UtilsService) { }

  ngOnInit(): void {
    this.utilsService.toggleMenu$.subscribe(() => {
      if(this.sidenav)
        this.sidenav.opened = !this.sidenav.opened;
    })
  }

}
