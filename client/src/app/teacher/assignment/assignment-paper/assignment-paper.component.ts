import { Component, EventEmitter, Input, OnInit, Output, ViewChild, Inject, ElementRef } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Paper } from 'src/app/models/paper.model';
import { formatDate } from '@angular/common'
import { MatSort, Sort } from "@angular/material/sort";
import { MatDialog, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Assignment } from 'src/app/models/assignment.model';

@Component({
  selector: 'app-assignment-paper',
  templateUrl: './assignment-paper.component.html',
  styleUrls: ['./assignment-paper.component.sass']
})
export class AssignmentPaperComponent implements OnInit {

  constructor(public dialog: MatDialog) { }

  @ViewChild(MatSort, { static: true }) sort: MatSort
  @ViewChild("filterIcon", {
    static: false,
    read: ElementRef
  }) filterIcon: ElementRef

  colsToDisplay = ["studentId", "studentName", "studentLastName", "status", "vote", "lastUpdateTime", "openPaper"]
  dataSource = new MatTableDataSource<Paper>()
  currentAssignment: Assignment

  @Input() set papersData(papers: Paper[]) {
    if (papers != null) {
      this.dataSource.data = papers
    }
  }

  @Input() set assignment(assignment: Assignment) {
    if (assignment != null) {
      this.currentAssignment = assignment
      //console.log("assignment: ",assignment)
    }


  }

  unreaded: boolean = true;
  readed: boolean = true;
  submitted: boolean = true;
  reviewed: boolean = true;
  finished: boolean = true
  closed: boolean = true;

  @Output() clickPaperEvent = new EventEmitter<number>()

  sortColumn($event: Sort): void {
    this.dataSource.sortingDataAccessor = (item, property) => {
      switch (property) {
        case "student.id": {
          return item.student.id
        }
      }
    }
  }

  clickPaper(paperId: number) {
    this.clickPaperEvent.emit(paperId)
  }

  openFilterDialog() {
    const filterIconOffset = this.filterIcon.nativeElement.getBoundingClientRect()
    const dialogRef = this.dialog.open(FilterDialog, {
      data: {
        unreaded: this.unreaded,
        readed: this.readed,
        submitted: this.submitted,
        reviewed: this.reviewed,
        finished: this.finished,
        closed: this.closed
      },
      position: {
        top: filterIconOffset.top + "px",
        bottom: filterIconOffset.bottom + "px",
        right: filterIconOffset.right + "px",
        left: filterIconOffset.left + "px"
      }
    })

    dialogRef.componentInstance.onChangeFilters.subscribe(data => {
      this.readed = data.readed
      this.unreaded = data.unreaded
      this.reviewed = data.reviewed
      this.submitted = data.submitted
      this.finished = data.finished
      this.closed = data.closed
      this.applyFilter()
    })

    dialogRef.afterClosed().subscribe(() => {
      dialogRef.componentInstance.onChangeFilters.unsubscribe()
    })
  }

  format(date) {
    return formatDate(date, 'EEEE, MMMM d, y, h:mm:ss a', 'en-US', 'GMT+1')
  }

  ngOnInit(): void {
    this.dataSource.filterPredicate = ((data: Paper, filter: string): boolean => {
      const filterArray = filter.split("$")

      //console.log(filter)
      const unreaded = filterArray[0] //has not read yet the assignment
      const readed = filterArray[1] //has read the assignment
      const submitted = filterArray[2] //has submitted the paper
      const reviewed = filterArray[3] //teacher has reviewed the paper
      const finished = filterArray[4] //teacher given vote 
      const closed = filterArray[5] //assignment expired

      const matchFilter = []

      unreaded != "" ? matchFilter.push(data.status.toLowerCase().includes(unreaded)) : null
      readed != "" ? matchFilter.push(data.status.toLowerCase().includes(readed)) : null
      submitted != "" ? matchFilter.push(data.status.toLowerCase().includes(submitted) && data.vote === null) : null
      reviewed != "" ? matchFilter.push(data.status.toLowerCase().includes(reviewed)) : null
      finished != "" ? matchFilter.push(data.status.toLowerCase().includes(finished) && data.vote !== null) : null
      closed != "" ? matchFilter.push(data.status.toLowerCase().includes(closed)) : null
      
      //return true it all values in array are true else return false
      return this.isFilterRow(matchFilter)

    })

    this.dataSource.sort = this.sort;
  }

  isFilterRow(matchFilter) {
    let returnValue: boolean = false;
    matchFilter.forEach(item => {
      returnValue = returnValue || item
    })
    return returnValue
  }

  applyFilter() {

    /*In this part i set the status for the filter  */
    const unreaded = this.unreaded ? "null" : ""
    const readed = this.readed ? "readed" : ""
    const submitted = this.submitted ? "submitted" : ""
    const reviewed = this.reviewed ? "reviewed" : ""
    const finished = this.finished ? "submitted" : ""
    const closed = this.closed ? "closed" : ""
    const separator = "$"


    const filterValue =
      unreaded + separator +
      readed + separator +
      submitted + separator +
      reviewed + separator +
      finished + separator +
      closed + separator
    this.dataSource.filter = filterValue.trim().toLowerCase()
  }
}


@Component({
  selector: "filter-dialog",
  templateUrl: "filter-dialog.html"
})
export class FilterDialog {
  constructor(@Inject(MAT_DIALOG_DATA) public data: any) {

  }

  @Output() onChangeFilters = new EventEmitter();

  emitChangeFilters() {
    this.onChangeFilters.emit(this.data)
  }
}
