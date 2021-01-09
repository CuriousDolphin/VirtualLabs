import { ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { PaperSnapshot } from 'src/app/models/papersnapshot.model';
import { formatDate } from '@angular/common'
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { SolutionFormData } from 'src/app/models/formData.model';


@Component({
  selector: 'app-assignment-papersnapshot',
  templateUrl: './assignment-papersnapshot.component.html',
  styleUrls: ['./assignment-papersnapshot.component.sass']
})
export class AssignmentPapersnapshotComponent implements OnInit {

  imageSrc: string;
  formGroup: FormGroup;
  toReviewControl = new FormControl(true)
  voteControl = new FormControl({ value: 1, disabled: this.toReviewControl.value }, [Validators.min(1), Validators.max(30)])
  solutionFileControl = new FormControl(null, [Validators.required])
  solutionFileSourceControl = new FormControl(null, [Validators.required])
  imageLink: String = "http://localhost:8080/VM_images/defaultVmImage.png"

  constructor(
    private formBuilder: FormBuilder,
    private changeDetector: ChangeDetectorRef
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
    return formatDate(date, 'yyyy-MM-dd hh:mm:ss', 'en', 'GMT')
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
        this.imageSrc = reader.result as string;
        this.formGroup.patchValue({
          solutionFileSource: reader.result
        })
        this.changeDetector.markForCheck()
      }
    }
  }

  openImage() {
    console.log("apro Immagine")
  }

  onSubmit(): void {


    const splittedPath = this.formGroup.controls["solutionFile"].value.split("\\")

    const papersnapshot: PaperSnapshot = {
      id: null,
      submissionDate: new Date(),
      content: splittedPath[splittedPath.length - 1]
    }

    const solutionFormData: SolutionFormData = {
      toReview: this.formGroup.controls["toReview"].value,
      vote: this.formGroup.controls["vote"].value,
      imgSource: this.formGroup.controls["solutionFileSource"].value,
      papersnapshot: papersnapshot
    }

    this.submitSolutionEvent.emit(solutionFormData)
  }

}
