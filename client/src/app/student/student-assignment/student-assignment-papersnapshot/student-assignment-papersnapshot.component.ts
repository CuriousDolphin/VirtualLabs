import { formatDate } from '@angular/common';
import { ChangeDetectorRef, Component, EventEmitter, Inject, Input, OnInit, Output, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { PaperSnapshot } from 'src/app/models/papersnapshot.model';
import { DomSanitizer } from '@angular/platform-browser';
import { MatSort } from '@angular/material/sort';
import { MatDialog, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { StudentAssignment } from 'src/app/models/studentAssignment.model';
import { StudentImageDialog } from '../student-assignment-assignment/student-assignment-assignment.component';

@Component({
  selector: 'app-student-assignment-papersnapshot',
  templateUrl: './student-assignment-papersnapshot.component.html',
  styleUrls: ['./student-assignment-papersnapshot.component.sass']
})
export class StudentAssignmentPapersnapshotComponent implements OnInit {

  constructor(
    private domSanitizer: DomSanitizer,
    private changeDetector: ChangeDetectorRef,
    public dialog: MatDialog) {
  }

  ngOnInit(): void {
    this.dataSource.sort = this.sort
  }

  @ViewChild(MatSort, { static: true }) sort: MatSort
  imageSrc: ArrayBuffer = null;
  currAssignment: StudentAssignment;
  dataSource = new MatTableDataSource<PaperSnapshot>();
  colsToDisplay = ["submissionDate", "type",  "content"]

  @Input() set paperSnapshotsData(paperSnapshots: PaperSnapshot[]) {
    if (paperSnapshots != null)
      this.dataSource.data = paperSnapshots
  }

  @Input() set currentAssignment(assignment: StudentAssignment) {
    if (assignment != null) {
      console.log(assignment)
      this.currAssignment = assignment
    }
      
  }

  @Output() submitSolutionEvent = new EventEmitter<PaperSnapshot>();

  format(date) {
    return formatDate(date, 'EEEE, MMMM d, y, h:mm:ss a', 'en-US', 'GMT+1')
  }

  renderTrustImage(base64: string) {
    return this.domSanitizer.bypassSecurityTrustUrl(base64)
  }

  onFileChange(event) {

    let reader = new FileReader();

    if (event.target.files && event.target.files.length) {
      const [file] = event.target.files
      reader.readAsDataURL(file)
      reader.onload = () => {
        this.imageSrc = reader.result as ArrayBuffer;
        //this.changeDetector.markForCheck()
      }
      reader.onerror = () => {
        this.imageSrc = null
      }
    }
  }

  onSubmit(): void {
    const papersnapshot: PaperSnapshot = {
      id: null,
      submissionDate: new Date(),
      type: "solution",
      content: this.imageSrc
    }
    this.submitSolutionEvent.emit(papersnapshot)
  }

  openImage(src) {
    const dialogRef = this.dialog.open(StudentImageDialog, {
      data: {
        src: this.renderTrustImage(src),
        status: "readed"
      }
    })
  }

  canUpload(): boolean {
    const value = (
      (this.currAssignment.status === "readed" || this.currAssignment.status === "reviewed")
      && new Date(this.currAssignment.expiryDate) > new Date())
    return value

  }
}
