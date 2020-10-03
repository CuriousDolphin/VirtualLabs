import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { StudentsComponent } from './teacher/students/students.component';
import { VmsComponent } from './teacher/vms/vms.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { StudentsContComponent } from './teacher/students-cont/students-cont.component';
import { AuthGuard } from './auth/auth.guard';
import { TeacherComponent } from './teacher/teacher.component';
import { TabContComponent } from './teacher/tab-cont/tab-cont.component';
const routes: Routes = [
  { path: 'home', component: HomeComponent },
  { path: '', redirectTo: 'home', pathMatch: 'full' },

  {
    path: 'teacher',
    component:TeacherComponent,
    children: [
      {
        path:':id_course',
        component:TabContComponent,
        children:[
          {
            path: 'students',
            component: StudentsContComponent,
            canActivate: [AuthGuard],
          },
          {
            path: 'vms',
            component: VmsComponent,
            canActivate: [AuthGuard],
          },
        ]
      },
      /* {
        path:':id/**',
        component: TabContComponent,

        canActivate: [AuthGuard],


      }, */
      
    ],
    canActivate: [AuthGuard],
  },

  { path: '**', component: PageNotFoundComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { relativeLinkResolution: 'legacy' })],
  exports: [RouterModule],
})
export class AppRoutingModule {}
