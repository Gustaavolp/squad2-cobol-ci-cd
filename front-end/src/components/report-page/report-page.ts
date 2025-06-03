import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '../../app/shared/material.module';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { Menu } from '../menu/menu';
import { CustomerService, Customer } from '../customer.service';
import { AccountService, Account } from '../account.service';
import { TransactionService, Transaction, TransactionType } from '../transaction.service';

// Interface local para transações exibidas na tela, com dateTime processado
interface DisplayedTransaction extends Omit<Transaction, 'dateTime'> {
  dateTime: Date | string; // Permitir Date para o pipe, string como fallback
}

@Component({
  selector: 'app-report-page',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MaterialModule,
    MatSelectModule,
    MatFormFieldModule,
    Menu,
  ],
  templateUrl: './report-page.html',
  styleUrl: './report-page.scss',
})
export class ReportPage implements OnInit {
  users: Customer[] = [];
  selectedUser: string | null = null;
  selectedAccount: string | null = null;
  filteredAccounts: (Account & { displayName?: string })[] = [];
  filteredTransactions: DisplayedTransaction[] = [];
  displayedColumns: string[] = ['dateTime', 'type', 'amount'];
  public transactionType = TransactionType;

  currentAccountBalance: number | null = null;

  constructor(
    private customerService: CustomerService,
    private accountService: AccountService,
    private transactionService: TransactionService
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.customerService.getCustomers().subscribe({
      next: data => {
        this.users = data;
      },
      error: err => {
        console.error('Erro ao carregar usuários:', err);
      },
    });
  }

  onUserChange(): void {
    this.selectedAccount = null;
    this.filteredTransactions = [];
    this.filteredAccounts = [];
    this.currentAccountBalance = null;

    if (this.selectedUser) {
      this.accountService.getAccountsByCustomerId(this.selectedUser).subscribe({
        next: data => {
          this.filteredAccounts = data.map(acc => ({
            ...acc,
            displayName: acc.title || `Conta ID: ${acc.id}`,
          }));
        },
        error: err => {
          console.error('Erro ao carregar contas:', err);
          this.filteredAccounts = [];
        },
      });
    }
  }

  onAccountChange(): void {
    this.filteredTransactions = [];
    this.currentAccountBalance = null;

    if (this.selectedAccount) {
      const accountData = this.filteredAccounts.find(acc => acc.id === this.selectedAccount);
      if (accountData) {
        this.currentAccountBalance = accountData.balance;
      }

      this.transactionService.getTransactionsByAccountId(this.selectedAccount).subscribe({
        next: (apiTransactions: Transaction[]) => {
          this.filteredTransactions = apiTransactions.map(tx => {
            let finalDateTime: Date | string = tx.dateTime;
            const sourceDateTime = tx.dateTime as any;

            if (Array.isArray(sourceDateTime) && sourceDateTime.length >= 6) {
              finalDateTime = new Date(
                sourceDateTime[0],
                sourceDateTime[1] - 1,
                sourceDateTime[2],
                sourceDateTime[3],
                sourceDateTime[4],
                sourceDateTime[5]
              );
            } else if (typeof sourceDateTime === 'string') {
              const parsedDate = new Date(sourceDateTime);
              if (!isNaN(parsedDate.valueOf())) {
                finalDateTime = parsedDate;
              }
            }
            return { ...tx, dateTime: finalDateTime };
          });
        },
        error: err => {
          console.error('Erro ao carregar transações:', err);
          this.filteredTransactions = [];
        },
      });
    }
  }

  getTotalTransactionsValue(): number {
    return this.filteredTransactions.reduce((acc, transaction) => {
      if (transaction.type === TransactionType.DEPOSIT) {
        return acc + transaction.amount;
      } else if (
        transaction.type === TransactionType.WITHDRAWAL ||
        transaction.type === TransactionType.TRANSFER
      ) {
        return acc - transaction.amount;
      }
      return acc;
    }, 0);
  }
}
