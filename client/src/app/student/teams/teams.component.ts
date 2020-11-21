import { Component, Input, OnInit } from "@angular/core";
import { Team } from "src/app/models/team.model";
import * as _ from "lodash";
import { Student } from "src/app/models/student.model";
import { Course } from "src/app/models/course.model";

@Component({
  selector: "app-teams",
  templateUrl: "./teams.component.html",
  styleUrls: ["./teams.component.sass"],
})
export class TeamsComponent implements OnInit {
  _teams: Team[];
  hasTeam = false;
  team: Team;
  @Input() studentId: String;
  @Input() currentCourse: Course;
  @Input() studentsNotInTeams: Student[];
  @Input() set teams(teams: Team[]) {
    if (teams != null) {
      this._teams = teams;

      // has activated team
      this.team = _.find(this._teams, (team) => team.status === 1);
      if (this.team != null) {
        this.hasTeam = true;
        console.log("User have a team: ", this.team);
      } else {
        this.hasTeam = false;
      }

      console.log("Retrieved student teams", this._teams);
    }
  }

  constructor() {}

  ngOnInit(): void {}
}
