import { Component, OnDestroy, OnInit } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { BehaviorSubject, combineLatest, Observable, Subscription, of } from "rxjs";
import { switchMap, tap, map, shareReplay } from "rxjs/operators";
import { Course } from "src/app/models/course.model";
import { CourseService } from "src/app/services/course.service";
import { StudentService } from "../../services/student.service";
import { ToastService } from "src/app/services/toast.service";
import { UtilsService } from "src/app/services/utils.service";
import { Team } from "src/app/models/team.model";
import { AuthService } from "src/app/auth/auth.service";
import { Student } from "src/app/models/student.model";
import { TeamProposal } from "src/app/models/teamProposal.model";
import { VmInstance } from 'src/app/models/vm-instance.model';

@Component({
  selector: "app-course-dashboard",
  templateUrl: "./student-course-dashboard.html",
  styleUrls: ["./student-course-dashboard.sass"],
})
export class StudentCourseDashboard implements OnInit, OnDestroy {
  currentPath = "";
  private _currentCourseName$: BehaviorSubject<string> = new BehaviorSubject(
    null
  );
  private _reloadCourse$: BehaviorSubject<void> = new BehaviorSubject(null);
  private _reloadTeams$: BehaviorSubject<void> = new BehaviorSubject(null);

  private createTeamSubscription: Subscription;
  private confirmTeamSubscription: Subscription;
  private rejectTeamSubscription: Subscription;
  private courseSubscription: Subscription;
  private routeSubscription: Subscription;
  private deleteVmSubscription: Subscription;
  private startVmSubscription: Subscription;
  private stopVmSubscription: Subscription;
  currentCourse: Course;
  currentActiveTeam: String;
  currentCourse$: Observable<Course>;
  studentTeams$: Observable<Team[]>;
  studentVmInstances$: Observable<VmInstance[]>;
  studentsNotInTeams$: Observable<Student[]>;
  studentId: String;
  isLoading = false;
  constructor(
    private route: ActivatedRoute,
    private utilsService: UtilsService,
    private courseService: CourseService,
    private toastService: ToastService,
    private studentService: StudentService,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    console.log("student dashboard on init");
    this.studentId = this.authService.getUserId();
    this.currentActiveTeam = "";

    // take the name of the route (course name)
    this.routeSubscription = this.route.url.subscribe((evt) => {
      const name = evt[0].path;
      console.log("student course dashboard route change", "data", name);
      if (name) {
        this._currentCourseName$.next(name);
      }
    });
    // get current course after name changes and reload course is triggered
    this.currentCourse$ = combineLatest([
      this._currentCourseName$,
      this._reloadCourse$,
    ]).pipe(
      map(([courseName, reload]) => courseName),
      tap(() => (this.isLoading = true)),
      switchMap((courseName) => {
        if (courseName) return this.courseService.getCourse(courseName);
      }),
      tap((course) => {
        this.currentCourse = course;
      }),
      tap(() => (this.isLoading = false))
    );

    this.studentTeams$ = combineLatest([
      this._currentCourseName$,
      this._reloadTeams$,
    ]).pipe(
      tap(() => (this.isLoading = true)),
      map(([courseName, reload]) => courseName),
      switchMap((courseName) => {
        if (courseName)
          return this.studentService.getTeamsByStudentIdCourseName(
            this.authService.getUserId(),
            courseName
          );
      }),
      tap(() => (this.isLoading = false)),
      tap(val => {
        this.currentActiveTeam = "";
        if (val.filter(t => t.status === 1).length === 1) {
          this.currentActiveTeam = val.find(t => t.status === 1).name
        }
      })
    );

    this.studentsNotInTeams$ = combineLatest([
      this._currentCourseName$,
      this._reloadTeams$,
    ]).pipe(
      map(([courseName, reload]) => courseName),
      tap(
        () => ((this.isLoading = true), console.log("get student not in team"))
      ),
      switchMap((courseName) => {
        if (courseName)
          return this.courseService.getStudentsNotInTeam(courseName);
      }),
      tap(() => (this.isLoading = false))
    );

    this.studentVmInstances$ = combineLatest([
      this._currentCourseName$,
      this._reloadTeams$,
      this.studentTeams$
    ]).pipe(
      tap(() => (this.isLoading = true)),
      map(([courseName, reload]) => courseName),
      switchMap((courseName) => {
        if (courseName) {
          if (this.currentActiveTeam != "") {
            return this.studentService.getVmInstancesPerTeam(
              this.authService.getUserId(),
              this.currentActiveTeam
            );
          }
          else
            return of<VmInstance[]>([]);
        }
      }),
      tap(() => (this.isLoading = false))
    );

    /*combineLatest([
      this._currentCourseName$,
      this._reloadTeams$,
    ]).pipe(
      map(([courseName, reload]) => courseName),
      tap(
        () => ((this.isLoading = true), console.log("get vm instances"))
      ),
      switchMap((courseName) => {
        if (courseName)
          return this.studentService.getVmInstancesPerTeam(this.studentId, "Team1");
      }),
      tap(() => (this.isLoading = false))
    );*/

  }

