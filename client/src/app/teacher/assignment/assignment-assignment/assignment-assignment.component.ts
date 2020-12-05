import { Component, OnInit, Input, EventEmitter, Output} from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Assignment } from 'src/app/models/assignment.model';

@Component({
  selector: 'app-assignment-assignment',
  templateUrl: './assignment-assignment.component.html',
  styleUrls: ['./assignment-assignment.component.sass']
})
export class AssignmentAssignmentComponent implements OnInit {

  constructor() { }

  colsToDisplay=["id", "content", "releaseDate", "expiryDate"]
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

  ngOnInit(): void {
  }

}
