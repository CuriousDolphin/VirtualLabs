import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Course } from '../models/course.model';

@Injectable({
  providedIn: 'root',
})
export class UtilsService {
  private _toggleMenu$: BehaviorSubject<void> = new BehaviorSubject<void>(null);
  toggleMenu$ = this._toggleMenu$.asObservable();

  private _reloadCurses$: BehaviorSubject<void> = new BehaviorSubject(null);

  reloadCurses$ = this._reloadCurses$.asObservable();

  constructor() { }

  public toggleMenu() {
    this._toggleMenu$.next(null);
  }

  public reloadCurses() {
    this._reloadCurses$.next(null)
  }
}