  createTeam(proposal: TeamProposal) {
    if (this.createTeamSubscription) this.createTeamSubscription.unsubscribe();

    this.createTeamSubscription = this.courseService
      .proposeTeam(this.currentCourse.name, proposal)
      .subscribe(
        (data) => {
          this.toastService.success(
            "Team propose success ! Team members will be notified."
          );
          this._reloadTeams();
        },
        (error) => {
          this.toastService.error("Error in team propose, try again later");
          this._reloadTeams();
        }
      );
  }
  confirmTeam(team: Team) {
    if (this.confirmTeamSubscription)
      this.confirmTeamSubscription.unsubscribe();

    this.confirmTeamSubscription = this.courseService
      .confirmTeam(team.confirmation_token)
      .subscribe(
        (data) => {
          this.toastService.success("Team confirm success ! \n");
          this._reloadTeams();
        },
        (error) => {
          this.toastService.error(
            "Error in team confirmation, try again later \n" + error
          );
          this._reloadTeams();
        }
      );
  }
  rejectTeam(team: Team) {
    if (this.rejectTeamSubscription) this.rejectTeamSubscription.unsubscribe();

    this.rejectTeamSubscription = this.courseService
      .rejectTeam(team.confirmation_token)
      .subscribe(
        (data) => {
          this.toastService.success("Team confirm success ! \n");
          this._reloadTeams();
        },
        (error) => {
          this.toastService.error(
            "Error in team confirmation, try again later \n" + error
          );
          this._reloadTeams();
        }
      );
  }

  deleteVm(vm: VmInstance) {
    if (this.deleteVmSubscription) this.deleteVmSubscription.unsubscribe();
    
    this.deleteVmSubscription = this.studentService
      .deleteVm(this.authService.getUserId(), this.currentActiveTeam, vm)
      .subscribe(
        (data) => {
          this.toastService.success("VM deleted success ! \n");
          this._reloadTeams();
        },
        (error) => {
          this.toastService.error(
            "Error VM delete, try again later \n" + error
          );
          this._reloadTeams();
        }
      );
  }

  startVm(vm: VmInstance) {
    if (this.startVmSubscription) this.startVmSubscription.unsubscribe();

    this.startVmSubscription = this.studentService
    .startVm(this.authService.getUserId(), this.currentActiveTeam, vm)
    .subscribe(
      (data) => {
        this.toastService.success("VM started success ! \n"); //TODO: open VM image
        this._reloadTeams();
      },
      (error) => {
        this.toastService.error(
          "Error VM start, try again later \n" + error
        );
        this._reloadTeams();
      }
    );
  }

  stopVm(vm: VmInstance) {
    if (this.stopVmSubscription) this.stopVmSubscription.unsubscribe();

    this.stopVmSubscription = this.studentService
    .stopVm(this.authService.getUserId(), this.currentActiveTeam, vm)
    .subscribe(
      (data) => {
        this.toastService.success("VM stopped success ! \n"); //TODO: open VM image
        this._reloadTeams();
      },
      (error) => {
        this.toastService.error(
          "Error VM stop, try again later \n" + error
        );
        this._reloadTeams();
      }
    );
  }

  _reloadTeams() {
    this._reloadTeams$.next();
  }
  reloadData() {
    this._reloadCourse$.next();
    this._reloadTeams$.next();
  }

  ngOnDestroy(): void {
    if (this.courseSubscription) this.courseSubscription.unsubscribe();
    if (this.routeSubscription) this.routeSubscription.unsubscribe();
    if (this.createTeamSubscription) this.createTeamSubscription.unsubscribe();
    if (this.deleteVmSubscription) this.deleteVmSubscription.unsubscribe();
    if (this.startVmSubscription) this.startVmSubscription.unsubscribe();
    if (this.stopVmSubscription) this.stopVmSubscription.unsubscribe();
  }
}
