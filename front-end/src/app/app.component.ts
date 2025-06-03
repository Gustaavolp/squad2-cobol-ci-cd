import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { TransactionModalComponent } from '../components/transaction-modal/transaction-modal.component';
import { TransactionService } from '../components/transaction.service';
import { TransactionModalService } from '../components/transaction-modal/transaction-modal.service';

// Importar os serviços corretos
import { CustomerService, Customer } from '../components/customer.service';
import { AccountService, Account } from '../components/account.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, TransactionModalComponent],
  template: `
    <router-outlet></router-outlet>
    <app-transaction-modal
      [visible]="modalVisible"
      [users]="customers"
      [accounts]="allAccounts"
      (close)="closeModal()"
      (submit)="handleTransactionSubmit($event)"
    >
    </app-transaction-modal>
  `,
})
export class AppComponent {
  modalVisible = false;
  customers: Customer[] = [];
  allAccounts: Account[] = [];

  constructor(
    private transactionService: TransactionService,
    private modalService: TransactionModalService,
    private customerService: CustomerService,
    private accountService: AccountService
  ) {
    this.modalService.visible$.subscribe(visible => {
      this.modalVisible = visible;
      if (visible) {
        this.loadDataForModal();
      }
    });
  }

  loadDataForModal() {
    this.customerService.getCustomers().subscribe(customers => {
      this.customers = customers;
    });

    console.warn(
      'AppComponent: Carregamento de allAccounts precisa ser revisto. AccountService não possui getAllAccounts().'
    );
    this.allAccounts = [];
  }

  closeModal() {
    this.modalService.close();
  }

  handleTransactionSubmit(transaction: any) {
    this.transactionService.createTransaction(transaction).subscribe({
      next: () => {
        this.modalService.close();
      },
      error: error => {
        console.error('Erro ao realizar transação via AppComponent', error);
      },
    });
  }
}
