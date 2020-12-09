import { Component, OnInit, Input, EventEmitter, Output, ViewChild} from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Assignment } from 'src/app/models/assignment.model';
import { formatDate } from '@angular/common'
import { MatSort } from "@angular/material/sort";

@Component({
  selector: 'app-assignment-assignment',
  templateUrl: './assignment-assignment.component.html',
  styleUrls: ['./assignment-assignment.component.sass']
})
export class AssignmentAssignmentComponent implements OnInit {

  constructor() { }

  @ViewChild(MatSort, {static: true}) sort: MatSort

  colsToDisplay=["content", "releaseDate", "expiryDate"]
  dataSource = new MatTableDataSource<Assignment>();
  @Input() set assignmentsData(assignments: Assignment[]) {
    if(assignments != null) {
      this.dataSource.data = assignments;
    }
  }

  @Output() clickAssignmentEvent = new EventEmitter<number>()

  clickAssignment(assignmentId: number) {
    this.clickAssignmentEvent.emit(assignmentId);
  }

  format(date) {
    return formatDate(date, 'yyyy-MM-dd hh:mm:ss', 'en', 'GMT')
  }



  ngOnInit(): void {
    this.dataSource.sort = this.sort
  }

}
