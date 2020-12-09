import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Paper } from 'src/app/models/paper.model';
import { formatDate } from '@angular/common'
import { MatSort } from "@angular/material/sort";

@Component({
  selector: 'app-assignment-paper',
  templateUrl: './assignment-paper.component.html',
  styleUrls: ['./assignment-paper.component.sass']
})
export class AssignmentPaperComponent implements OnInit {

  constructor() { }

  @ViewChild(MatSort, {static: true}) sort: MatSort

  colsToDisplay = ["studentId", "studentName", "studentLastName", "status", "lastUpdateTime", ]
  dataSource = new MatTableDataSource<Paper>()
  @Input() set papersData(papers: Paper[]) {
    if( papers != null) {
      this.dataSource.data = papers
    }
  }

  format(date) {
    return formatDate(date, 'yyyy-MM-dd hh:mm:ss', 'en', 'GMT')
  }

  ngOnInit(): void {
    this.dataSource.sort = this.sort;
  }

}
