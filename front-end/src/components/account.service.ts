import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../environments/environment';

export enum AccountType {
  CHECKING = 'CHECKING',
  SAVINGS = 'SAVINGS',
}

export interface Account {
  id?: string;
  customerId: string;
  type: AccountType;
  balance: number;
  title?: string;
}

export interface CreateAccountPayload {
  customerId: string;
  type: AccountType;
  balance?: number;
  title?: string;
}

@Injectable({
  providedIn: 'root',
})
export class AccountService {
  private apiUrl: string;

  constructor(private http: HttpClient) {
    this.apiUrl = `${environment.apiUrl}/accounts`;
  }

  getAccountsByCustomerId(customerId: string): Observable<Account[]> {
    return this.http
      .get<Account[]>(`${this.apiUrl}/customer/${customerId}`)
      .pipe(catchError(this.handleError));
  }

  createAccount(payload: CreateAccountPayload): Observable<Account> {
    return this.http
      .post<Account>(`${this.apiUrl}/create`, payload)
      .pipe(catchError(this.handleError));
  }

  deleteAccount(accountId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${accountId}`).pipe(catchError(this.handleError));
  }

  private handleError(error: HttpErrorResponse) {
    console.error('An API error occurred in AccountService', error);
    let errorMessage = 'Erro na operação da conta; por favor, tente novamente mais tarde.';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Erro: ${error.error.message}`;
    } else if (error.status === 0) {
      errorMessage = 'Não foi possível conectar à API de contas. Verifique o backend e o CORS.';
    } else {
      if (error.error && error.error.message) {
        errorMessage = error.error.message;
      } else if (typeof error.error === 'string') {
        errorMessage = error.error;
      } else {
        errorMessage = `Erro ${error.status}: ${error.statusText}`;
      }
    }
    return throwError(() => new Error(errorMessage));
  }
}
