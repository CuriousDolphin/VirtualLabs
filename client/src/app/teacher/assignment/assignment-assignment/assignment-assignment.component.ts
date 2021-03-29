import { Component, OnInit, Input, EventEmitter, Output, ViewChild, Inject, ChangeDetectorRef } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Assignment } from 'src/app/models/assignment.model';
import { formatDate } from '@angular/common'
import { MatSort } from "@angular/material/sort";
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { DomSanitizer } from '@angular/platform-browser';
import { MatDialog, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-assignment-assignment',
  templateUrl: './assignment-assignment.component.html',
  styleUrls: ['./assignment-assignment.component.sass']
})
export class AssignmentAssignmentComponent implements OnInit {

  tomorrow: String;
  imageSrc: ArrayBuffer;
  formGroup: FormGroup;
  titleControl = new FormControl(null, [Validators.required])
  expiryDateControl = new FormControl(null, [Validators.required])
  assignmentFileControl = new FormControl(null, [Validators.required])
  assignmentFileSourceControl = new FormControl(null, [Validators.required])

  constructor(
    private formBuilder: FormBuilder,
    private changeDetector: ChangeDetectorRef,
    private domSanitizer: DomSanitizer,
    public dialog: MatDialog

  ) { 
    this.formGroup = formBuilder.group({
      title: this.titleControl,
      expiryDate: this.expiryDateControl,
      assignmentFile: this.assignmentFileControl,
      assignmentFileSource: this.assignmentFileSourceControl
    })
    const tmpDate = new Date()
    tmpDate.setDate(tmpDate.getDate() + 1)
    this.tomorrow = tmpDate.toJSON().split('T')[0]
  }

  @ViewChild(MatSort, { static: true }) sort: MatSort

  colsToDisplay = ["title", "content", "releaseDate", "expiryDate"]
  dataSource = new MatTableDataSource<Assignment>();
  @Input() set assignmentsData(assignments: Assignment[]) {
    if (assignments != null) {
      this.dataSource.data = assignments;
    }
  }

  @Output() clickAssignmentEvent = new EventEmitter<number>()
  @Output() submitAssignmentEvent = new EventEmitter<Assignment>();

  clickAssignment(assignmentId: number) {
    this.clickAssignmentEvent.emit(assignmentId);
  }

  renderTrustImage(base64: string) {
    return this.domSanitizer.bypassSecurityTrustUrl(base64)
  }

  openImage(src) {
    this.dialog.open(ImageDialog, {
      data: {
        src: this.renderTrustImage(src)
      }
    });
  }

  onFileChange(event) {
    let reader = new FileReader();

    if (event.target.files && event.target.files.length) {
      const [file] = event.target.files
      reader.readAsDataURL(file)
      reader.onload = () => {
        this.imageSrc = reader.result as ArrayBuffer;
        this.formGroup.patchValue({
          assignmentFileSource: reader.result
        })
        this.changeDetector.markForCheck()
      }
    }
  }

  format(date) {
    return formatDate(date, 'EEEE, MMMM d, y, h:mm:ss a', 'en-US', 'GMT+1')
  }

  ngOnInit(): void {
    this.dataSource.sort = this.sort
  }

  onSubmit(): void {

    const splittedPath = this.formGroup.controls["assignmentFile"].value.split("\\")

    const assignment: Assignment = {
      id: null,
      title: this.formGroup.controls["title"].value,
      releaseDate: new Date(),
      expiryDate: this.formGroup.controls["expiryDate"].value,
      content: this.formGroup.controls["assignmentFileSource"].value
    }

    this.submitAssignmentEvent.emit(assignment)
  }
}

@Component({
  selector: "image-dialog",
  templateUrl: "../image-dialog.html"
})
export class ImageDialog {
  constructor(@Inject(MAT_DIALOG_DATA) public data: object) {

  }
}
