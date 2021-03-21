import { Component } from '@angular/core';
import { UtilsService } from 'src/app/services/utils.service';

@Component({
  selector: 'app-teacher-landing-page',
  templateUrl: './teacher-landing-page.component.html',
  styleUrls: ['./teacher-landing-page.component.sass']
})
export class TeacherLandingPageComponent  {

  constructor(private utilsService: UtilsService,) { }

  toggleForMenuClick() {
    this.utilsService.toggleMenu();
  }

}
