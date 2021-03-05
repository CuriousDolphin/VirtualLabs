import { NgModule } from "@angular/core";
import { Routes, RouterModule } from "@angular/router";
import { HomeComponent } from "./home/home.component";
import { PageNotFoundComponent } from "./page-not-found/page-not-found.component";
import { TeacherComponent } from "./teacher/teacher.component";
import { CourseDashboard } from "./teacher/course-dashboard/course-dashboard";
import { TeacherGuard } from "./auth/teacher.guard";
import { StudentComponent } from "./student/student.component";
import { StudentGuard } from "./auth/student.guard";
import { StudentCourseDashboard } from "./student/student-course-dashboard/student-course-dashboard";
import { StudentLandingPageComponent } from "./student/student-landing-page/student-landing-page.component";
import { TeacherLandingPageComponent } from "./teacher/teacher-landing-page/teacher-landing-page.component";
import { HomeGuard } from "./auth/home.guard";
const routes: Routes = [
  { path: "home", component: HomeComponent, canActivate: [HomeGuard] }, //homeguard fa il redirect alla pagina dello studente se e' loggato uno studente e viceversa.
  { path: "", redirectTo: "home", pathMatch: "full" },

  {
    path: "teacher",
    component: TeacherComponent,
    children: [
      { path: "", component: TeacherLandingPageComponent },
      {
        path: ":id_course",
        component: CourseDashboard,
        canActivate: [TeacherGuard],
      },
    ],
    canActivate: [TeacherGuard],
  },
  {
    path: "student",
    component: StudentComponent,
    children: [
      {
        path: "",
        component: StudentLandingPageComponent,
      },
      {
        path: ":id_course",
        component: StudentCourseDashboard,
        canActivate: [StudentGuard],
      },
    ],
    canActivate: [StudentGuard],
  },

  { path: "**", component: PageNotFoundComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
