import { Component, Input, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { PaperSnapshot } from 'src/app/models/papersnapshot.model';
import { formatDate } from '@angular/common'
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';


@Component({
  selector: 'app-assignment-papersnapshot',
  templateUrl: './assignment-papersnapshot.component.html',
  styleUrls: ['./assignment-papersnapshot.component.sass']
})
export class AssignmentPapersnapshotComponent implements OnInit {

  formGroup: FormGroup;
  toReview = new FormControl(true)
  vote = new FormControl({value:0, disabled: this.toReview.value})
  solution = new FormControl("")

  constructor(
    private formBuilder: FormBuilder
  ) {
    this.formGroup = formBuilder.group({
      toReview: this.toReview,
      vote: this.vote,
      solution: this.solution
    })
  }

  colsToDisplay = ["content", "submissionDate"]
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



}
