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


@Component({
  selector: 'app-assignment-papersnapshot',
  templateUrl: './assignment-papersnapshot.component.html',
  styleUrls: ['./assignment-papersnapshot.component.sass']
})
export class AssignmentPapersnapshotComponent implements OnInit {

  imageSrc: ArrayBuffer;
  formGroup: FormGroup;
  currentPaper: Paper;
  toReviewControl = new FormControl({value: false, disabled: this.toReviewDisabled()})
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

  colsToDisplay = ["submissionDate", "content"]
  dataSource = new MatTableDataSource<PaperSnapshot>()

  @Input() set papersnapshotsData(papersnapshots: PaperSnapshot[]) {
    if (papersnapshots != null)
      this.dataSource.data = papersnapshots
  }

  @Input() set paper(paper: Paper) {
    if (paper != null) {
      //console.log(paper)
      this.currentPaper = paper
    }

  }

  @Output() submitSolutionEvent = new EventEmitter<SolutionFormData>();



  format(date) {
    return formatDate(date, 'EEEE, MMMM d, y, h:mm:ss a', 'en-US', 'GMT+1')
  }

  ngOnInit(): void {
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
      this.currentPaper.vote === null && 
      ((this.currentPaper.status === 'submitted') || (this.currentPaper.status === 'closed'))
      )
  }

  toReviewDisabled() {
    return (this.currentPaper && this.currentPaper.status === 'closed') 
  }

  onSubmit(): void {

    //const splittedPath = this.formGroup.controls["solutionFile"].value.split("\\")

    const papersnapshot: PaperSnapshot = {
      id: null,
      submissionDate: new Date(),
      content: this.formGroup.controls["solutionFileSource"].value
    }

    const solutionFormData: SolutionFormData = {
      toReview: this.formGroup.controls["toReview"].value,
      vote: this.formGroup.controls["vote"].value,
      papersnapshot: papersnapshot
    }

    //console.log(solutionFormData)

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
