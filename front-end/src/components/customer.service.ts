import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../environments/environment'; // Usando caminho relativo

// Interface atualizada para corresponder ao backend e formulário
export interface Customer {
  id?: string; // O backend usa Long, mas o frontend pode usar string ou number
  name: string;
  email: string;
  birthdate: string | Date; // Mantém flexibilidade, mas enviaremos como string YYYY-MM-DD
  password?: string; // O password é write-only no backend, enviado na criação/atualização
  role: string; // Ex: 'USER', 'ADMIN'
  // Campos antigos removidos: cpf, address, phone, fullName
}

@Injectable({
  providedIn: 'root',
})
export class CustomerService {
  // private storageKey = 'customers'; // Removida lógica de localStorage
  private apiUrl: string;

  constructor(private http: HttpClient) {
    // Acessa a apiUrl de environment e adiciona o path específico dos customers
    this.apiUrl = `${environment.apiUrl}/customers`;
  }

  // Método para criar um novo cliente via API
  createCustomer(customerData: Omit<Customer, 'id'>): Observable<Customer> {
    let dataToSend: any = { ...customerData };

    console.log('[CustomerService] Original birthdate from customerData:', customerData.birthdate);
    console.log('[CustomerService] Type of original birthdate:', typeof customerData.birthdate);

    if (customerData.birthdate) {
      let dateToFormat: Date;
      if (customerData.birthdate instanceof Date) {
        dateToFormat = customerData.birthdate;
        console.log('[CustomerService] birthdate is already a Date object.');
      } else if (typeof customerData.birthdate === 'string') {
        console.log('[CustomerService] birthdate is a string, attempting to parse.');
        dateToFormat = new Date(customerData.birthdate);
      } else {
        console.warn(
          '[CustomerService] birthdate is neither Date nor string. Sending as is.',
          customerData.birthdate
        );
        // Se dataToSend já foi criado com spread de customerData, e customerData.birthdate é de tipo inesperado,
        // dataToSend.birthdate já terá esse valor. Não precisa reatribuir aqui a menos que a chave em dataToSend fosse diferente.
      }

      // Só formata se dateToFormat for uma data válida
      // E garante que estamos atribuindo à chave correta 'birthdate' em dataToSend
      if (dateToFormat! && !isNaN(dateToFormat.getTime())) {
        console.log('[CustomerService] Parsed/obtained dateToFormat:', dateToFormat);
        dataToSend.birthdate = this.formatDateToYYYYMMDD(dateToFormat);
      } else if (typeof customerData.birthdate === 'string') {
        console.warn(
          '[CustomerService] Failed to parse birthdate string, sending original string:',
          customerData.birthdate
        );
        dataToSend.birthdate = customerData.birthdate; // Envia a string original se o parse falhar
      }
    } else {
      console.log('[CustomerService] birthdate is null or undefined. Sending as is.');
      // Se customerData.birthdate for null/undefined, dataToSend.birthdate já terá esse valor.
    }

    console.log('[CustomerService] Payload to API:', JSON.stringify(dataToSend));
    return this.http
      .post<Customer>(`${this.apiUrl}/create`, dataToSend)
      .pipe(catchError(this.handleError));
  }

  getCustomers(): Observable<Customer[]> {
    return this.http.get<Customer[]>(`${this.apiUrl}/all`).pipe(catchError(this.handleError));
  }

  getCustomerById(id: string): Observable<Customer> {
    return this.http.get<Customer>(`${this.apiUrl}/get/${id}`).pipe(catchError(this.handleError));
  }

  updateCustomer(id: string, customerData: Customer): Observable<Customer> {
    let dataToSend: any = { ...customerData };
    if (customerData.birthdate) {
      let dateToFormat: Date;
      if (customerData.birthdate instanceof Date) {
        dateToFormat = customerData.birthdate;
      } else if (typeof customerData.birthdate === 'string') {
        dateToFormat = new Date(customerData.birthdate);
      } else {
        dataToSend.birthdate = customerData.birthdate; /* não formata */
      }
      if (dateToFormat! && !isNaN(dateToFormat.getTime())) {
        dataToSend.birthdate = this.formatDateToYYYYMMDD(dateToFormat);
      } else if (typeof customerData.birthdate === 'string') {
        dataToSend.birthdate = customerData.birthdate; /* parse falhou, envia original */
      }
    }
    const { id: customerId, ...payload } = dataToSend;
    return this.http
      .put<Customer>(`${this.apiUrl}/update/${id}`, payload)
      .pipe(catchError(this.handleError));
  }

  deleteCustomer(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/delete/${id}`).pipe(catchError(this.handleError));
  }

  private formatDateToYYYYMMDD(date: Date): string {
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  private handleError(error: HttpErrorResponse) {
    console.error('An API error occurred', error);
    // Tenta extrair mensagens de erro do corpo da resposta do backend, se disponíveis
    let errorMessage = 'Algo deu errado; por favor, tente novamente mais tarde.';
    if (error.error instanceof ErrorEvent) {
      // Erro do lado do cliente ou de rede
      console.error('Client-side error:', error.error.message);
      errorMessage = `Erro: ${error.error.message}`;
    } else if (error.status === 0) {
      // Isso pode indicar um problema de CORS ou que o backend não está respondendo.
      console.error('API não respondeu. Verifique o backend e a configuração de CORS.');
      errorMessage = 'Não foi possível conectar à API. Verifique o backend e o CORS.';
    } else {
      // O backend retornou um código de erro (4xx, 5xx)
      // O corpo da resposta pode conter pistas sobre o que deu errado
      console.error(
        `Backend returned code ${error.status}, ` + `body was: ${JSON.stringify(error.error)}`
      );
      // Tenta usar mensagens de erro específicas da API, se disponíveis
      if (error.error && typeof error.error === 'object') {
        if (error.error.message) errorMessage = error.error.message;
        else if (error.error.error)
          errorMessage = error.error.error; // Comum em alguns frameworks
        // Adicione outras verificações para campos de erro comuns da sua API
        else if (Array.isArray(error.error.errors) && error.error.errors.length > 0) {
          // Para erros de validação do Spring Boot
          errorMessage = error.error.errors
            .map((e: any) => e.defaultMessage || e.message)
            .join(', ');
        } else {
          errorMessage = `Erro ${error.status}: ${error.statusText}`;
        }
      } else if (typeof error.error === 'string') {
        errorMessage = error.error;
      }
    }
    return throwError(() => new Error(errorMessage));
  }
}
