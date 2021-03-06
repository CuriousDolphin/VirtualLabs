import { BrowserModule } from "@angular/platform-browser";
import { NgModule } from "@angular/core";
import { HttpClientModule, HTTP_INTERCEPTORS } from "@angular/common/http";

import { AppRoutingModule } from "./app-routing.module";
import { AppComponent } from "./app.component";
import { MatToolbarModule } from "@angular/material/toolbar";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { MatIconModule } from "@angular/material/icon";
import { MatSidenavModule } from "@angular/material/sidenav";
import { MatTabsModule } from "@angular/material/tabs";
import { MatListModule } from "@angular/material/list";
import { MatButtonModule } from "@angular/material/button";
import { MatTableModule } from "@angular/material/table";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatAutocompleteModule } from "@angular/material/autocomplete";
import { MatInputModule } from "@angular/material/input";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatSortModule } from "@angular/material/sort";
import { MatPaginatorModule } from "@angular/material/paginator";
import { StudentsComponent } from "./teacher/students/students.component";
import { HomeComponent } from "./home/home.component";
import { PageNotFoundComponent } from "./page-not-found/page-not-found.component";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatDialogModule } from "@angular/material/dialog";
import { LoginDialogComponent } from "./auth/login-dialog/login-dialog.component";
import { TokenInterceptor } from "./auth/token.interceptor";
import { MatSnackBarModule } from "@angular/material/snack-bar";
import { TeacherComponent } from "./teacher/teacher.component";
import { CourseDashboard } from "./teacher/course-dashboard/course-dashboard";
import { MatChipsModule } from "@angular/material/chips";
import { FlexLayoutModule } from "@angular/flex-layout";
import { CourseDialogComponent } from "./teacher/course-dialog/course-dialog.component";
import { MatSlideToggleModule } from "@angular/material/slide-toggle";
import { ToastComponent } from "./utils/toast/toast.component";
import { MatTooltipModule } from "@angular/material/tooltip";
import { MatCardModule } from "@angular/material/card";
import { StudentComponent } from "./student/student.component";
import { StudentCourseDashboard } from "./student/student-course-dashboard/student-course-dashboard";
import { RegisterDialogComponent } from "./auth/register-dialog/register-dialog.component";
import { TeamsComponent } from "./student/teams/teams.component";
import { MatExpansionModule } from "@angular/material/expansion";
import { StudentLandingPageComponent } from "./student/student-landing-page/student-landing-page.component";
import { MatMenuModule } from "@angular/material/menu";
import { TeacherLandingPageComponent } from './teacher/teacher-landing-page/teacher-landing-page.component';
import { AssignmentComponent } from './teacher/assignment/assignment.component';
import { AssignmentAssignmentComponent } from './teacher/assignment/assignment-assignment/assignment-assignment.component';
import { AssignmentPaperComponent } from './teacher/assignment/assignment-paper/assignment-paper.component';
import { AssignmentPapersnapshotComponent } from './teacher/assignment/assignment-papersnapshot/assignment-papersnapshot.component';
import { VmsStudentComponent } from './student/vms-student/vms-student.component';
import { MatGridListModule } from '@angular/material/grid-list';
import {MatRadioModule} from '@angular/material/radio';
import { DialogCreateVmComponent } from './student/vms-student/dialog-create-vm/dialog-create-vm.component';
import { DialogEditVmComponent } from './student/vms-student/dialog-edit-vm/dialog-edit-vm.component';
//import { OpenVmComponent } from './student/vms-student/open-vm/open-vm.component';
import { VmsTeacherComponent } from "./teacher/vms-teacher/vms-teacher.component";
import { DialogEditModelComponent } from "./teacher/vms-teacher/dialog-edit-model/dialog-edit-model.component";
import { DialogConfirmDeleteComponent } from "./student/vms-student/dialog-confirm-delete/dialog-confirm-delete.component";
import { OpenVmComponent } from "./open-vm/open-vm.component";

@NgModule({
  declarations: [
    AppComponent,
    StudentsComponent,
    HomeComponent,
    PageNotFoundComponent,
    LoginDialogComponent,
    TeacherComponent,
    CourseDashboard,
    CourseDialogComponent,
    RegisterDialogComponent,
    ToastComponent,
    StudentComponent,
    StudentCourseDashboard,
    TeamsComponent,
    StudentLandingPageComponent,
    TeacherLandingPageComponent,
    AssignmentComponent,
    AssignmentAssignmentComponent,
    AssignmentPaperComponent,
    AssignmentPapersnapshotComponent,
    VmsStudentComponent,
    DialogCreateVmComponent,
    DialogEditVmComponent,
    OpenVmComponent,
    VmsTeacherComponent,
    DialogEditModelComponent,
    DialogConfirmDeleteComponent,
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    MatToolbarModule,
    BrowserAnimationsModule,
    MatIconModule,
    MatSidenavModule,
    MatTabsModule,
    MatListModule,
    MatButtonModule,
    MatTableModule,
    MatCheckboxModule,
    MatAutocompleteModule,
    MatInputModule,
    FormsModule,
    ReactiveFormsModule,
    MatSortModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatSnackBarModule,
    MatChipsModule,
    FlexLayoutModule,
    MatSlideToggleModule,
    MatTooltipModule,
    MatCardModule,
    MatExpansionModule,
    MatMenuModule,
    MatGridListModule,
    MatRadioModule,
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true,
    },
  ],
  bootstrap: [AppComponent],
  entryComponents: [LoginDialogComponent, RegisterDialogComponent,OpenVmComponent,DialogEditModelComponent,DialogConfirmDeleteComponent],
})
export class AppModule {}
