import { Component, EventEmitter, Inject, Input, OnInit, Output, ViewChild } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Assignment } from 'src/app/models/assignment.model';
import { formatDate } from '@angular/common'
import { DomSanitizer } from '@angular/platform-browser';
import { MatDialog, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { StudentAssignment } from 'src/app/models/studentAssignment.model';

@Component({
  selector: 'app-student-assignment-assignment',
  templateUrl: './student-assignment-assignment.component.html',
  styleUrls: ['./student-assignment-assignment.component.sass']
})
export class StudentAssignmentAssignmentComponent implements OnInit {

  constructor( 
    private domSanitizer: DomSanitizer,
    public dialog: MatDialog) { }

  ngOnInit(): void {
    this.dataSource.sort = this.sort
  }

  @ViewChild(MatSort, { static: true }) sort: MatSort

  colsToDisplay = ["title", "content", "status", "vote", "releaseDate", "expiryDate", "openAssignment"]
  dataSource = new MatTableDataSource<StudentAssignment>();

  @Input() set assignmentsData(assignments: StudentAssignment[]) {
    if (assignments != null) {
      this.dataSource.data = assignments;
    }
  }
  @Output() confirmReadClickedEvent = new EventEmitter<{assignmentId: number, status: String}>();
  @Output() clickAssignmentEvent = new EventEmitter<number>();

  format(date) {
    return formatDate(date, 'EEEE, MMMM d, y, h:mm:ss a', 'en-US', 'GMT+1')
  }

  renderTrustImage(base64: string) {
    return this.domSanitizer.bypassSecurityTrustUrl(base64)
  }

  clickAssignment(assignment: StudentAssignment) {
    if(assignment.status === "null" || assignment.status === "reviewed") {
      this.openImage(assignment.content, assignment.id, assignment.status)
    } else {
      this.clickAssignmentEvent.emit(assignment.id)
    }
  }

  openImage(src, assignmentId: number, status: String) {
    const dialogRef = this.dialog.open(StudentImageDialog, {
      data: {
        src: this.renderTrustImage(src),
        status: status
      }
    })

    const clickConfirmReadEventSubscription =  dialogRef.componentInstance.clickConfirmReadEvent.subscribe(result => {
      const data = {
        assignmentId: assignmentId,
        status: "readed"
      }
      this.dialog.closeAll()
      this.confirmReadClickedEvent.emit(data)
    })

    dialogRef.afterClosed().subscribe(result => {      
      clickConfirmReadEventSubscription.unsubscribe()
    });
  }
}

@Component({
  selector: "student-image-dialog",
  templateUrl: "../student-image-dialog.html"
})
export class StudentImageDialog {
  constructor(@Inject(MAT_DIALOG_DATA) public data: any) {

  }

  @Output() clickConfirmReadEvent = new EventEmitter<number>()

  onConfirmClick() {
    this.clickConfirmReadEvent.emit()
  }
}
