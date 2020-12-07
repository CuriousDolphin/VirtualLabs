import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Assignment } from 'src/app/models/assignment.model';
import { Paper } from 'src/app/models/paper.model';
@Component({
  selector: 'app-assignment',
  templateUrl: './assignment.component.html',
  styleUrls: ['./assignment.component.sass']
})
export class AssignmentComponent implements OnInit {
  assignmentsData = []
  papersData = []
  assignment: Boolean
  paper: Boolean
  paperSnapshot: Boolean

  @Input() set assignments(assignments: Assignment[]) {
    if(assignments != null) {
      this.assignmentsData = assignments
    }
  }
  @Input() set papers(papers: Paper[]) {
    if(papers != null) {
      this.papersData = papers
    }
  }

  @Output() assignmentClickedEvent = new EventEmitter<number>()

  constructor() { }

  ngOnInit(): void {
  }

  assignmentClicked(assignmentId: number) {
    this.assignmentClickedEvent.emit(assignmentId)
    this.paper=true
    this.assignment=false
    this.paperSnapshot=false
  }
}
