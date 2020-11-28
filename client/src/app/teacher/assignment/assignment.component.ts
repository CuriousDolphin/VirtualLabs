import { Component, Input, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Assignment } from 'src/app/models/assignment.model';

@Component({
  selector: 'app-assignment',
  templateUrl: './assignment.component.html',
  styleUrls: ['./assignment.component.sass']
})
export class AssignmentComponent implements OnInit {
  colsToDisplay=["id", "content", "releaseDate", "expiryDate"]
  dataSource = new MatTableDataSource<Assignment>();
  @Input() set assignments(assignments: Assignment[]) {
    if(assignments != null) {
      this.dataSource.data = assignments;
      console.log("Assignments by course:" + this.dataSource.data); //li riceve
    }
  }

  consola(Row) {
    console.log(Row);
  }

  constructor() { }

  ngOnInit(): void {
  }

}
