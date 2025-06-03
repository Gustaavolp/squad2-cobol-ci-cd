import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../environments/environment';

// Enum para o tipo de transação, correspondendo ao backend
export enum TransactionType {
  DEPOSIT = 'DEPOSIT',
  WITHDRAWAL = 'WITHDRAWAL',
  TRANSFER = 'TRANSFER', // Incluído para correspondência completa com o backend, embora não usado no formulário inicial
}

// Interface para o payload de criação de transação
export interface CreateTransactionPayload {
  accountId: string; // ID da conta para a transação
  type: TransactionType;
  amount: number;
  destinationAccountId?: string; // Opcional, para transferências
}

// Interface para a resposta da transação (pode ser mais completa se necessário)
export interface Transaction {
  id: string;
  accountId: string;
  type: TransactionType;
  amount: number;
  dateTime: string; // ou Date, dependendo de como o backend formata LocalDateTime
  destinationAccountId?: string;
}

@Injectable({
  providedIn: 'root',
})
export class TransactionService {
  private apiUrl: string;

  constructor(private http: HttpClient) {
    this.apiUrl = `${environment.apiUrl}/transactions`; // API base para transações
  }

  createTransaction(payload: CreateTransactionPayload): Observable<Transaction> {
    return this.http.post<Transaction>(this.apiUrl, payload).pipe(catchError(this.handleError));
  }

  getTransactionsByAccountId(accountId: string): Observable<Transaction[]> {
    return this.http
      .get<Transaction[]>(`${this.apiUrl}/account/${accountId}`)
      .pipe(catchError(this.handleError));
  }

  private handleError(error: HttpErrorResponse) {
    console.error('An API error occurred in TransactionService', error);
    let errorMessage = 'Erro na operação de transação; por favor, tente novamente mais tarde.';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Erro: ${error.error.message}`;
    } else if (error.status === 0) {
      errorMessage = 'Não foi possível conectar à API de transações. Verifique o backend e o CORS.';
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
