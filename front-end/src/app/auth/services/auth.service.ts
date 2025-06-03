import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap, Subject, EMPTY } from 'rxjs';
import { catchError, finalize } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

const API_URL = environment.apiUrl;

interface AuthResponse {
  token: string;
  email: string;
  name: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly TOKEN_KEY = 'auth_token';
  private readonly USER_KEY = 'auth_user';
  private currentUserSubject: BehaviorSubject<any>;
  public currentUser$: Observable<any>;

  constructor(private http: HttpClient) {
    this.currentUserSubject = new BehaviorSubject<any>(this.getUserFromStorage());
    this.currentUser$ = this.currentUserSubject.asObservable();
  }

  private getUserFromStorage(): any {
    if (typeof localStorage !== 'undefined' && typeof sessionStorage !== 'undefined') {
      let userJson = localStorage.getItem(this.USER_KEY);
      if (!userJson) {
        userJson = sessionStorage.getItem(this.USER_KEY);
      }
      if (userJson) {
        try {
          return JSON.parse(userJson);
        } catch (e) {
          console.error('Error parsing user JSON from storage:', e);
          localStorage.removeItem(this.USER_KEY);
          sessionStorage.removeItem(this.USER_KEY);
          return null;
        }
      }
      return null;
    }
    return null;
  }

  login(credentials: {
    email: string;
    password: string;
    rememberMe: boolean;
  }): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${API_URL}/auth/login`, credentials)
      .pipe(tap(response => this.handleAuthentication(response, credentials.rememberMe)));
  }

  register(userData: any): Observable<AuthResponse> {
    console.log('Registering user:', userData);
    return this.http.post<AuthResponse>(`${API_URL}/customers/create`, userData).pipe(
      tap(
        response => console.log('Registration successful:', response),
        error => console.error('Registration error:', error)
      )
    );
  }

  logout(): Observable<void> {
    const logoutSubject = new Subject<void>();

    this.http
      .post<any>(`${API_URL}/auth/logout`, {})
      .pipe(
        tap(() => console.log('Logout bem-sucedido no backend.')),
        catchError(err => {
          console.error(
            'Erro ao fazer logout no backend, limpando dados locais de qualquer maneira:',
            err
          );
          return EMPTY;
        }),
        finalize(() => {
          this.clearLocalAuthData();
          logoutSubject.next();
          logoutSubject.complete();
        })
      )
      .subscribe();

    return logoutSubject.asObservable();
  }

  private clearLocalAuthData(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    sessionStorage.removeItem(this.TOKEN_KEY);
    sessionStorage.removeItem(this.USER_KEY);
    this.currentUserSubject.next(null);
    console.log('Dados locais de autenticação limpos.');
  }

  private handleAuthentication(response: AuthResponse, rememberMe: boolean): void {
    if (rememberMe) {
      localStorage.setItem(this.TOKEN_KEY, response.token);
      localStorage.setItem(this.USER_KEY, JSON.stringify(response.email));
    } else {
      sessionStorage.setItem(this.TOKEN_KEY, response.token);
      sessionStorage.setItem(this.USER_KEY, JSON.stringify(response.email));
    }
  }

  getToken(): string | null {
    if (typeof localStorage !== 'undefined') {
      return (
        localStorage.getItem(this.TOKEN_KEY) ||
        (typeof sessionStorage !== 'undefined' ? sessionStorage.getItem(this.TOKEN_KEY) : null)
      );
    }
    return null;
  }

  isAuthenticated(): boolean {
    const token = this.getToken();
    return !!token;
  }
}
