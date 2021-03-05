import { ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output, Inject} from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { PaperSnapshot } from 'src/app/models/papersnapshot.model';
import { formatDate } from '@angular/common'
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { SolutionFormData } from 'src/app/models/formData.model';
import { DomSanitizer } from '@angular/platform-browser';
import { MatDialog, MAT_DIALOG_DATA } from '@angular/material/dialog';


@Component({
  selector: 'app-assignment-papersnapshot',
  templateUrl: './assignment-papersnapshot.component.html',
  styleUrls: ['./assignment-papersnapshot.component.sass']
})
export class AssignmentPapersnapshotComponent implements OnInit {

  imageSrc: ArrayBuffer;
  formGroup: FormGroup;
  toReviewControl = new FormControl(true)
  voteControl = new FormControl({ value: 1, disabled: this.toReviewControl.value }, [Validators.min(1), Validators.max(30)])
  solutionFileControl = new FormControl(null, [Validators.required])
  solutionFileSourceControl = new FormControl(null, [Validators.required])
  
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

  @Output() submitSolutionEvent = new EventEmitter<SolutionFormData>();

  format(date) {
    return formatDate(date, 'EEEE, MMMM d, y, h:mm:ss a', 'en-US', 'GMT+1')
  }

  ngOnInit(): void {
  }

  toggleVote(event: MatCheckboxChange) {
    const control = this.formGroup.get("vote")
    event.checked ? control.disable() : control.enable()
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

  onSubmit(): void {


    const splittedPath = this.formGroup.controls["solutionFile"].value.split("\\")

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

    this.submitSolutionEvent.emit(solutionFormData)
  }

}

@Component({
  selector: "image-dialog",
  templateUrl: "image-dialog.html"
})
export class ImageDialog {
  constructor(@Inject(MAT_DIALOG_DATA) public data: object) {

  }
}
