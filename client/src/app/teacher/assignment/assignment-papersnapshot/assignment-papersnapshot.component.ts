import { ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output, Inject } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { PaperSnapshot } from 'src/app/models/papersnapshot.model';
import { formatDate } from '@angular/common'
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { SolutionFormData } from 'src/app/models/formPaperSnapshotData.model';
import { DomSanitizer } from '@angular/platform-browser';
import { MatDialog, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Paper } from 'src/app/models/paper.model';
import { BehaviorSubject } from 'rxjs';
import { Assignment } from 'src/app/models/assignment.model';


@Component({
  selector: 'app-assignment-papersnapshot',
  templateUrl: './assignment-papersnapshot.component.html',
  styleUrls: ['./assignment-papersnapshot.component.sass']
})
export class AssignmentPapersnapshotComponent implements OnInit {

  imageSrc: ArrayBuffer;
  formGroup: FormGroup;
  _currentPaper = new BehaviorSubject<Paper>(null);
  currentAssignment: Assignment;
  toReviewControl = new FormControl({value: false, disabled: false})
  voteControl = new FormControl({ value: 1, disabled: this.toReviewControl.value }, [Validators.min(1), Validators.max(30)])
  solutionFileControl = new FormControl({value: null, disabled: !this.toReviewControl.value}, [Validators.required])
  solutionFileSourceControl = new FormControl({value: null, disabled: !this.toReviewControl.value}, [Validators.required])

  constructor(
    private formBuilder: FormBuilder,
    private changeDetector: ChangeDetectorRef,
    private domSanitizer: DomSanitizer,
    public dialog: MatDialog
  ) {
    this.formGroup = formBuilder.group({
      toReview: this.toReviewControl,
      vote: this.voteControl,
      solutionFile: this.solutionFileControl,
      solutionFileSource: this.solutionFileSourceControl
    })
  }

  colsToDisplay = ["submissionDate", "type", "content"]
  dataSource = new MatTableDataSource<PaperSnapshot>()

  @Input() set papersnapshotsData(papersnapshots: PaperSnapshot[]) {
    if (papersnapshots != null)
      this.dataSource.data = papersnapshots
  }

  @Input() set paper(paper: Paper) {
    if (paper != null) {
      this._currentPaper.next(paper)
      if (paper.status === "closed") {
        const toReviewControl = this.formGroup.get("toReview")
        toReviewControl.setValue(false)
        toReviewControl.disable()
      }
    }
  }

  @Input() set assignment(assignment: Assignment) {
    if(assignment != null) {
      this.currentAssignment = assignment
    }
  }

  get paper() {
    return this._currentPaper.getValue()
  }

  @Output() submitSolutionEvent = new EventEmitter<SolutionFormData>();

  format(date) {
    return formatDate(date, 'EEEE, MMMM d, y, h:mm:ss a', 'en-US', 'GMT+1')
  }

  ngOnInit(): void {
    this._currentPaper.subscribe()
  }

  ngOnDestroy(): void {
    this._currentPaper.unsubscribe()
  }

  toggleVote(event: MatCheckboxChange) {
    const voteControl = this.formGroup.get("vote")
    const solutionFileControl = this.formGroup.get("solutionFile")
    const solutionFileSourceControl = this.formGroup.get("solutionFileSource")
    if (event.checked) { //toReview
      voteControl.disable()
      solutionFileControl.enable()
      solutionFileSourceControl.enable()
    } else { // final vote
      voteControl.enable()
      solutionFileControl.disable()
      solutionFileSourceControl.disable()
    }
  }

  onFileChange(event) {

    let reader = new FileReader();

    if (event.target.files && event.target.files.length) {
      const [file] = event.target.files
      reader.readAsDataURL(file)
      reader.onload = () => {
        this.imageSrc = reader.result as ArrayBuffer;
        this.formGroup.patchValue({
          solutionFileSource: reader.result
        })
        this.changeDetector.markForCheck()
      }
    }
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

  canUploadSolution() {
    return (
      this.paper && 
      this.paper.vote === null && 
      ((this.paper.status === 'submitted') || (this.paper.status === 'closed'))
      )
  }

  toReviewDisabled() {
    return (this.paper && this.paper.status === 'closed') 
  }

  onSubmit(): void {

    //const splittedPath = this.formGroup.controls["solutionFile"].value.split("\\")

    const papersnapshot: PaperSnapshot = {
      id: null,
      submissionDate: new Date(),
      type: "correction",
      content: this.formGroup.controls["solutionFileSource"].value
    }

    const solutionFormData: SolutionFormData = {
      toReview: this.formGroup.controls["toReview"].value,
      vote: this.formGroup.controls["vote"].value,
      papersnapshot: papersnapshot
    }

    this.submitSolutionEvent.emit(solutionFormData)
  }

}

@Component({
  selector: "image-dialog",
  templateUrl: "../image-dialog.html"
})
export class ImageDialog {
  constructor(@Inject(MAT_DIALOG_DATA) public data: any) {

  }
}
