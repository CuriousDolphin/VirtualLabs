import { Injectable } from "@angular/core";
import { BehaviorSubject } from "rxjs";
import { Course } from "../models/course.model";

@Injectable({
  providedIn: "root",
})
export class UtilsService {
  private _toggleMenu$: BehaviorSubject<void> = new BehaviorSubject<void>(null);
  toggleMenu$ = this._toggleMenu$.asObservable();

  private _currentCourse$: BehaviorSubject<Course> = new BehaviorSubject<
    Course
  >(null);
  currentCourse$ = this._currentCourse$.asObservable();

  constructor() {}

  public toggleMenu() {
    this._toggleMenu$.next(null);
  }

  public setCurrentCourse(course?: Course) {
    this._currentCourse$.next(course);
  }
}
