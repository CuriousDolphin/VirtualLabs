import { Component, OnInit, ViewChild } from "@angular/core";
import { MatSidenav } from "@angular/material/sidenav";
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: "app-home",
  template: "<h2>home</h2>",
})
export class HomeComponent implements OnInit {
  constructor(private route: ActivatedRoute) {}
  ngOnInit() {}
}
