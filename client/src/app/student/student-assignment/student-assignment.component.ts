import { Component, Input, OnInit, Output, EventEmitter } from '@angular/core';
import { StudentAssignment } from 'src/app/models/studentAssignment.model';

@Component({
  selector: 'app-student-assignment',
  templateUrl: './student-assignment.component.html',
  styleUrls: ['./student-assignment.component.sass']
})
export class StudentAssignmentComponent implements OnInit {

  toShowLevel: number
  assignmentsData = []
  papersData = []

  @Input() set studentAssignments(assignments: StudentAssignment[]) {
    if (assignments != null) {
      this.assignmentsData = assignments
    }
  }

  @Output() updatePaperStatusEvent = new EventEmitter<{assignmentId: number, status: String}>();

  constructor() { }

  ngOnInit(): void {
    this.toShowLevel = 0
  }

  back() {
    this.toShowLevel = this.toShowLevel - 1
  }

  updatePaperStatus(data: {assignmentId: number, status: String}) {
    this.updatePaperStatusEvent.emit(data)
  }
}
