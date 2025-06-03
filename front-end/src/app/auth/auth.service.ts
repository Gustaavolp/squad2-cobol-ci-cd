import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { User } from '../../types/user.type';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly loginUrl = environment.apiUrl + '/usersApi.php';
  private readonly tokenKey = 'token';
  private readonly tokenExpiration = 'tokenExpiration';

  constructor(private http: HttpClient) { }

  loggedInUser(user: User): Observable<any> {
    return this.http.post<any>(this.loginUrl, user, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    }).pipe(
      tap(response => {
        if (response && response.token) {
          this.setSession(response.token);
        }
      })
    );
  }

  allUsers(): Observable<any> {
    return this.http.get<any>(this.loginUrl);
  }

  loggedIn(): boolean {
    return this.isTokenValid();
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.tokenExpiration);
  }

  private setSession(token: string): void {
    const expiresAt = new Date();
    expiresAt.setHours(expiresAt.getHours() + 1); // Token expires in 1 hour
    
    localStorage.setItem(this.tokenKey, token);
    localStorage.setItem(this.tokenExpiration, expiresAt.toISOString());
  }

  private isTokenValid(): boolean {
    const token = this.getToken();
    const expiration = localStorage.getItem(this.tokenExpiration);
    
    if (!token || !expiration) {
      return false;
    }
    
    return new Date() < new Date(expiration);
  }
}