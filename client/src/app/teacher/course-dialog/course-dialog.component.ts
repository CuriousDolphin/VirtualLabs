import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-course-dialog',
  templateUrl: './course-dialog.component.html',
  styleUrls: ['./course-dialog.component.sass']
})
export class CourseDialogComponent implements OnInit {
  courseForm: FormGroup;

  constructor(@Inject(MAT_DIALOG_DATA) public data: any, public fb: FormBuilder) {

    this.courseForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      acronym: ['', [Validators.required, Validators.minLength(3)]],
      enabled: true,
      min: 0,
      max: 10,
    });

  }

  ngOnInit(): void {
  }

}
