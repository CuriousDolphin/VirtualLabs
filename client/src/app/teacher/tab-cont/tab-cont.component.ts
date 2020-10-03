import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-tab-cont',
  templateUrl: './tab-cont.component.html',
  styleUrls: ['./tab-cont.component.sass']
})
export class TabContComponent implements OnInit {
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
  constructor() { }

  ngOnInit(): void {
    console.log("tab on init")
  }

}
