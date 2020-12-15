import { Component, Input, OnInit } from '@angular/core';
import { Team } from 'src/app/models/team.model';
import { VmModel } from 'src/app/models/vm-model.model';
import { VmInstance } from 'src/app/models/vm-instance.model';
import { BehaviorSubject } from 'rxjs';
import { Course } from 'src/app/models/course.model';
import * as _ from 'lodash';

@Component({
  selector: 'app-vms-teacher',
  templateUrl: './vms-teacher.component.html',
  styleUrls: ['./vms-teacher.component.sass']
})
export class VmsTeacherComponent implements OnInit {

  loadedVmModel$: BehaviorSubject<boolean> = new BehaviorSubject(false);
  loadedVmInstances$: BehaviorSubject<boolean> = new BehaviorSubject(false);
  courseAcronym$: BehaviorSubject<String> = new BehaviorSubject("");

  @Input() set teams(teams: Team[]) {
    this._teams = teams;
    if (this._teams !== null) {
      if (this._teams.length === 0)
        this.hasTeams = true;
      else
        this.hasTeams = false;
    }
  }
  @Input() set vmInstances(vmInstances: VmInstance[]) {
    this._vmInstances = vmInstances;
  }
  @Input() set vmModel(vmModel: VmModel) {
    this._vmModel = vmModel;
  }
  @Input() currentCourse: Course;

  _teams: Team[];
  _vmInstances: VmInstance[];
  _vmModel: VmModel;
  hasTeams = false;

  constructor() { }

  ngOnInit(): void {
  }

  ngOnChanges(): void {
    if (this._vmModel !== undefined && this._vmModel !== null) {
      this.loadedVmModel$.next(true);
    }
    if (this._vmInstances !== undefined && this._vmInstances !== null) {
      this.loadedVmInstances$.next(true);
    }
    if (this.currentCourse != undefined && this.currentCourse != null) {
      this.courseAcronym$.next(this.currentCourse.acronym)
    }
  }

  countRunningVms(vms: VmInstance[]): number{
    return _.sumBy(vms, (vm) => { return vm.state });
  }

  countVcpus(vms: VmInstance[]): number{
    return _.sumBy(vms, (vm) => { return vm.countVcpus });
  }

  countRam(vms: VmInstance[]): number{
    return _.sumBy(vms, (vm) => { return vm.countRam });
  }

  countDisk(vms: VmInstance[]): number{
    return _.sumBy(vms, (vm) => { return vm.countDisks });
  }

}
