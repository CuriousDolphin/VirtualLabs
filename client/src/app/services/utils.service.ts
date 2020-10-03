import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class UtilsService {
  private _toggleMenu$: BehaviorSubject<void> = new BehaviorSubject<void>(null);
  toggleMenu$ = this._toggleMenu$.asObservable();

  constructor() {
   }

   public toggleMenu(){
     this._toggleMenu$.next(null);
   }
}
