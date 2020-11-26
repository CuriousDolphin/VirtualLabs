import { Component, Input, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Assignment } from 'src/app/models/assignment.model';

@Component({
  selector: 'app-assignment',
  templateUrl: './assignment.component.html',
  styleUrls: ['./assignment.component.sass']
})
export class AssignmentComponent implements OnInit {
  dataSource = new MatTableDataSource<Assignment>();
  @Input() set assignments(assignments: Assignment[]) {
    if(assignments != null) {
      console.log('Assignments: ' + assignments);
      this.dataSource.data = assignments;
    }
  }

  constructor() { }

  ngOnInit(): void {
  }

}
