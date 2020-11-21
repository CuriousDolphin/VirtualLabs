import { Component, EventEmitter, Input, OnInit, Output } from "@angular/core";
import { Team } from "src/app/models/team.model";
import * as _ from "lodash";
import { Student } from "src/app/models/student.model";
import { Course } from "src/app/models/course.model";
import { SelectionModel } from "@angular/cdk/collections";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { TeamProposal } from "src/app/models/teamProposal.model";

@Component({
  selector: "app-teams",
  templateUrl: "./teams.component.html",
  styleUrls: ["./teams.component.sass"],
})
export class TeamsComponent implements OnInit {
  _teams: Team[];
  hasTeam = false;
  team: Team;
  _userRequests: Team[];
  _pendingRequests: Team[];
  selectedStudents = new SelectionModel<Student>(true, []);
  teamForm: FormGroup;
  @Output() createTeam = new EventEmitter<TeamProposal>();
  @Output() confirmTeam = new EventEmitter<Team>();
  @Output() rejectTeam = new EventEmitter<Team>();
  @Input() studentId: string;
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
        this._userRequests = _.filter(
          this._teams,
          (team) => team.owner.id == this.studentId
        );
        this._pendingRequests = _.filter(
          this._teams,
          (team) => team.owner.id != this.studentId
        );
      }
      this.initPage();

      console.log("Retrieved student teams", this._teams);
    }
  }

  constructor(public fb: FormBuilder) {
    this.initPage();
  }
  initPage() {
    this.teamForm = this.fb.group({
      name: ["", [Validators.required, Validators.minLength(3)]],
      timeout: [
        1,
        [Validators.required, Validators.min(1), Validators.max(30)],
      ],
    });
    this.selectedStudents.clear();
  }
  _confirmTeam(team: Team) {
    this.confirmTeam.emit(team);
  }
  _rejectTeam(team: Team) {
    this.rejectTeam.emit(team);
  }

  ngOnInit(): void {}
  toggleStudent(student: Student) {
    this.selectedStudents.toggle(student);
  }

  isTeamConfirmedByStudent(studentId: string, team: Team) {
    if (team.members_status[studentId] == "Confirmed") return true;
    return false;
  }

  isCreateTeamDisabled() {
    if (
      this.currentCourse != null &&
      (this.selectedStudents.selected.length > this.currentCourse.max ||
        this.selectedStudents.selected.length < this.currentCourse.min)
    ) {
      return true;
    }

    if (this.teamForm.invalid) {
      return true;
    }
    return false;
  }
  emitProposal() {
    let tmp: TeamProposal = {
      name: this.teamForm.get("name").value,
      daysTimeout: this.teamForm.get("timeout").value,
      owner: this.studentId,
      members: [],
    };

    this.selectedStudents.selected.forEach((s: Student) =>
      tmp.members.push(s.id)
    );
    this.createTeam.emit(tmp);
  }
}
