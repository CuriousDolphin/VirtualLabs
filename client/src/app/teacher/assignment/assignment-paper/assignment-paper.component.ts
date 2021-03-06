import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Paper } from 'src/app/models/paper.model';
import { formatDate } from '@angular/common'
import { MatSort, Sort } from "@angular/material/sort";

@Component({
  selector: 'app-assignment-paper',
  templateUrl: './assignment-paper.component.html',
  styleUrls: ['./assignment-paper.component.sass']
})
export class AssignmentPaperComponent implements OnInit {

  constructor() { }

  @ViewChild(MatSort, {static: true}) sort: MatSort

  colsToDisplay = ["studentId", "studentName", "studentLastName", "status", "lastUpdateTime" ]
  dataSource = new MatTableDataSource<Paper>()
  @Input() set papersData(papers: Paper[]) {
    if( papers != null) {
      this.dataSource.data = papers
    }
  }
  filterValues: any = {};
  unread: boolean;
  submitted: boolean;
  toReview: boolean;

  @Output() clickPaperEvent = new EventEmitter<number>()

  sortColumn($event: Sort): void {
    this.dataSource.sortingDataAccessor = (item, property) => {
      switch(property) {
        case "student.id": {
          return item.student.id
        }
      }
    }
  }

  clickPaper(paperId: number) {
    this.clickPaperEvent.emit(paperId)
  }

  format(date) {
    return formatDate(date, 'EEEE, MMMM d, y, h:mm:ss a', 'en-US', 'GMT+1')
  }

  ngOnInit(): void {
    this.dataSource.filterPredicate = ((data: Paper, filter: string): boolean => {
        const filterValues = JSON.parse(filter);

        return false //to modify
    })

    this.dataSource.sort = this.sort;
  }

}
