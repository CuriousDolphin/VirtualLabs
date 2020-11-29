import {
  Component,
  ViewChild,
  OnInit,
  Input,
  Output,
  EventEmitter,
  ElementRef,
} from "@angular/core";
import { Student } from "../../models/student.model";
import { Observable } from "rxjs";
import { map, startWith } from "rxjs/operators";
import { SelectionModel } from "@angular/cdk/collections";
import { MatTableDataSource } from "@angular/material/table";
import { FormControl } from "@angular/forms";
import * as _ from "lodash";
import { MatSort } from "@angular/material/sort";
import { MatPaginator } from "@angular/material/paginator";

@Component({
  selector: "app-students",
  templateUrl: "./students.component.html",
  styleUrls: ["./students.component.sass"],
})
export class StudentsComponent implements OnInit {
  dataSource = new MatTableDataSource<Student>();
  @Input() set enrolledStudents(students: Student[]) {
    if (students != null) {
      console.log("set enrolled students", students);

      this.selectedStudents.clear();
      this.dataSource.data = students;
    }
  }
  @Input() set studentsDB(students: Student[]) {
    if (students != null) {
      this.allStudents = students;
      console.log("Retrieved all students", this.allStudents);
    }
  }
  allStudents = []; // for autocomplete
  @Output() deleteStudents = new EventEmitter<string[]>();
  @Output() addStudent = new EventEmitter<Student>();
  @Output() uploadCsv = new EventEmitter<any>();
  myControl = new FormControl();
  filteredOptions: Observable<Student[]>;
  colsToDisplay = ["select", "id", "name", "firstName", "teamName"];
  selectedStudents = new SelectionModel<Student>(true, []);

  studentToAdd: Student = null;

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  @ViewChild("uploader") myInputVariable: ElementRef;

  ngOnInit(): void {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    this.filteredOptions = this.myControl.valueChanges.pipe(
      startWith(""),
      // debounceTime(200),
      // distinctUntilChanged(),
      map((value) => (typeof value === "string" ? value : value.firstName)),
      map((value) => this._filter(value))
    );
  }
  displayFn(student: Student): string {
    return student && student.lastName ? student.name + " " + student.id : "";
  }
  uploadFile(event) {
    this.uploadCsv.emit(event[0]); // outputs the first file
    this.myInputVariable.nativeElement.value = ""; // reset input
  }
  selectStudentToAdd(student: Student) {
    console.log(student);
    this.studentToAdd = student;
  }
  addStudentToTable() {
    if (
      this.studentToAdd &&
      _.findIndex(this.dataSource.data, this.studentToAdd) === -1
    ) {
      this.selectedStudents.clear();
      // emit here
      this.addStudent.emit(this.studentToAdd);
      this.studentToAdd = null;
    }
  }

  private _filter(value: string): Student[] {
    if (value) {
      const tmp = value.toLowerCase();
      return _.chain(this.allStudents)
        .filter((student: Student) => student.name.toLowerCase().includes(tmp))
        .value();
    }
  }
  isAllSelected() {
    const numSelected = this.selectedStudents.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  masterToggle() {
    this.isAllSelected()
      ? this.selectedStudents.clear()
      : this.dataSource.data.forEach((student) =>
          this.selectedStudents.select(student)
        );
  }

  deleteSelectedStudents() {
    const arr: Array<string> = [];
    this.selectedStudents.selected.forEach((student: Student) =>
      arr.push(student.id)
    );
    this.deleteStudents.emit(arr);
    this.selectedStudents.clear();
  }
  toggleStudent(student: Student) {
    this.selectedStudents.toggle(student);
  }
}
