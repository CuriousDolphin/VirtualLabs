import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Assignment } from 'src/app/models/assignment.model';

@Component({
  selector: 'app-assignment',
  templateUrl: './assignment.component.html',
  styleUrls: ['./assignment.component.sass']
})
export class AssignmentComponent implements OnInit {
  colsToDisplay=["id", "content", "releaseDate", "expiryDate"]
  assignmentsData = [];
  @Input() set assignments(assignments: Assignment[]) {
    if(assignments != null) {
      this.assignmentsData = assignments
    }
  }

  @Output() assignmentClickedEvent = new EventEmitter<number>()

  constructor() { }

  ngOnInit(): void {
  }

  assignmentClicked(assignmentId: number) {
    this.assignmentClickedEvent.emit(assignmentId)
  }
}
