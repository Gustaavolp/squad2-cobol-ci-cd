import { Injectable } from '@angular/core';
import {
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
  UrlTree,
  Router,
} from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service'; // Ajuste o caminho se necessário

@Injectable({
  providedIn: 'root',
})
export class NonAuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    if (this.authService.isAuthenticated()) {
      // Se o usuário já está autenticado, redireciona para a página home
      this.router.navigate(['/home']); // Ajuste '/home' se o nome da sua rota principal for diferente
      return false; // Impede o acesso à rota atual (ex: landing page ou login)
    }
    return true; // Permite o acesso se o usuário não estiver autenticado
  }
}
