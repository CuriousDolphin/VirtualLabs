import { Component, Input, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Paper } from 'src/app/models/paper.model';

@Component({
  selector: 'app-assignment-paper',
  templateUrl: './assignment-paper.component.html',
  styleUrls: ['./assignment-paper.component.sass']
})
export class AssignmentPaperComponent implements OnInit {

  constructor() { }

  colsToDisplay = ["id", "status", "vote", "lastUpdateTime", "studentId", "studentName", "studentLastName"]
  dataSource = new MatTableDataSource<Paper>()
  @Input() set papersData(papers: Paper[]) {
    if( papers != null) {
      this.dataSource.data = papers
    }
  }

  ngOnInit(): void {
  }

}
