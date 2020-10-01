import {
  Component,
  ViewChild,
  OnInit,
  Input,
  Output,
  EventEmitter,
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
    if (students != null) this.allStudents = students;
  }
  allStudents = []; //for autocomplete
  @Output() deleteStudents = new EventEmitter<Student[]>();
  @Output() addStudent = new EventEmitter<Student>();
  myControl = new FormControl();
  filteredOptions: Observable<Student[]>;
  title = "ai20-lab05";
  colsToDisplay = ["select", "id", "name", "firstName"];
  selectedStudents = new SelectionModel<Student>(true, []);

  studentToAdd: Student = null;

  @ViewChild(MatSort, { static: true }) sort: MatSort;
  @ViewChild(MatPaginator, { static: true }) paginator: MatPaginator;
  ngOnInit(): void {
    this.dataSource.sort = this.sort;
    this.dataSource.paginator = this.paginator;
    this.filteredOptions = this.myControl.valueChanges.pipe(
      startWith(""),
      //debounceTime(200),
      //distinctUntilChanged(),
      map((value) => (typeof value === "string" ? value : value.firstName)),
      map((value) => this._filter(value))
    );
  }
  displayFn(student: Student): string {
    return student && student.firstName
      ? student.firstName + " " + student.id
      : "";
  }

  selectStudentToAdd(student: Student) {
    this.studentToAdd = student;
  }
  addStudentToTable() {
    if (
      this.studentToAdd &&
      _.findIndex(this.dataSource.data, this.studentToAdd) == -1
    ) {
      this.selectedStudents.clear();
      // emit here
      this.addStudent.emit(this.studentToAdd);
      this.studentToAdd = null;
    }
  }

  private _filter(value: string): Student[] {
    const tmp = value.toLowerCase();
    return _.chain(this.allStudents)
      .filter((student: Student) =>
        student.firstName.toLowerCase().includes(tmp)
      )
      .value();
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

  deleteSelectedStudent() {
    this.deleteStudents.emit(this.selectedStudents.selected);
    this.selectedStudents.clear();
  }
  toggleStudent(student: Student) {
    this.selectedStudents.toggle(student);
  }
}
