import { Component, Input, OnInit, Output, EventEmitter } from '@angular/core';
import { PaperSnapshot } from 'src/app/models/papersnapshot.model';
import { StudentAssignment } from 'src/app/models/studentAssignment.model';

@Component({
  selector: 'app-student-assignment',
  templateUrl: './student-assignment.component.html',
  styleUrls: ['./student-assignment.component.sass']
})
export class StudentAssignmentComponent implements OnInit {

  toShowLevel: number
  assignmentsData = []
  paperSnapshotsData = []
  currAssignment: StudentAssignment
  currentAssignmentId: number

  @Input() set studentAssignments(assignments: StudentAssignment[]) {
    this.resetData();
    if (assignments != null) {
      this.assignmentsData = assignments
    }
  }

  @Input() set studentPaperSnapshots(paperSnapshots: PaperSnapshot[]) {
    if (paperSnapshots != null)
      this.paperSnapshotsData = paperSnapshots
  }


  @Input() set currentAssignment(assignment: StudentAssignment) {
    if(assignment != null) 
      this.currAssignment = assignment
  }

  @Output() updatePaperStatusEvent = new EventEmitter<{ assignmentId: number, status: String }>();
  @Output() assignmentClickedEvent = new EventEmitter<number>();
  @Output() solutionSubmittedEvent = new EventEmitter<{ paperSnapshot: PaperSnapshot, assignmentId: number }>();

  constructor() { }

  ngOnInit(): void {
    this.toShowLevel = 0
  }

  back() {
    this.toShowLevel = this.toShowLevel - 1
  }
  resetData(){
    this.toShowLevel=0;
    this.currAssignment=null;
    this.paperSnapshotsData=null;
    this.assignmentsData=null;
  }

  assignmentClicked(assignmentId: number) {
    this.assignmentClickedEvent.emit(assignmentId)
    this.currentAssignmentId = assignmentId
    this.toShowLevel = this.toShowLevel + 1
  }

  solutionSubmitted(papersnapshot: PaperSnapshot) {
    this.solutionSubmittedEvent.emit(
      {
        paperSnapshot: papersnapshot,
        assignmentId: this.currentAssignmentId
      })
  }

  breadCrumbHelper(type: string) {
    let returnValue
    console.log("bread")
    switch (type) {
      case "assignment":
        returnValue = this.toShowLevel > 0 && this.currAssignment ? "(" + this.currAssignment.title + ")" : ""
        break
      default:
        returnValue = ""
        break
    }
    return returnValue
  }

  updatePaperStatus(data: { assignmentId: number, status: String }) {
    this.updatePaperStatusEvent.emit(data)
  }
}
