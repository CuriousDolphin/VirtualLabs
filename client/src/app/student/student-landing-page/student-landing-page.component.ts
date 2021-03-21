import { Component } from '@angular/core';
import { UtilsService } from 'src/app/services/utils.service';

@Component({
  selector: 'app-student-landing-page',
  templateUrl: './student-landing-page.component.html',
  styleUrls: ['./student-landing-page.component.sass']
})
export class StudentLandingPageComponent  {

  constructor(private utilsService: UtilsService,) { }


  toggleForMenuClick() {
    this.utilsService.toggleMenu();
  }
}
