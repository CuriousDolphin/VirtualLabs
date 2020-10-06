import { Component, OnInit, Inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Subscription } from 'rxjs';
import { Course } from 'src/app/models/course.model';
import { CourseService } from 'src/app/services/course.service';

@Component({
  selector: 'app-course-dialog',
  templateUrl: './course-dialog.component.html',
  styleUrls: ['./course-dialog.component.sass']
})
export class CourseDialogComponent implements OnInit {
  courseForm: FormGroup;
  isLoading = false;
  showError = false;

  courseSubscription: Subscription;


  constructor(private courseService: CourseService,
    public dialogRef: MatDialogRef<CourseDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    public fb: FormBuilder) {

    this.courseForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      acronym: ['', [Validators.required, Validators.minLength(2)]],
      enabled: true,
      min: 0,
      max: 5,
    });

  }

  ngOnInit(): void {
  }

  cancel() {
    this.courseForm.reset();
    this.dialogRef.close(false);
  }

  createCourse() {
    if (this.courseSubscription) this.courseSubscription.unsubscribe();
    this.isLoading = true;

    if (this.courseForm.valid) {
      const course: Course = {
        name: this.courseForm.get('name').value,
        acronym: this.courseForm.get('acronym').value,
        enabled: this.courseForm.get('enabled').value,
        min: this.courseForm.get('min').value,
        max: this.courseForm.get('max').value,
      };
      this.courseSubscription = this.courseService.addCourse(course).subscribe((evt) => {
        this.isLoading = false;
        if (evt == null) {
          //  failed
          this.showError = true;
        } else {
          this.dialogRef.close(true);
        }
      })
    }
  }

}
