import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { Assignment } from 'src/app/models/assignment.model';
import { Paper } from 'src/app/models/paper.model';
import { PaperSnapshot } from 'src/app/models/papersnapshot.model';

@Component({
  selector: 'app-assignment',
  templateUrl: './assignment.component.html',
  styleUrls: ['./assignment.component.sass']
})
export class AssignmentComponent implements OnInit {
  assignmentsData = []
  papersData = []
  papersnapshotsData = []
  toShowLevel: number

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

  @Input() set papersnapshots(papersnapshots: PaperSnapshot[]) {
    if (papersnapshots != null) {
      this.papersnapshotsData = papersnapshots
    }
  }

  @Output() assignmentClickedEvent = new EventEmitter<number>()
  @Output() paperClickedEvent = new EventEmitter<number>()

  constructor() { }

  ngOnInit(): void {
    this.toShowLevel = 0
  }

  back() {
    this.toShowLevel = this.toShowLevel - 1
  }
  
  assignmentClicked(assignmentId: number) {
    this.assignmentClickedEvent.emit(assignmentId)
    this.toShowLevel = this.toShowLevel + 1 
  }

  paperClicked(paperId: number) {
    this.paperClickedEvent.emit(paperId)
    this.toShowLevel = this.toShowLevel + 1
  }

}
