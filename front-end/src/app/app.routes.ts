import { Routes } from '@angular/router';
import { CustomerFormComponent } from '../components/customer-form/customer-form.component';
import { ListPage } from '../components/list-page/list-page';
import { LandingPage } from '../pages/landing-page/landing-page';
import { ReportPage } from '../components/report-page/report-page';
import { AuthGuard } from './auth/guards/auth.guard';
import { NonAuthGuard } from './auth/guards/non-auth.guard';

export const routes: Routes = [
  {
    path: '',
    component: LandingPage,
    canActivate: [NonAuthGuard],
  },
  {
    path: 'auth',
    loadChildren: () => import('./auth/auth.module').then(m => m.AuthModule),
    canActivate: [NonAuthGuard],
  },
  {
    path: 'home',
    loadComponent: () => import('../pages/home/home').then(c => c.Home),
    canActivate: [AuthGuard],
  },
  { path: 'list/customer', component: ListPage },
  { path: 'list/wallet', component: ListPage },
  { path: 'customers/new', component: CustomerFormComponent },
  { path: 'customers/edit/:id', component: CustomerFormComponent },
  { path: 'report', component: ReportPage },
  { path: '**', redirectTo: '' },
];
