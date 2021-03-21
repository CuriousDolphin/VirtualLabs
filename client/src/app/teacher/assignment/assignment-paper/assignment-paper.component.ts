import { Component, EventEmitter, Input, OnInit, Output, ViewChild, Inject, ElementRef } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Paper } from 'src/app/models/paper.model';
import { formatDate } from '@angular/common'
import { MatSort, Sort } from "@angular/material/sort";
import { MatDialog, MAT_DIALOG_DATA } from '@angular/material/dialog';

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

  colsToDisplay = ["studentId", "studentName", "studentLastName", "status", "lastUpdateTime"]
  dataSource = new MatTableDataSource<Paper>()
  @Input() set papersData(papers: Paper[]) {
    if (papers != null) {
      this.dataSource.data = papers
    }
  }

  unreaded: boolean = true;
  readed: boolean = true;
  submitted: boolean = true;
  reviewed: boolean = true;

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
        reviewed: this.reviewed
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

      const matchFilter = []

      unreaded != "" ? matchFilter.push(data.status.toLowerCase().includes(unreaded)) : null
      readed != "" ? matchFilter.push(data.status.toLowerCase().includes(readed)) : null
      submitted != "" ? matchFilter.push(data.status.toLowerCase().includes(submitted)) : null
      reviewed != "" ? matchFilter.push(data.status.toLowerCase().includes(reviewed)) : null

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
    const unreaded = this.unreaded ? "null" : ""
    const readed = this.readed ? "readed" : ""
    const submitted = this.submitted ? "submitted" : ""
    const reviewed = this.reviewed ? "reviewed" : ""
    const separator = "$"


    const filterValue = unreaded + separator + readed + separator + submitted + separator + reviewed + separator
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
