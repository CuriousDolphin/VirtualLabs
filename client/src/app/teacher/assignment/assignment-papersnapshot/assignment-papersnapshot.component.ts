import { Component, Input, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { PaperSnapshot } from 'src/app/models/papersnapshot.model';
import { formatDate } from '@angular/common'
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatCheckbox, MatCheckboxChange } from '@angular/material/checkbox';


@Component({
  selector: 'app-assignment-papersnapshot',
  templateUrl: './assignment-papersnapshot.component.html',
  styleUrls: ['./assignment-papersnapshot.component.sass']
})
export class AssignmentPapersnapshotComponent implements OnInit {

  formGroup: FormGroup;
  toReviewControl = new FormControl(true)
  voteControl = new FormControl({value: 1, disabled: this.toReviewControl.value}, [Validators.min(1), Validators.max(30)])
  solutionControl = new FormControl("", [Validators.required])
  solutionPath = ""
  imageLink: String = "http://localhost:8080/VM_images/defaultVmImage.png"

  constructor(
    private formBuilder: FormBuilder
  ) {
    this.formGroup = formBuilder.group({
      toReview: this.toReviewControl,
      vote: this.voteControl,
      solution: this.solutionControl
    })
  }

  colsToDisplay = ["content", "submissionDate", "solutionImg"]
  dataSource = new MatTableDataSource<PaperSnapshot>()

  @Input() set papersnapshotsData(papersnapshots: PaperSnapshot[]) {
    if (papersnapshots != null)
      this.dataSource.data = papersnapshots
  }

  format(date) {
    return formatDate(date, 'yyyy-MM-dd hh:mm:ss', 'en', 'GMT')
  }

  ngOnInit(): void {
  }

  toggleVote(event: MatCheckboxChange) {
    const control = this.formGroup.get("vote")
    event.checked ? control.disable() : control.enable()
  }

  changeInput() {
    this.solutionPath = this.formGroup.get("solution").value
  }

  onSubmit(): void {
    console.log(this.formGroup.value)
    // qua va scritto il codice per mandare il file al client
  }

}
